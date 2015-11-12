package com.eci.youku.main;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.eci.youku.constant.Constants;
import com.eci.youku.core.CacheManage;
import com.eci.youku.core.DatabaseManage;
import com.eci.youku.core.PropertiesManage;
import com.eci.youku.servlet.DefaultServlet;
import com.eci.youku.servlet.DownloadServlet;
import com.eci.youku.servlet.ImportServlet;
import com.eci.youku.servlet.ItemListServlet;
import com.eci.youku.servlet.LoginServlet;
import com.eci.youku.servlet.RandomCodeServlet;
import com.eci.youku.servlet.ResourceServlet;
import com.eci.youku.servlet.ShopListServlet;
import com.eci.youku.util.ServerUtils;

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
		servletContextHandler.addServlet(RandomCodeServlet.class, "/randomcode");
		servletContextHandler.addServlet(ResourceServlet.class, "/resource/*");
		servletContextHandler.addServlet(DownloadServlet.class, "/download/*");
		
		servletContextHandler.addServlet(LoginServlet.class, "/login");
		servletContextHandler.addServlet(ShopListServlet.class, "/shop");
		servletContextHandler.addServlet(ItemListServlet.class, "/item");
		servletContextHandler.addServlet(ImportServlet.class, "/import");
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
			CacheManage.initCache();
		} catch (Exception e) {
			logger.fatal("cache init failed!", e);
			System.exit(1);
		}
	}
}
