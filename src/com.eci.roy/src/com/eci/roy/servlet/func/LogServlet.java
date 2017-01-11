package com.eci.roy.servlet.func;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.eci.roy.constant.Constants;
import com.eci.roy.dao.LogDao;
import com.eci.roy.dao.MachineDao;
import com.eci.roy.model.LogModel;

public class LogServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(LogServlet.class);

	String msg = "";
	String title = "";
	String page = "";
	MachineDao shopDao = new MachineDao();
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			try {
				title = "操作记录";
				page = defaultHandle(req);
			} catch (Exception e) {
				title = "Exception";
				page = e.getMessage();
				StringBuffer buffer = new StringBuffer("Request args:");
				for(Object key : req.getParameterMap().keySet()){
					buffer.append("&" + key + "=" + toString(req.getParameterMap().get(key)));
				}
				logger.error(buffer.toString(), e);
			}
			
			resp.setContentType("text/html;charset=utf-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("<title>"+title+"</title>");
			resp.getWriter().println("<link href=\"/resource/main.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
			resp.getWriter().println("<script src=\"/resource/main.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<script src=\"/resource/jquery-1.11.1.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<body>操作记录<br>"+page+"</body>");
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private String defaultHandle(HttpServletRequest req) throws SQLException {
		List<LogModel> list = LogDao.queryLogByUserId((Long)req.getSession().getAttribute(Constants.SESSION_USER_ID));
		return getButtonPage()+getListPage(list);
	}

	private String getButtonPage(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"menutable\"><tr>");
		//buffer.append("<td><input type=\"button\" value=\"删除\" onclick=\"deleteSelected();\"></td>");
		buffer.append("</tr></table>");
		
		return buffer.toString();
	}
	
	private String getListPage(List<LogModel> list){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"gridtable\">");
		buffer.append("最近"+list.size()+"条操作记录");
		buffer.append("<tr>")
			.append("<th>id</th>")
			.append("<th>time</th>")
			.append("<th>content</th>")
			.append("</tr>");

		for(LogModel model : list){
			buffer.append("<tr>");
			buffer.append("<td>").append(model.getId()).append("</td>");
			buffer.append("<td>").append(model.getTimeStr()).append("</td>");
			buffer.append("<td>").append(model.getContent()).append("</td>");
			buffer.append("</tr>");
		}

		buffer.append("</table>");
		return String.valueOf(buffer.toString());
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
