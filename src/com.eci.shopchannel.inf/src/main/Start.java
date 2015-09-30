package main;

import manage.CacheManage;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import servlet.DefaultServlet;
import servlet.LoginServlet;
import servlet.ManageServlet;
import servlet.QueryServlet;
import servlet.RandomCodeServlet;
import utils.ServerUtils;

public class Start {
	
	static Logger logger = Logger.getLogger(Start.class);

	public static String IP = "";
	public static int PORT = 8008;
	public static int TIME_OUT_S = 1800;

	public static void main(String[] args) throws Exception {
		logger.info("Server is starting...");
		init();
		logger.info("cache init success...");
		Server server = new Server(PORT);
		ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/");
		
		//设置servlet
		servletContextHandler.addServlet(QueryServlet.class, "/query");
		servletContextHandler.addServlet(LoginServlet.class, "/login");
		servletContextHandler.addServlet(RandomCodeServlet.class, "/randomcode");
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
			CacheManage.initCache();
		} catch (Exception e) {
			logger.fatal("cache init error!", e);
			System.exit(1);
		}
		
	}
}
