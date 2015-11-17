package com.eci.youku.data.push.task;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.eci.youku.data.push.dao.TBJdpTbTradeDao;
import com.eci.youku.data.push.dao.YKShopDao;
import com.eci.youku.data.push.dao.YKTradeDao;
import com.eci.youku.data.push.model.TBJdpTbTradeModel;
import com.eci.youku.data.push.model.YKShopModel;
import com.eci.youku.data.push.model.YKTradeModel;
import com.eci.youku.data.push.utils.StringUtils;
import com.taobao.api.ApiException;
import com.taobao.api.domain.Trade;
import com.taobao.api.internal.util.TaobaoUtils;
import com.taobao.api.response.TradeFullinfoGetResponse;

public class DataFetchFromTB implements Runnable{
	
	private static final Logger logger = Logger.getLogger(DataFetchFromTB.class);
	private static final int size = 100;

	YKShopDao yKShopDao = new YKShopDao();
	YKTradeDao yKTradeDao = new YKTradeDao();
	TBJdpTbTradeDao tBJdpTbTradeDao = new TBJdpTbTradeDao();
	
	@Override
	public void run() {

		try {
			logger.info("start fetch from tb...");
			List<YKShopModel> shopList = yKShopDao.getList();

			logger.info("shop count is "+shopList.size());
			for(YKShopModel shopModel : shopList){

				logger.info("start fetch sid="+shopModel.getSid());
				while(true){
					String lastTime = yKTradeDao.getLastJdpModified(shopModel.getSid());
					if(StringUtils.isEmpty(lastTime)){
						lastTime = "2015-11-09 00:00:00";
					}
					List<TBJdpTbTradeModel> tBJdpTbTradeList = tBJdpTbTradeDao.queryList(shopModel.getNick(), lastTime, 0, size);
					List<YKTradeModel> yKTradeModelList = new ArrayList<YKTradeModel>();
					for(TBJdpTbTradeModel tBJdpTbTradeModel : tBJdpTbTradeList){
						YKTradeModel yKTradeModel = new YKTradeModel();
						TradeFullinfoGetResponse response = TaobaoUtils.parseResponse(tBJdpTbTradeModel.getJdp_response(), TradeFullinfoGetResponse.class);
						Trade trade = response.getTrade();
						yKTradeModel.setTid(trade.getTid());
						yKTradeModel.setSid(shopModel.getSid());
						yKTradeModel.setSTATUS(trade.getStatus());
						yKTradeModel.setSeller_nick(trade.getSellerNick());
						yKTradeModel.setSeller_title(trade.getTitle());
						yKTradeModel.setBuyer_nick(trade.getBuyerNick());
						yKTradeModel.setPayment(trade.getPayment());
						yKTradeModel.setReceiver_name(trade.getReceiverName());
						yKTradeModel.setReceiver_mobile(trade.getReceiverMobile());
						yKTradeModel.setPay_time(trade.getPayTime()==null?null:new Timestamp(trade.getPayTime().getTime()));
						yKTradeModel.setEnd_time(trade.getEndTime()==null?null:new Timestamp(trade.getEndTime().getTime()));
						yKTradeModel.setTrade_created(new Timestamp(trade.getCreated().getTime()));
						yKTradeModel.setTrade_modified(new Timestamp(trade.getModified().getTime()));
						yKTradeModel.setJdp_created(tBJdpTbTradeModel.getJdp_created());
						yKTradeModel.setJdp_modified(tBJdpTbTradeModel.getJdp_modified());
						yKTradeModelList.add(yKTradeModel);
					}
					yKTradeDao.insert(yKTradeModelList);
					logger.info(tBJdpTbTradeList.size() + " is done!");
					if(tBJdpTbTradeList.size()<100){
						break;
					}
				}
			}

			logger.info("fetch from tb is done!");
		} catch (SQLException e) {
			logger.error(e);
		} catch (ApiException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		
	}

	
}
