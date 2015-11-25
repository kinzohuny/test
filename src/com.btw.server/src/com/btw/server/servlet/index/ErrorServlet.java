package com.btw.server.servlet.index;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		resp.getWriter().println("<title>Error</title>");
		resp.getWriter().println("<html>");
		resp.getWriter().println("<head><title>Error</title></head>");
		resp.getWriter().println("<body bgcolor=\"black\">");
		resp.getWriter().println("<center><h1><font color=\"white\">Whoops, looks like something went wrong.<font></h1></center>");
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
