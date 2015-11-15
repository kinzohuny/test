package com.eci.youku.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.eci.youku.dao.MobileDao;
import com.eci.youku.dao.ShopDao;
import com.eci.youku.model.MobileModel;
import com.eci.youku.model.ShopModel;
import com.eci.youku.util.StringUtils;

public class SyncServlet extends HttpServlet {

	private static final long serialVersionUID = -4818978526507688591L;
	private static final Logger logger = Logger.getLogger(SyncServlet.class);
	private static final String key = "89926b05cc3d6bcc";
	private MobileDao mobileDao = new MobileDao();
	private ShopDao shopDao = new ShopDao();
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String result = "";
		try {
			if(StringUtils.isNotEmpty(req.getParameter("token"))
					&&isValid(req.getParameter("timestamp"))
					&&req.getParameter("token").equals(StringUtils.md5(key+req.getParameter("timestamp")))){
				//有效请求
				if("getMobile".equals(req.getParameter("task"))){
					String lastUpdated = req.getParameter("lastUpdated");
					if(StringUtils.isEmpty(lastUpdated)){
						lastUpdated = "2015-11-09";
					}
					List<MobileModel> list = mobileDao.queryList(lastUpdated);
					result = JSON.toJSONString(list);
					
				}else if("getShop".equals(req.getParameter("task"))){
					String lastUpdated = req.getParameter("lastUpdated");
					if(StringUtils.isEmpty(lastUpdated)){
						lastUpdated = "2015-11-09";
					}
//					List<ShopModel> list = shopDao.queryList(lastUpdated);
//					result = JSON.toJSONString(list);
				}else if("pushTradeData".equals(req.getParameter("task"))){
					//TODO import trade list return ok
				}else if("pushTradeDataReady".equals(req.getParameter("task"))){
					//TODO return ok and last trade time
				}
				
				
				
			}else{
				//无效请求
				result = "error";
			}
		} catch (SQLException e) {
			result = "exception";
			logger.error(e);
		}
		
		resp.setStatus(200);
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=utf-8");
		resp.getWriter().append(result);

		resp.getWriter().flush();
		resp.getWriter().close();
	}

	private boolean isValid(String timestamp){
		if(StringUtils.isEmpty(timestamp)){
			return false;
		}
		Long time = StringUtils.toLong(timestamp);
		if(time==null||time-System.currentTimeMillis()>180000||time-System.currentTimeMillis()<-180000){
			return false;
		}
		return true;
	}
	
}
