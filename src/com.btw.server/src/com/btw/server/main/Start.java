package com.btw.server.main;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.btw.server.constant.Constants;
import com.btw.server.core.CachePool;
import com.btw.server.core.DatabaseManage;
import com.btw.server.core.PropertiesManage;
import com.btw.server.servlet.func.BlackIpManageServlet;
import com.btw.server.servlet.func.ServerIpListServlet;
import com.btw.server.servlet.index.DefaultServlet;
import com.btw.server.servlet.index.IndexServlet;
import com.btw.server.servlet.index.LoginServlet;
import com.btw.server.servlet.index.MenuServlet;
import com.btw.server.servlet.intf.IpServlet;
import com.btw.server.servlet.intf.IpSyncServlet;
import com.btw.server.servlet.pub.RandomCodeServlet;
import com.btw.server.servlet.pub.ResourceServlet;
import com.btw.server.util.ServerUtils;

public class Start {
	
	static Logger logger = Logger.getLogger(Start.class);

	public static String IP = "";
	public static int PORT = 80;
	public static int TIME_OUT_S = 1800;
	public static int INIT_THREADS = 20;
	public static int MAX_THREADS = 100;
	public static int MAX_QUEUED = 300;

	public static void main(String[] args) throws Exception {
		logger.info("Server is starting...");
		init();
		
		Server server = new Server(PORT);
		ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/");
		//设置线程池
		QueuedThreadPool threadPool = new QueuedThreadPool(MAX_THREADS);
		threadPool.setMinThreads(INIT_THREADS);
		threadPool.setMaxQueued(MAX_QUEUED);
		server.setThreadPool(threadPool);
		//设置servlet
		
		//index
	    servletContextHandler.addServlet(LoginServlet.class, "/login");
	    servletContextHandler.addServlet(IndexServlet.class, "/index");
	    servletContextHandler.addServlet(MenuServlet.class, "/menu");
	    servletContextHandler.addServlet(IndexServlet.class, "/index");
		
		
		//func
	    servletContextHandler.addServlet(ServerIpListServlet.class, "/serveriplist");
	    servletContextHandler.addServlet(BlackIpManageServlet.class, "/blackipmanage");
		
		//intf
	    servletContextHandler.addServlet(IpSyncServlet.class, "/ip-sync");
	    servletContextHandler.addServlet(IpServlet.class, "/ip");
		
		
		//pub
	    servletContextHandler.addServlet(RandomCodeServlet.class, "/randomcode");
		servletContextHandler.addServlet(ResourceServlet.class, "/resource/*");
	    servletContextHandler.addServlet(DefaultServlet.class, "/*");
		
		//设置session管理
		HashSessionManager hashSessionManager = new HashSessionManager();
		hashSessionManager.setMaxInactiveInterval(TIME_OUT_S);
		servletContextHandler.setSessionHandler(new SessionHandler(hashSessionManager));

		//设置
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(".");
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { servletContextHandler, resourceHandler });
		server.setHandler(handlers);
		
		server.start();
		logger.info("Server is started at "+ ServerUtils.getIp() + ":" + PORT);
		
		server.join();
		

	}

	private static void init() {
		initConfigue();
		initDatabase();
		initCache();
	}
	
	private static void initConfigue(){
		try {
			PropertiesManage.initProperties();
			PORT = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_PORT));
			TIME_OUT_S = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_TIME_OUT_S));
			INIT_THREADS = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_INIT_THREADS));
			MAX_THREADS = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_MAX_THREADS));
			MAX_QUEUED = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_MAX_QUEUED));
		} catch (Exception e) {
			logger.fatal("properties init failed!", e);
			System.exit(1);
		}
	}
	
	private static void initDatabase(){
		try {
			DatabaseManage.initDatabase();
		} catch (Exception e) {
			logger.fatal("database init failed!", e);
			System.exit(1);
		}
	}
	
	private static void initCache(){
		try {
			CachePool.getInstance();
		} catch (Exception e) {
			logger.fatal("cache init failed!", e);
			System.exit(1);
		}
	}
}
