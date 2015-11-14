package com.eci.youku.data.push.task;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.eci.youku.data.push.dao.YKTradeDao;
import com.eci.youku.data.push.model.YKTradeModel;
import com.taobao.api.internal.util.WebUtils;

public class DataPushToQYGJ implements Runnable{

	private static final Logger logger = Logger.getLogger(DataPushToQYGJ.class);
	YKTradeDao yKTradeDao = new YKTradeDao();
	
	@Override
	public void run() {
		
		try {
			String status = WebUtils.doPost("http://server.sfinx.cn/ip", null, 3000, 3000);
			if("ok".equals(status)){
				//第一步：同步手机号及店铺
				String mobiles = WebUtils.doPost("http://server.sfinx.cn/ip", null, 3000, 3000);
				//第二步：查询有效订单并推送订单
				List<YKTradeModel> list = yKTradeDao.getTradeByModile(mobiles, new Timestamp(System.currentTimeMillis()));
				logger.info(list.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
