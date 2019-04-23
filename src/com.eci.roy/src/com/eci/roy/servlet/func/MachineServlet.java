package com.eci.roy.servlet.func;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
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
import com.eci.roy.model.MachineModel;
import com.eci.roy.util.MagicUtils;
import com.eci.roy.util.StringUtils;

public class MachineServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(MachineServlet.class);

	String msg = "";
	String title = "";
	String page = "";
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			try {
				if(StringUtils.isNotEmpty(req.getParameter("wakeup"))){
					title = "开机";
					page = wakeupHandle(req);
				}else{
					title = "机器列表";
					page = defaultHandle(req);
				}
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
			resp.getWriter().println("<body>机器管理<br>"+page+"</body>");
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private String wakeupHandle(HttpServletRequest req) throws SQLException, IOException, InterruptedException {
		StringBuffer buffer = new StringBuffer();
		List<MachineModel> list = MachineDao.queryMachineByUserId((Long)req.getSession().getAttribute(Constants.SESSION_USER_ID));
		for(MachineModel model : list){
			LogDao.logInsert(Calendar.getInstance().getTime(), (Long)req.getSession().getAttribute(Constants.SESSION_USER_ID), "启动"+model.getMac());
			MagicUtils.lanWake(model.getMac(), Constants.WAKEUP_MASK_IP, Constants.WAKEUP_PORT);
		}
		buffer.append("请求成功，已经尝试开启您的机器。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到列表界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/machine');</script>");
		return buffer.toString();
	}

	private String defaultHandle(HttpServletRequest req) throws SQLException {
		List<MachineModel> list = MachineDao.queryMachineByUserId((Long)req.getSession().getAttribute(Constants.SESSION_USER_ID));
		return getButtonPage()+getListPage(list);
	}
	
	private String getButtonPage(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"menutable\"><tr>");
		buffer.append("<td><input type=\"button\" value=\"开机\" onclick=\"wakeupAll();\"></td>");
		buffer.append("</tr></table>");
		
		return buffer.toString();
	}
	
	private String getListPage(List<MachineModel> list){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"gridtable\">");
		buffer.append("您拥有"+list.size()+"台机器");
		buffer.append("<tr>")
			.append("<th>ID</th>")
			.append("<th>MAC</th>")
			.append("</tr>");

		for(MachineModel model : list){
			buffer.append("<tr>");
			buffer.append("<td>").append(model.getId()).append("</td>");
			buffer.append("<td>").append(model.getMac()).append("</td>");
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
