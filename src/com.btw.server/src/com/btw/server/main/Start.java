package com.btw.server.main;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.btw.server.core.CacheManage;
import com.btw.server.core.DatabaseManage;
import com.btw.server.core.PropertiesManage;
import com.btw.server.self.SelfErrorHandler;
import com.btw.server.self.SelfResourceHandler;
import com.btw.server.servlet.func.ServerIpListServlet;
import com.btw.server.servlet.func.SmsListServlet;
import com.btw.server.servlet.index.IndexServlet;
import com.btw.server.servlet.index.LoginServlet;
import com.btw.server.servlet.index.MenuServlet;
import com.btw.server.servlet.intf.IpServlet;
import com.btw.server.servlet.intf.IpSyncServlet;
import com.btw.server.servlet.pub.RandomCodeServlet;
import com.btw.server.task.AutoSmsSendTask;
import com.btw.server.util.ServerUtils;

public class Start {
	
	private final static Logger logger = Logger.getLogger(Start.class);

	private static int PORT = 80;
	private static int TIME_OUT_S = 1800;
	private static int INIT_THREADS = 20;
	private static int MAX_THREADS = 100;

	public static void main(String[] args) throws Exception {
		
		logger.info("Server is starting...");
		init();
		startTask();
		
		Server server = new Server(PORT);
		//设置线程池
		server.addBean(new QueuedThreadPool(MAX_THREADS, INIT_THREADS));
		//设置错误界面
		server.addBean(new SelfErrorHandler(false));

		//设置资源管理 - 使用自定义资源处理器
		ResourceHandler resourceHandler = new SelfResourceHandler();
//		//resourceHandler.setDirectoriesListed(true);  //会显示一个列表
//		//resourceHandler.setWelcomeFiles(new String[]{"index.html"});
		resourceHandler.setResourceBase("resource");
		
		//设置servlet
		ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
		
		//index
	    servletContextHandler.addServlet(IndexServlet.class, "");
	    servletContextHandler.addServlet(IndexServlet.class, "/index");
	    servletContextHandler.addServlet(LoginServlet.class, "/login");
	    servletContextHandler.addServlet(MenuServlet.class, "/menu");
		
		//func
	    servletContextHandler.addServlet(ServerIpListServlet.class, "/serveriplist");
	    servletContextHandler.addServlet(SmsListServlet.class, "/smslist");
		
		//intf
	    servletContextHandler.addServlet(IpSyncServlet.class, "/ip-sync");
	    servletContextHandler.addServlet(IpServlet.class, "/ip");
	    
		//pub
	    servletContextHandler.addServlet(RandomCodeServlet.class, "/randomcode");
//	    servletContextHandler.addServlet(ErrorServlet.class, "/");
		
		//设置session管理
	    servletContextHandler.getSessionHandler().getSessionManager().setMaxInactiveInterval(TIME_OUT_S);;
		
		//只要有一个Handler将请求标记为已处理，或抛出异常，Handler的调用就到此结束。
		HandlerList handlerList = new HandlerList();
		handlerList.setHandlers(new Handler[] { resourceHandler, servletContextHandler });
		server.setHandler(handlerList);
		
		//不会结束，一直调用到最后一个Handler。注意人工避免冲突
//		HandlerCollection handlerCollection = new HandlerCollection();
//		handlerCollection.setHandlers(new Handler[] { resourceHandler, servletContextHandler });
//		server.setHandler(handlerCollection);
		try {
			server.start();
		} catch (Exception e) {
			logger.fatal("server start failed!", e);
			System.exit(1);
		}
		logger.info("Server is started at "+ ServerUtils.getIp() + ":" + PORT);
		
		server.join();
		

	}

	private static void startTask() {
		
		new AutoSmsSendTask().start();
	}

	private static void init() {
		initConfigue();
		initDatabase();
		initCache();
	}
	
	private static void initConfigue(){
			PORT = Integer.valueOf(PropertiesManage.getProperties(PropertiesManage.SERVER_PORT));
			TIME_OUT_S = Integer.valueOf(PropertiesManage.getProperties(PropertiesManage.SERVER_TIME_OUT_S));
			INIT_THREADS = Integer.valueOf(PropertiesManage.getProperties(PropertiesManage.SERVER_INIT_THREADS));
			MAX_THREADS = Integer.valueOf(PropertiesManage.getProperties(PropertiesManage.SERVER_MAX_THREADS));
	}
	
	private static void initDatabase(){
		try {
			DatabaseManage.queryOne(Object.class, "select 1");
		} catch (Exception e) {
			logger.fatal("database init failed!", e);
			System.exit(1);
		}
	}
	
	private static void initCache(){
		try {
			CacheManage.get("init");
		} catch (Exception e) {
			logger.fatal("cache init failed!", e);
			System.exit(1);
		}
	}
}
