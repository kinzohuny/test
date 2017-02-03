package com.btw.server.servlet.func;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.btw.server.constant.Constants;
import com.btw.server.dao.SmsRecDao;
import com.btw.server.model.SmsRecModel;


public class SmsListServlet extends HttpServlet{

	private static final long serialVersionUID = -2120120288485213236L;
	private static final Logger logger = Logger.getLogger(SmsListServlet.class);

	String msg = "";
	String title = "";
	String page = "";
	SmsRecDao smsRecDao = new SmsRecDao();
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			try {
				title = "短信记录";
				page = defaultHandle(req);
			} catch (Exception e) {
				title = "Exception";
				page = e.getMessage();
				logger.error(e.getMessage(), e);
			}
			
			resp.setContentType("text/html;charset=utf-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("<title>"+title+"</title>");
			resp.getWriter().println("<link href=\"/css/main.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
			resp.getWriter().println("<script src=\"/js/main.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<script src=\"/js/jquery-1.11.1.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<body>"+page+"</body>");
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private String defaultHandle(HttpServletRequest req) throws SQLException {

		List<SmsRecModel> list = smsRecDao.queryList();
		return getButtonPage()+getFilterPage(req)+getListPage(list);
	}
	
	private String getButtonPage(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"menutable\"><tr>");
		buffer.append("</tr></table>");
		
		return buffer.toString();
	}
	
	private String getFilterPage(HttpServletRequest req){
		StringBuffer buffer = new StringBuffer();
		//暂不放置查询条件
//		buffer.append("<table class=\"menutable\"><tr>");
//		buffer.append("<form action=\"\" method=\"post\">");
//		buffer.append("<td><input type=\"submit\" value=\"查询\"></td>");
//		buffer.append("<td><input type=\"button\" value=\"清空\" onclick=\"clearFilter();\"></td>");
//		buffer.append("</from>");
//		buffer.append("</tr></table>");
		return buffer.toString();
	}
	
	private String getListPage(List<SmsRecModel> list){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"gridtable\">");
		buffer.append("短信：共"+list.size()+"条记录");
		buffer.append("<tr>")
			.append("<th>手机</th>")
			.append("<th>模板</th>")
			.append("<th>参数</th>")
			.append("<th>状态</th>")
			.append("<th>提交时间</th>")
			.append("<th>发送时间</th>")
			.append("<th>更新时间</th>")
			.append("</tr>");
		
		for(SmsRecModel model : list){
			buffer.append("<tr>");
			buffer.append("<td>").append(model.getMobile()).append("</td>");
			buffer.append("<td>").append(model.getTid()).append("</td>");
			buffer.append("<td>").append(model.getArgs()).append("</td>");
			buffer.append("<td>").append(getStatusStr(model.getStatus())).append("</td>");
			buffer.append("<td>").append(model.getCreated()).append("</td>");
			buffer.append("<td>").append(model.getSend_time()).append("</td>");
			buffer.append("<td>").append(model.getUpdated()).append("</td>");
			buffer.append("</tr>");
		}

		buffer.append("</table>");
		return String.valueOf(buffer.toString());
	} 
	
	private String getStatusStr(Integer status){
		switch (status) {
		case 0:
			return "未处理";
		case 1:
			return "成功";
		case 2:
			return "失败";
		case 3:
			return "异常";
		case 9:
			return "发送中";
		default:
			return "";
		}
		
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
}
