package com.eci.youku.servlet.index;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eci.youku.constant.Constants;

public class MenuServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			resp.setContentType("text/html;charset=utf-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("<body>"+getMenu()+"</body>");
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private String getMenu(){
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("<a target=\"body\" href=\"shop\">店铺管理</a> &nbsp;&nbsp;");
		stringBuilder.append("<a target=\"body\" href=\"item\">商品管理</a> &nbsp;&nbsp;");
		stringBuilder.append("<a target=\"body\" href=\"youkuvipverify\">会员权益审核</a> &nbsp;&nbsp;");
		stringBuilder.append("<a target=\"body\" href=\"viprec\">权益发放记录</a> &nbsp;&nbsp;");
		stringBuilder.append("<a target=\"body\" href=\"youkuvipmanual\">人工发放权益</a> &nbsp;&nbsp;");
		stringBuilder.append("<a target=\"_top\" href=\"login?logout=true\">注销</a> &nbsp;&nbsp;");
		
		return stringBuilder.toString();
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
}
