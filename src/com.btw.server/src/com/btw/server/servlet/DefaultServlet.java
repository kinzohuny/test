package com.btw.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		resp.getWriter().println("<title>404 Not Found</title>");
		resp.getWriter().println("<html>");
		resp.getWriter().println("<head><title>404 Not Found</title></head>");
		resp.getWriter().println("<body bgcolor=\"white\">");
		resp.getWriter().println("<center><h1>404 Not Found</h1></center>");
		resp.getWriter().println("<hr>");
		resp.getWriter().println("</body>");
		resp.getWriter().println("</html>");
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
}
