package main;

import manage.CacheManage;
import manage.Constants;
import manage.PropertiesManage;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import servlet.DefaultServlet;
import servlet.DownloadServlet;
import servlet.LoginServlet;
import servlet.ManageServlet;
import servlet.QueryServlet;
import servlet.RandomCodeServlet;
import servlet.ResourceServlet;
import utils.ServerUtils;

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
		servletContextHandler.addServlet(QueryServlet.class, "/query");
		servletContextHandler.addServlet(LoginServlet.class, "/login");
		servletContextHandler.addServlet(RandomCodeServlet.class, "/randomcode");
		servletContextHandler.addServlet(DownloadServlet.class, "/download/*");
		servletContextHandler.addServlet(ResourceServlet.class, "/resource/*");
		servletContextHandler.addServlet(ManageServlet.class, "/manage");
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
		server.join();
		logger.info("Server is started at "+ ServerUtils.getIp() + ":" + PORT);
	}

	private static void init() {
		try {
			PropertiesManage.initProperties();
			logger.info("properties init success...");
		} catch (Exception e) {
			logger.fatal("properties init error!", e);
			System.exit(1);
		}

		PORT = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_PORT));
		TIME_OUT_S = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_TIME_OUT_S));
		INIT_THREADS = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_INIT_THREADS));
		MAX_THREADS = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_MAX_THREADS));
		MAX_QUEUED = Integer.valueOf(PropertiesManage.getProperties(Constants.PROPERTIES_SERVER_MAX_QUEUED));
				
		try {
			CacheManage.initCache();
			logger.info("cache init success...");
		} catch (Exception e) {
			logger.fatal("cache init error!", e);
			System.exit(1);
		}
	}
}
