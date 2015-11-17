package com.eci.youku.data.push.task;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.eci.youku.data.push.dao.YKMobileDao;
import com.eci.youku.data.push.dao.YKTradeDao;
import com.eci.youku.data.push.model.YKTradeModel;
import com.eci.youku.data.push.utils.StringUtils;
import com.eci.youku.data.push.utils.WebUtils;

/**
 * 订单数据全量推送，如果不考虑先下单后注册的手机的情况，可以不运行此任务 。
 * @author Kinzo
 *
 */
public class FullDataPushToQYGJ implements Runnable{

	
	
	private static final String key = "89926b05cc3d6bcc";
	private static final String url = "http://www.qiyiguoji.com:30000/sync";
//	private static final String url = "http://localhost:30000/sync";
	private static final Logger logger = Logger.getLogger(FullDataPushToQYGJ.class);
	YKTradeDao yKTradeDao = new YKTradeDao();
	YKMobileDao yKMobileDao = new YKMobileDao();
	int size = 100;
	
	@Override
	public void run() {
		
		try {
			String status = WebUtils.doPost(getUrl()+"&task=pushTradeDataReady", null, 3000, 3000);
			logger.info("full [pushTradeDataReady] return="+status);
			if("ok".equals(status)){

				//查询有效订单并推送订单，只处理30天内订单
				logger.info("full [pushTradeData] start...");
				
				int page = 0;
				while(true){
					List<YKTradeModel> list = yKTradeDao.getFullValidTrade(page*size, size);
					String result = WebUtils.doPost((getUrl()+"&task=pushTradeData&data=true"), "text/html", JSON.toJSONString(list).getBytes("UTF-8"), 3000, 300000);
					logger.info("full [pushTradeData] push trade page="+page+" size="+list.size()+" return="+result);
					page++;
					if(list.size()!=100){
						break;
					}
				}
			}
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
		new FullDataPushToQYGJ().run();
	}
}
