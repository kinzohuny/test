package com.btw.web.core.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DefaultServlet extends HttpServlet {

	private static final long serialVersionUID = 7517307146166408300L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().println("<h1>Hello World</h1>");
		HttpSession s = req.getSession();
		resp.getWriter().println("<h1>Session is:" + s.getId() + "</h1>");
		for(Object key : req.getParameterMap().keySet()){
			resp.getWriter().print("<br/>" + key + "=" + toString(req.getParameterMap().get(key)));
		}
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	private String toString(Object obj){
		StringBuffer sb = new StringBuffer();
		if(obj instanceof String[]){
			String[] array = (String[]) obj;
			String separator = "";
			for(String str : array){
				sb.append(separator).append(str);
				separator = ",";
			}
		}
		return sb.toString();
	}
}
