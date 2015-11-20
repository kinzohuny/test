package com.eci.youku.servlet.func;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.eci.youku.constant.Constants;
import com.eci.youku.dao.ValidTradeDao;
import com.eci.youku.dao.VipRecDao;
import com.eci.youku.model.VipRecModel;

public class VipRecServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;
	private static final Logger logger = Logger.getLogger(VipRecServlet.class);

	String msg = "";
	String title = "";
	String page = "";
	ValidTradeDao validTradeDao = new ValidTradeDao();
	VipRecDao vipRecDao = new VipRecDao();
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			try {
				title = "权益发放记录";
				page = defaultHandle(req);
			} catch (Exception e) {
				title = "Exception";
				page = e.getMessage();
				logger.error(e.getMessage(), e);
			}
			
			resp.setContentType("text/html;charset=utf-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("<title>"+title+"</title>");
			resp.getWriter().println("<link href=\"/resource/main.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
			resp.getWriter().println("<script src=\"/resource/main.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<script src=\"/resource/jquery-1.11.1.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<body>"+page+"</body>");
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private String defaultHandle(HttpServletRequest req) throws SQLException {

		List<VipRecModel> list = vipRecDao.queryList();
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
	
	private String getListPage(List<VipRecModel> list){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"gridtable\">");
		buffer.append("权益发放：共"+list.size()+"条记录");
		buffer.append("<tr>")
			.append("<th>手机</th>")
			.append("<th>会员类型</th>")
			.append("<th>提交时间</th>")
			.append("<th>发放状态</th>")
			.append("<th>发放时间</th>")
			.append("<th>发放类型</th>")
			.append("<th>短信状态</th>")
			.append("<th>短信时间</th></tr>");
		
		for(VipRecModel model : list){
			buffer.append("<tr>");
			buffer.append("<td>").append(model.getMobile()).append("</td>");
			buffer.append("<td>").append(model.getVip_type()).append("</td>");
			buffer.append("<td>").append(model.getCreated()).append("</td>");
			buffer.append("<td>").append(model.getVip_status()).append("</td>");
			buffer.append("<td>").append(model.getVip_time()==null?"":model.getVip_time()).append("</td>");
			buffer.append("<td>").append(model.getIsManual()!=null&&model.getIsManual()==1?"人工":"自动").append("</td>");
			buffer.append("<td>").append(model.getSms_status()==null?"0":model.getSms_status()).append("</td>");
			buffer.append("<td>").append(model.getSms_time()==null?"":model.getSms_time()).append("</td>");
			buffer.append("</tr>");
		}

		buffer.append("</table>");
		return String.valueOf(buffer.toString());
	} 

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
}
