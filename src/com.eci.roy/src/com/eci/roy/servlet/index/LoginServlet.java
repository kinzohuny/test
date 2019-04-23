package com.eci.roy.servlet.index;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.eci.roy.constant.Constants;
import com.eci.roy.dao.LogDao;
import com.eci.roy.dao.UserDao;
import com.eci.roy.model.ForbiddenModel;
import com.eci.roy.model.UserModel;
import com.eci.roy.util.StringUtils;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;
	
	private static final Logger logger = Logger.getLogger(LoginServlet.class);

	String msg;
	
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession();
		msg = "";

		String random = req.getParameter("random");
		String name = req.getParameter("name");
		String password = req.getParameter("password");
		
		if(StringUtils.isNotEmpty(random)&&StringUtils.isNotEmpty(name)&&StringUtils.isNotEmpty(password)){
			verifyLogin(req, random, name, password);
		}else if(!"nullnullnull".equals(random+name+password)){
			msg = "登录信息不完整！";
		}
		
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.setContentType("text/html;charset=utf-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("<title>Login</title>");
			resp.getWriter().println("<link href=\"/resource/main.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
			resp.getWriter().println("<script src=\"/resource/md5.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<script src=\"/resource/jquery-1.11.1.js\" type=\"text/javascript\"></script>");
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
		HttpSession session = req.getSession();
		if(!random.equalsIgnoreCase((String)session.getAttribute(Constants.SESSION_RANDOM))){
			msg = "验证码不正确！";
			return false;
		}
		String ip = getRemoteIp(req);
		if(isForbidden(ip)){
			addForbidden(ip);
			msg = "禁止登录！";
			insertLog(null, "login failed[forbidden].ip:"+ip+" name:"+name);
			return false;
		}
		
		UserModel user;
		try {
			user = UserDao.verify(name, password);
		} catch (Exception e) {
			logger.error("login verify error!", e);
			msg = "未知的系统异常，请联系管理员！";
			return false;
		}
		
		if(user == null){
			addForbidden(ip);
			msg = "用户名或密码不正确！";
			insertLog(null, "login failed[U.P error].ip:"+ip+" name:"+name);
			return false;
		}
		delForbidden(ip);
		session.setAttribute(Constants.SESSION_IS_LOGIN, true);
		session.setAttribute(Constants.SESSION_USER_ID, user.getId());
		session.setAttribute(Constants.SESSION_USER_NAME, user.getName());
		insertLog(user.getId(), "login sucess.ip:"+ip);
		
		return true;
	}

	private void insertLog(Long userId, String content){
		try {
			LogDao.logInsert(Calendar.getInstance().getTime(), userId, content);
		} catch (Exception e) {
			logger.error("log insert error!userId:"+userId+" content:"+content, e);
		}
	}
	
    private String getRemoteIp(HttpServletRequest request){  
        String ipAddress = request.getHeader("x-forwarded-for");  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getHeader("Proxy-Client-IP");  
            }  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getHeader("WL-Proxy-Client-IP");  
            }  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getRemoteAddr();  
                if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){  
                    //根据网卡取本机配置的IP  
                    InetAddress inet=null;  
                    try {  
                        inet = InetAddress.getLocalHost();  
                    } catch (UnknownHostException e) {  
                        e.printStackTrace();  
                    }  
                    ipAddress= inet.getHostAddress();  
                }  
            }  
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
            if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15  
                if(ipAddress.indexOf(",")>0){  
                    ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));  
                }  
            }  
            return ipAddress;   
    }  
    
    private static Map<String, ForbiddenModel> forbidMap = new HashMap<String, ForbiddenModel>();
    
    public boolean isForbidden(String ip){
    	checkTimeout();
    	ForbiddenModel forbiddenModel = forbidMap.get(getKey(ip));
    	if(forbiddenModel == null || !forbiddenModel.isForbid()){
    		return false;
    	}
    	return true;
    }
	
    private void addForbidden(String ip){
    	ForbiddenModel forbiddenModel = forbidMap.get(getKey(ip));
    	if(forbiddenModel==null){
    		forbiddenModel = new ForbiddenModel(ip);
    		forbidMap.put(getKey(ip), forbiddenModel);
    	}
    	forbiddenModel.addTimes();
    }
    
    private void delForbidden(String ip){
    	forbidMap.remove(getKey(ip));
    }
    
    private void checkTimeout(){
    	Iterator<String> itKey = forbidMap.keySet().iterator();
    	while(itKey.hasNext()){
    		String key = itKey.next();
    		ForbiddenModel forbiddenModel = forbidMap.get(key);
    		if(forbiddenModel==null || forbiddenModel.isTimeout()){
    			forbidMap.remove(key);
    		}
    	}
    }
    
    private String getKey(String ip){
    	return Constants.CACHE_PREFIX_IP_FORBIDDEN+ip;
    }
}
