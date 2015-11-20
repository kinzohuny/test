package com.eci.youku.servlet.func;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.eci.youku.constant.Constants;
import com.eci.youku.dao.MobileDao;
import com.eci.youku.dao.VipRecDao;
import com.eci.youku.model.VipRecModel;
import com.eci.youku.util.StringUtils;
import com.eci.youku.util.Validate;

public class YoukuVipManualServlet extends HttpServlet {

	private static final long serialVersionUID = 4401546963467607411L;
	private static final Logger logger = Logger.getLogger(YoukuVipManualServlet.class);

	VipRecDao vipRecDao = new VipRecDao();
	MobileDao mobileDao = new MobileDao();
	String msg;
	
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			String result = "";
			try {
				StringBuffer buffer = new StringBuffer();
				msg = "";
				String token = req.getParameter("token");
				if(!"false".equals(req.getParameter("new"))){
					buffer.append(getPage(new VipRecModel()));
				}else if(StringUtils.isEmpty(token)){
					buffer.append(getPage(new VipRecModel()));
					msg = "非法的请求!";
					buffer.append(getErrorReasonPage());
				}else if(token.equals(session.getAttribute(Constants.SESSION_POST_TOKEN))){
					buffer.append(getPage(new VipRecModel()));
					msg = "重复提交的请求!";
					buffer.append(getErrorReasonPage());
				}else{
					session.setAttribute(Constants.SESSION_POST_TOKEN, token);
					
					VipRecModel model = getItemFromReq(req);
					if(StringUtils.isNotEmpty(msg)){
						buffer.append(getPage(model));
						msg = "发放失败！\r\n"+msg;
						buffer.append(getErrorReasonPage());
					}else{
						model.setVip_status(0);
						model.setIsManual(1);
						vipRecDao.insert(model);
						buffer.append(getPage(new VipRecModel()));
						msg = "手机号["+model.getMobile()+"]"+model.getVip_type()+"个月会员发放成功！预计5分钟内到账。\r\n";
						buffer.append(getErrorReasonPage());
					}
				}

				result = buffer.toString();
			} catch (Exception e) {
				result = e.getMessage();
				logger.error(e.getMessage(), e);
			}
			
			resp.setContentType("text/html;charset=utf-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("<title>人工发放权益</title>");
			resp.getWriter().println("<link href=\"/resource/main.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
			resp.getWriter().println("<script src=\"/resource/main.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<script src=\"/resource/jquery-1.11.1.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<body>"+result+"</body>");
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}
	
	private Object getErrorReasonPage() {
		return "<textarea cols=\"100\" rows=\"20\" readonly=\"readonly\">"+msg+"</textarea>";
	}
	
	private String getPage(VipRecModel model){

		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"edittable\">");
		buffer.append("<form id=\"saveItem\" action=\"\" method=\"post\" onsubmit=\"return checkItem()\">");
		buffer.append("<tr><th colspan=\"3\">会员权益发放</th></tr>");
		buffer.append("<tr><td class=\"right notNull\">*手机号码：</td><td><input class=\"w300\" name=\"mobile\" value=\"").append(model.getMobile()==null?"":model.getMobile()).append("\"></td><td class=\"left\">需要人工权益发放的手机号码，系统中必须存在。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*VIP类型：</td><td><select class=\"w300\" name=\"vip_type\">")
			.append(model.getVip_type()!=null&&model.getVip_type()==3?"<option value=\"1\">月卡</option><option value=\"3\" selected=\"selected\">季卡</option>":"<option value=\"1\" selected=\"selected\">月卡</option><option value=\"3\">季卡</option>")
			.append("</select></td><td class=\"left\">优酷VIP类型：月卡、季卡</td></tr>");
		buffer.append("<tr><td><input type=\"hidden\" name=\"new\" value=\"false\"></td>");
		buffer.append("<td class=\"empty\"><input type=\"submit\" class=\"w100\" value=\"确认发放\" onClick=\"return confirm('确定人工发放会员权益吗！（此操作不可撤销）');\"></td>");
		buffer.append("<td><input type=\"hidden\" name=\"token\" value=\""+System.currentTimeMillis()+"\"></td></tr>");
		
		buffer.append("</form>");
		buffer.append("</table>");
		
		return buffer.toString();
	}
	
	private VipRecModel getItemFromReq(HttpServletRequest req) throws SQLException{
		StringBuffer errorInfo = new StringBuffer();
		VipRecModel model = new VipRecModel();
		
		model.setVip_status(0);
		model.setIsManual(1);
		
		String mobile = req.getParameter("mobile")==null?null:req.getParameter("mobile").trim();
		model.setMobile(mobile);
		if(StringUtils.isEmpty(mobile)){
			errorInfo.append("【*手机号】不可为空。\r\n");
		}else if(!Validate.isMobile(mobile)){
			errorInfo.append("【*手机号】格式不正确。\r\n");
		}else if(!mobileDao.isExist(mobile)){
			errorInfo.append("【*手机号】系统不存在该手机号。\r\n");
		}
		
		Integer vip_type = StringUtils.toInteger(req.getParameter("vip_type"));
		model.setVip_type(vip_type);
		if(StringUtils.isEmpty(req.getParameter("vip_type"))){
			errorInfo.append("【*会员类型】不可为空。\r\n");
		}else if(vip_type!=1 && vip_type!=3){
			errorInfo.append("【*会员类型】不存在类型。\r\n");
		}
		
		msg = errorInfo.toString();
		return model;
	}
}
