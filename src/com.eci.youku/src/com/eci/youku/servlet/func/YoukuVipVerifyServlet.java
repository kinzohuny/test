package com.eci.youku.servlet.func;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.eci.youku.model.virtual.ToVerifyTradeVModel;
import com.eci.youku.util.StringUtils;

public class YoukuVipVerifyServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;
	private static final Logger logger = Logger.getLogger(YoukuVipVerifyServlet.class);

	String msg = "";
	String title = "";
	String page = "";
	ValidTradeDao validTradeDao = new ValidTradeDao();
	VipRecDao vipRecDao = new VipRecDao();
	List<ToVerifyTradeVModel> toVerifyTradeVModelList = null;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			try {
				if(StringUtils.isNotEmpty(req.getParameter("changeStatus"))){
					title = "状态更新";
					page = statusHandle(req);
				}else{
					title = "会员权益审核";
					page = defaultHandle(req);
				}
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
			resp.getWriter().println("<body>会员权益审核<input type=\"hidden\" id=\"token\" name=\"token\" value=\""+System.currentTimeMillis()+"\"><br>"+page+"</body>");
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private String statusHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		
		String token = req.getParameter("token");
		if(StringUtils.isEmpty(token)){
			buffer.append("非法的请求!");
		}else if(token.equals(req.getSession().getAttribute(Constants.SESSION_POST_TOKEN))){
			buffer.append("重复提交的请求!");
		}else{
			req.getSession().setAttribute(Constants.SESSION_POST_TOKEN, token);
			
			String status = req.getParameter("changeStatus");
			String mobiles = req.getParameter("ids");
			
			List<String> mobileList = Arrays.asList(mobiles.split(","));
			//审核通过的记录生成会员发放记录
			List<VipRecModel> vipRecModelList = new ArrayList<VipRecModel>();
			StringBuffer tids = new StringBuffer();
			for(ToVerifyTradeVModel model:toVerifyTradeVModelList){
				if(mobileList.contains(model.getMobile())){
					tids.append(model.getTids()).append(",");
					if(1 == model.getVip_type() || 3 == model.getVip_type()){
						vipRecModelList.add(getVipRecModel(model));
					}
				}
			}
			
			int i = validTradeDao.updateVerifyStatus(tids.toString().split(","), status, new Timestamp(System.currentTimeMillis()));
			if("1".equals(status)){
				int j = vipRecDao.insertList(vipRecModelList);
				buffer.append("同意成功，处理").append(i).append("条订单记录。生成").append(j).append("条VIP发放记录<br>");
			}else if("2".equals(status)){
				buffer.append("拒绝成功，修改").append(mobileList.size()).append("条订单记录。<br>");
			}else if("3".equals(status)){
				buffer.append("人工发放，修改").append(mobileList.size()).append("条订单记录。请人工处理相关手机号。<br>");
			}else{
				logger.warn("错误的状态:["+status+"] 被修改的订单："+tids);
				throw new RuntimeException("错误的状态:["+status+"] 被修改的订单："+tids);
			}
		}
		
		buffer.append("<span id=\"jumpTo\">5</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(5,'/youkuvipverify');</script>");
		return buffer.toString();
	}

	private VipRecModel getVipRecModel(ToVerifyTradeVModel toVerifyTradeVModel) {
		VipRecModel model = new VipRecModel();
		model.setMobile(toVerifyTradeVModel.getMobile());
		model.setVip_type(toVerifyTradeVModel.getVip_type());
		model.setVip_status(0);
		model.setIsManual(0);
		return model;
	}

	private String defaultHandle(HttpServletRequest req) throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(req.getParameter("sid"))){
			map.put("sidLike", "%"+req.getParameter("sid")+"%");
		}
		if(StringUtils.isNotEmpty(req.getParameter("title"))){
			map.put("titleLike", "%"+req.getParameter("title")+"%");
		}
		if(StringUtils.isNotEmpty(req.getParameter("status"))){
			map.put("status", req.getParameter("status"));
		}
		toVerifyTradeVModelList = validTradeDao.queryToVerify();
		return getButtonPage()+getFilterPage(req)+getListPage(toVerifyTradeVModelList);
	}
	
	private String getButtonPage(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"menutable\"><tr>");
		buffer.append("<td><input type=\"button\" value=\"全选\" onclick=\"selectAll();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"全清\" onclick=\"selectNone();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"同意\" onclick=\"setStatus(1,'同意');\"></td>");
		buffer.append("<td><input type=\"button\" value=\"拒绝\" onclick=\"setStatus(2,'拒绝');\"></td>");
		buffer.append("<td><input type=\"button\" value=\"人工发放\" onclick=\"setStatus(3,'人工发放');\"></td>");
		//buffer.append("<td><input type=\"button\" value=\"查看审核结果\" onclick=\"location.href='?result=true'\"></td>");
		buffer.append("</tr></table>");
		
		return buffer.toString();
	}
	
	private String getFilterPage(HttpServletRequest req){
		StringBuffer buffer = new StringBuffer();
		//暂不放置查询条件
//		buffer.append("<table class=\"menutable\"><tr>");
//		buffer.append("<form action=\"\" method=\"post\">");
//		buffer.append("<td>店铺ID：<input class=\"val w100\" name=\"sid\" value=\"").append(req.getParameter("sid")==null?"":req.getParameter("sid")).append("\"></td>");
//		buffer.append("<td>店铺名称：<input class=\"val w100\" name=\"title\" value=\"").append(req.getParameter("title")==null?"":req.getParameter("title")).append("\"></td>");
//		buffer.append("<td>状态：<input class=\"val w50\" name=\"status\" value=\"").append(req.getParameter("status")==null?"":req.getParameter("status")).append("\"></td>");
//		buffer.append("<td><input type=\"submit\" value=\"查询\"></td>");
//		buffer.append("<td><input type=\"button\" value=\"清空\" onclick=\"clearFilter();\"></td>");
//		buffer.append("</from>");
//		buffer.append("</tr></table>");
		return buffer.toString();
	}
	
	private String getListPage(List<ToVerifyTradeVModel> list){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"gridtable\">");
		buffer.append("共"+list.size()+"条待审核记录");
		buffer.append("<tr>")
			.append("<th>手机</th>")
			.append("<th>备用手机</th>")
			.append("<th>店铺名称</th>")
			.append("<th>交易笔数</th>")
			.append("<th>成交金额</th>")
			.append("<th>会员类型</th>")
			.append("<th>注册时间</th>")
			.append("<th>首单时间</th>")
			.append("<th>末单时间</th>")
			.append("<th>成交时间</th>")
			.append("<th>未完成交易笔数</th></tr>");

		for(ToVerifyTradeVModel model : list){
			buffer.append("<tr>");
			buffer.append("<td><input id="+model.getMobile()+" name=\"checkbox\" type=\"checkbox\">").append(model.getMobile()).append("</td>");
			buffer.append("<td").append(needWarn(model.getShop_mobile()!=null)).append(">").append(toEmpty(model.getShop_mobile())).append("</td>");
			buffer.append("<td>").append(model.getTitles()).append("</td>");
			buffer.append("<td>").append(model.getTrade_num()).append("</td>");
			buffer.append("<td>").append(model.getPayment_sum()).append("</td>");
			buffer.append("<td>").append(model.getVip_type()).append("</td>");
			buffer.append("<td").append(needWarn(model.getMobile_created().getTime() > model.getTrade_min_created().getTime())).append(">").append(model.getMobile_created()).append("</td>");
			buffer.append("<td").append(needWarn(model.getMobile_created().getTime() > model.getTrade_min_created().getTime())).append(">").append(model.getTrade_min_created()).append("</td>");
			buffer.append("<td>").append(model.getTrade_max_created()).append("</td>");
			buffer.append("<td>").append(model.getTrade_finish_time()).append("</td>");
			buffer.append("<td").append(needWarn(model.getUnfinish_trade()!=null)).append(">").append(toZero(model.getUnfinish_trade())).append("</td>");
			buffer.append("</tr>");
		}

		buffer.append("</table>");
		return String.valueOf(buffer.toString());
	} 
	
	private String needWarn(boolean isNeed){
		if(isNeed){
			return " class=\"yellow\"";
		}
		return "";
	}
	
	private long toZero(Long l){
		if(l==null){
			return 0;
		}
		return l;
	}
	
	private String toEmpty(String str){
		if(str==null){
			return "";
		}
		return str;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
}
