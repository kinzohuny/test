package com.btw.server.servlet.func;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.btw.server.core.CachePool;
import com.btw.server.servlet.index.LoginServlet;
import com.btw.server.util.DateUtils;

public class BlackIpManageServlet extends HttpServlet{

	private static final long serialVersionUID = -401009223991790886L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	    HttpSession session = req.getSession();
	    if (session.getAttribute("SESSION_IS_LOGIN") == null) {
	      resp.sendRedirect("/login");
	    } else {
	      resp.setContentType("text/html;charset=utf-8");
	      resp.setStatus(200);
	      resp.getWriter().println("<title>Cache LIST</title>");

	      if("true".equals(req.getParameter("clean"))){
	    	  CachePool.getInstance().add(LoginServlet.BLACK_IP_MAP, new HashMap<String, Long>());
	      }
	      
	      resp.getWriter().println(getButton());	
	      resp.getWriter().println(getPage());

	      resp.getWriter().flush();
	      resp.getWriter().close();
	    }
	
	}
	
	private String getButton() {
		
		return "<form action=\"\" method=\"post\"><input type=\"hidden\" name=\"clean\" value=\"true\"><input type=\"submit\" value=\"清空\"></form>";
	}

	private String getPage(){
        StringBuffer buffer = new StringBuffer();
        
        @SuppressWarnings("unchecked")
		Map<String, Long> blackIpMap = (Map<String,Long>)CachePool.getInstance().get(LoginServlet.BLACK_IP_MAP);
        
        buffer.append("<table border=\"1\" bordercolor=\"#a0c6e5\" style=\"border-collapse:collapse\">");
        buffer.append("<tr><th>key</th><th>value</th></tr>");
        for (String ip : blackIpMap.keySet()) {
          buffer.append("<tr>");
          buffer.append("<td>").append(ip).append("</td>");
          buffer.append("<td>").append(DateUtils.format(new Date(blackIpMap.get(ip)))).append("</td>");
          buffer.append("</tr>");
        }
        buffer.append("</table>");
        
        return buffer.toString();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
}
