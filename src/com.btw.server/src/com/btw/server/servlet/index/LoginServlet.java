package com.btw.server.servlet.index;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.btw.server.constant.Constants;
import com.btw.server.core.CacheManage;
import com.btw.server.dao.ManagerDao;
import com.btw.server.util.ServerUtils;
import com.btw.server.util.StringUtils;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;
	
	private static final Logger logger = Logger.getLogger(LoginServlet.class);

	String msg;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession();
		msg = "";

		String random = req.getParameter("random");
		String name = req.getParameter("name");
		String password = req.getParameter("password");
		
		if(StringUtils.isNotEmpty(random)&&StringUtils.isNotEmpty(name)&&StringUtils.isNotEmpty(password)){
			if(verifyLogin(req, random, name, password)){
				session.setAttribute(Constants.SESSION_IS_LOGIN, true);
			}
		}else if(!"nullnullnull".equals(random+name+password)){
			msg = "登录信息不完整！";
		}
		
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
//			req.getRequestDispatcher("/view/Login.html").forward(req, resp);
			
			resp.setContentType("text/html;charset=utf-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("<title>Login</title>");
			resp.getWriter().println("<link rel=\"icon\" href=\"/image/favicon.ico\" mce_href=\"/image/favicon.ico\" type=\"image/x-icon\">");
			resp.getWriter().println("<link rel=\"shortcut icon\" href=\"/image/favicon.ico\" mce_href=\"/image/favicon.ico\" type=\"image/x-icon\">");
			resp.getWriter().println("<link href=\"/css/main.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
			resp.getWriter().println("<script src=\"/js/md5.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<script src=\"/js/jquery-1.11.1.js\" type=\"text/javascript\"></script>");
			//body
			resp.getWriter().println("<form target=\"_top\" id=\"login\" method=\"post\" onsubmit=\"document.getElementById('password').value=hex_md5(document.getElementById('password').value);\">");
			resp.getWriter().println("<span>账　号：</span><input name=\"name\"></input><br>");
			resp.getWriter().println("<span>密　码：</span><input id=\"password\" type=\"password\" name=\"password\"></input><br>");
			resp.getWriter().println("<span>验证码：</span><input name=\"random\"></input><br>");
			resp.getWriter().println("<img src=\"/randomcode\" onclick=\"this.src='/randomcode?temp='+Math.random();\" />");
			resp.getWriter().println("<input type=\"submit\" value=\"登录\" />");
			resp.getWriter().println("</form>");
			resp.getWriter().println("<em class=\"error\">"+msg+"</em>");
			
			resp.getWriter().flush();
			resp.getWriter().close();
		}else if(StringUtils.isNotEmpty(req.getParameter("logout"))){
			session.setAttribute(Constants.SESSION_IS_LOGIN, null);
			resp.sendRedirect("/login");
		}else{
			resp.sendRedirect("/index");
		}
		
	}
	
	private boolean verifyLogin(HttpServletRequest req, String random,
			String name, String password) {
		if(!random.equalsIgnoreCase((String)req.getSession().getAttribute(Constants.SESSION_RANDOM))){
			msg = "验证码不正确！";
			return false;
		}

		String ip = ServerUtils.getRemoteIp(req);
		if(isForbid(ip)){
			msg = "该IP禁止登录！";
			return false;
		}
		
		long user_num = 0;
		try {
			user_num = ManagerDao.verify(name, password);
		} catch (Exception e) {
			logger.error("login verify error!", e);
			msg = "未知的系统异常，请联系管理员！";
			return false;
		}
		
		if(user_num < 1){
			msg = "用户名或密码不正确！";
			addForbid(ip);
			return false;
		}
		
		removeForbid(ip);
		return true;
	}
	
	private final static int CLEAN_TIME = 60*60*24;
	
	private void removeForbid(String ip) {
		CacheManage.remove(ip);
	}

	private void addForbid(String ip) {
		
		if(CacheManage.get(ip)==null){
			CacheManage.put(ip, 0, 0, CLEAN_TIME);
		}else{
			CacheManage.put(ip, (Integer)CacheManage.get(ip)+1, 0, CLEAN_TIME);
		}
	}
	
	private boolean isForbid(String ip){
		if(CacheManage.get(ip)==null){
			return false;
		}else if((Integer)CacheManage.get(ip) < 2){
			return false;
		}else{
			return true;
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
}
