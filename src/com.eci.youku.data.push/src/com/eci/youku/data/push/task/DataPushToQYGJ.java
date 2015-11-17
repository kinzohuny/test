package com.eci.youku.data.push.task;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.eci.youku.data.push.dao.YKMobileDao;
import com.eci.youku.data.push.dao.YKTradeDao;
import com.eci.youku.data.push.model.YKMobileModel;
import com.eci.youku.data.push.model.YKTradeModel;
import com.eci.youku.data.push.utils.StringUtils;
import com.eci.youku.data.push.utils.WebUtils;

public class DataPushToQYGJ implements Runnable{

	private static final String key = "89926b05cc3d6bcc";
	private static final Timestamp INIT_TIME = Timestamp.valueOf("2015-11-09 00:00:00");
	private static final String url = "http://www.qiyiguoji.com:30000/sync";
//	private static final String url = "http://localhost:30000/sync";
	private static final Logger logger = Logger.getLogger(DataPushToQYGJ.class);
	YKTradeDao yKTradeDao = new YKTradeDao();
	YKMobileDao yKMobileDao = new YKMobileDao();
	int size = 100;
	
	@Override
	public void run() {
		
		try {
			String status = WebUtils.doPost(getUrl()+"&task=pushTradeDataReady", null, 3000, 3000);
			logger.info("[pushTradeDataReady] return="+status);
			if("ok".equals(status)){
				//第一步：同步店铺
//				String lastUpdated = 
//				String mobiles = WebUtils.doPost(getUrl()+"&task=getMobile&lastUpdated="+lastUpdated, null, 3000, 3000);
				
				
				//第二步：同步手机号
				logger.info("[getMobile] start...");
				Timestamp lastMobileUpdated = yKMobileDao.getLastUpdated();
				if(lastMobileUpdated==null){
					lastMobileUpdated = INIT_TIME;
				}
				String mobilesJson = WebUtils.doPost(getUrl()+"&task=getMobile&lastMobileUpdated="+lastMobileUpdated.getTime(), null, 3000, 300000);
				logger.info("[getMobile] return="+mobilesJson);
				if(StringUtils.isNotEmpty(mobilesJson)){
					List<YKMobileModel> mobileList = JSON.parseArray(mobilesJson, YKMobileModel.class);
					yKMobileDao.insert(mobileList);
					logger.info("[getMobile] add mobile size="+mobileList.size());
				}
				//第三步：查询有效订单并推送订单

				logger.info("[pushTradeData] start...");
				Long lastTradeUpdated = StringUtils.toLong(WebUtils.doPost(getUrl()+"&task=pushTradeData", null, 3000, 300000));
				logger.info("[pushTradeData] return="+lastTradeUpdated);
				
				while(lastTradeUpdated!=null){
					logger.info("[pushTradeData] push trade lastTradeUpdated="+new Timestamp(lastTradeUpdated));
					List<YKTradeModel> list = yKTradeDao.getValidTrade(new Timestamp(lastTradeUpdated), 0, size);
					String result = WebUtils.doPost((getUrl()+"&task=pushTradeData&data=true"), "text/html", JSON.toJSONString(list).getBytes("UTF-8"), 3000, 300000);
					logger.info("[pushTradeData] push trade size="+list.size()+" return="+result);
					lastTradeUpdated = StringUtils.toLong(result);
					if(list.size()!=100){
						break;
					}
				}
			}
			logger.info("[pushTradeDataReady] all done!");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private String getUrl(){
		StringBuilder result = new StringBuilder(url);
		result.append("?");
		result.append("timestamp=");
		String timestamp = String.valueOf(System.currentTimeMillis());
		result.append(timestamp);
		result.append("&token=");
		result.append(StringUtils.md5(key+timestamp));
		return result.toString();
	}
	
	public static void main(String[] args) {
		new DataPushToQYGJ().run();
	}
}
