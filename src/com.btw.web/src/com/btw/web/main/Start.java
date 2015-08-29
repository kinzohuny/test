package com.btw.web.main;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.btw.web.core.servlet.IndexServlet;
import com.btw.web.core.servlet.TestServlet;
import com.btw.web.utils.ServerUtils;

public class Start {
	
	static Logger logger = Logger.getLogger(Start.class);

	public static String IP = "";
	public static int PORT = 80;
	public static int TIME_OUT_S = 1800;

	public static void main(String[] args) throws Exception {

		Server server = new Server(PORT);
		ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/");
		
		//设置servlet
		servletContextHandler.addServlet(TestServlet.class, "/test");
		servletContextHandler.addServlet(IndexServlet.class, "/*");
		
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
}
