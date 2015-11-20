package com.eci.youku.servlet.intf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.eci.youku.dao.MobileDao;
import com.eci.youku.dao.ShopDao;
import com.eci.youku.dao.TradeDao;
import com.eci.youku.model.MobileModel;
import com.eci.youku.model.ShopModel;
import com.eci.youku.model.TradeModel;
import com.eci.youku.util.StringUtils;

public class SyncServlet extends HttpServlet {

	private static final long serialVersionUID = -4818978526507688591L;
	private static final Logger logger = Logger.getLogger(SyncServlet.class);
	private static final String key = "89926b05cc3d6bcc";
	private static final Timestamp INIT_TIME = Timestamp.valueOf("2015-11-09 00:00:00");
	private MobileDao mobileDao = new MobileDao();
	private ShopDao shopDao = new ShopDao();
	private TradeDao tradeDao = new TradeDao();
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String result = "";
		try {
			//有效请求
			if(StringUtils.isNotEmpty(req.getParameter("token"))
					&&isValid(req.getParameter("timestamp"))
					&&req.getParameter("token").equals(StringUtils.md5(key+req.getParameter("timestamp")))){
				
				//手机号同步
				if("getMobile".equals(req.getParameter("task"))){
					Long lastMobileUpdated = StringUtils.toLong(req.getParameter("lastMobileUpdated"));
					if(lastMobileUpdated!=null){
						List<MobileModel> mobileList = mobileDao.queryListByUpdated(new Timestamp(lastMobileUpdated));
						result = JSON.toJSONString(mobileList);
						logger.info("[getMobile] return mobiles size="+mobileList.size());
					}
				
				//店铺同步
				}else if("getShop".equals(req.getParameter("task"))){
					Long lastShopUpdated = StringUtils.toLong(req.getParameter("lastShopUpdated"));
					if(lastShopUpdated!=null){
						List<ShopModel> shopList = shopDao.queryListByUpdated(new Timestamp(lastShopUpdated));
						result = JSON.toJSONString(shopList);
						logger.info("[getShop] return shops size="+shopList.size());
					}
				}else if("pushTradeData".equals(req.getParameter("task"))){
					
					if("true".equals(req.getParameter("data"))){
						
						BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream(), "utf-8"));  
				        StringBuffer sb = new StringBuffer();  
				        String str = ""; 
						while ((str = reader.readLine()) != null)  
						{  
							sb.append(str).append("\n");  
						}  
						String tradeJson = sb.toString();
						
						List<TradeModel> tradeList = JSON.parseArray(tradeJson, TradeModel.class);
						tradeDao.insert(tradeList);
						logger.info("[pushTradeData] add trade size="+tradeList.size());
					}
					
					Timestamp lastTradeUpdated = tradeDao.getLastUpdated();
					if(lastTradeUpdated==null){
						lastTradeUpdated = INIT_TIME;
					}

					logger.info("[pushTradeData] return lastTradeUpdated="+lastTradeUpdated);
					result = String.valueOf(lastTradeUpdated.getTime());
				}else if("pushTradeDataReady".equals(req.getParameter("task"))){
					result = "ok";
				}
				
			}else{
				//无效请求
				result = "error";
			}
		} catch (SQLException e) {
			result = "exception";
			logger.error(e.getMessage(), e);
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
