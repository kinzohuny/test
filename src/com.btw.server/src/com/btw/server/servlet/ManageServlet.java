package com.btw.server.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.btw.server.dao.PcDao;
import com.btw.server.model.PcModel;

public class ManageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ManageServlet.class);

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	    HttpSession session = req.getSession();
	    if (session.getAttribute("SESSION_IS_LOGIN") == null) {
	      resp.sendRedirect("/login");
	    } else {
	      resp.setContentType("text/html;charset=utf-8");
	      resp.setStatus(200);
	      resp.getWriter().println("<title>IP LIST</title>");
	      List<PcModel> list = null;
	      try {
	        list = PcDao.queryForList(null);
	      } catch (Exception e) {
	        logger.error("query ip list error!", e);
	      }
	      if ((list != null) && (!list.isEmpty())) {
	        StringBuffer buffer = new StringBuffer();
	        resp.getWriter().println("<table border=\"1\" bordercolor=\"#a0c6e5\" style=\"border-collapse:collapse\">");
	        resp.getWriter().println("<tr><th>id</th><th>name</th><th>ip</th><th>created</th><th>updated</th></tr>");
	        for (PcModel model : list) {
	          buffer.setLength(0);
	          buffer.append("<tr>");
	          buffer.append("<td>").append(model.getId()).append("</td>");
	          buffer.append("<td>").append(model.getServer_name()).append("</td>");
	          buffer.append("<td>").append(model.getServer_ip()).append("</td>");
	          buffer.append("<td>").append(model.getCreated()).append("</td>");
	          buffer.append("<td>").append(model.getUpdated()).append("</td>");
	          buffer.append("</tr>");
	          resp.getWriter().println(buffer.toString());
	        }
	        resp.getWriter().println("</table>");
	      } else {
	        resp.getWriter().print("");
	      }

	      resp.getWriter().flush();
	      resp.getWriter().close();
	    }
	
	}
}
