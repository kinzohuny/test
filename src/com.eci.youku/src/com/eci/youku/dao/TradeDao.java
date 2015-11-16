package com.eci.youku.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.eci.youku.core.DatabaseManage;
import com.eci.youku.model.TradeModel;

public class TradeDao {
	
	private static final String QUERY_LAST_UPDATED = "select max(updated) as lastUpdated"
			+ " from youku_trade";
	

	
	private static final String INSERT = "insert into youku_trade (tid,sid,STATUS,seller_nick,seller_title,buyer_nick,payment,receiver_name,receiver_mobile,pay_time,end_time,trade_created,trade_modified,created,updated)"
			+ " select * from (select ? tid,? sid,? STATUS,? seller_nick,? seller_title,? buyer_nick,? payment,? receiver_name,? receiver_mobile,? pay_time,? end_time,? trade_created,? trade_modified,? created,? updated from dual) t"
			+ " on duplicate key update sid=t.sid,STATUS=t.STATUS,seller_nick=t.seller_nick,seller_title=t.seller_title,buyer_nick=t.buyer_nick,payment=t.payment,receiver_name=t.receiver_name,receiver_mobile=t.receiver_mobile,pay_time=t.pay_time,end_time=t.end_time,trade_created=t.trade_created,trade_modified=t.trade_modified,created=t.created,updated=t.updated";
	
	public Timestamp getLastUpdated() throws SQLException{
		return DatabaseManage.queryOne(Timestamp.class, QUERY_LAST_UPDATED);
	}
	
	public int insert(List<TradeModel> modelList) throws SQLException{
		Object[][] paras = new Object[modelList.size()][15];
		for(int i = 0; i < modelList.size(); i++){
			TradeModel model = modelList.get(i);
			paras[i][0] = model.getTid();
			paras[i][1] = model.getSid();
			paras[i][2] = model.getSTATUS();
			paras[i][3] = model.getSeller_nick();
			paras[i][4] = model.getSeller_title();
			paras[i][5] = model.getBuyer_nick();
			paras[i][6] = model.getPayment();
			paras[i][7] = model.getReceiver_name();
			paras[i][8] = model.getReceiver_mobile();
			paras[i][9] = model.getPay_time();
			paras[i][10] = model.getEnd_time();
			paras[i][11] = model.getTrade_created();
			paras[i][12] = model.getTrade_modified();
			paras[i][13] = model.getCreated();
			paras[i][14] = model.getUpdated();
		}
		
		return DatabaseManage.updateBatch(INSERT, paras);
	}
	

}
