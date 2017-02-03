package com.btw.server.servlet.index;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.btw.server.constant.Constants;

public class IndexServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			resp.setContentType("text/html;charset=utf-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println(getHead());
			resp.getWriter().println(getBody());
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private String getHead(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<head>");
		
		buffer.append("<title>后台管理</title>");
		
		//网站图标
//		buffer.append("<link rel=\"icon\" href=\"/image/favicon.ico\" mce_href=\"/image/favicon.ico\" type=\"image/x-icon\">");
//		buffer.append("<link rel=\"shortcut icon\" href=\"/image/favicon.ico\" mce_href=\"/image/favicon.ico\" type=\"image/x-icon\">");
		
		//网站动态图标
		buffer.append("<link rel=\"shortcut icon\" href=\"/image/favicon.ico\" >");
		buffer.append("<link rel=\"icon\" href=\"/image/favicon.gif\" type=\"image/gif\" >");
		
		buffer.append("</head>");
		return buffer.toString();
	}
	
	private String getBody(){
		StringBuilder buffer = new StringBuilder();
		buffer.append("<frameset rows=\"30,*\">");
		
		//菜单
		buffer.append("<frame name=\"head\" id=\"frame_head\" src=\"menu\" noresize=\"noresize\" frameborder=\"0\">");
		//内容
		buffer.append("<frame name=\"body\" id=\"frame_body\" src=\"\" noresize=\"noresize\" frameborder=\"0\">");

		buffer.append("	</frameset>");

		
		return buffer.toString();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
}
