package com.eci.youku.data.push.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.eci.youku.data.push.model.YKTradeModel;
import com.eci.youku.data.push.utils.YKDbUtils;

public class YKTradeDao {

	private static final String QUERY_LASTJDPMODIFIED = "select date_format(max(jdp_modified),'%Y-%m-%d %H:%i:%s')"
			+ " from trade"
			+ " where sid = ?";
	
	private static final String INSERT = "insert into trade (tid,sid,STATUS,seller_nick,seller_title,buyer_nick,payment,receiver_name,receiver_mobile,pay_time,end_time,trade_created,trade_modified,jdp_created,jdp_modified,created)"
			+ " select * from (select ? tid,? sid,? STATUS,? seller_nick,? seller_title,? buyer_nick,? payment,? receiver_name,? receiver_mobile,? pay_time,? end_time,? trade_created,? trade_modified,? jdp_created,? jdp_modified, now() created from dual) t"
			+ " on duplicate key update sid=t.sid,STATUS=t.STATUS,seller_nick=t.seller_nick,seller_title=t.seller_title,buyer_nick=t.buyer_nick,payment=t.payment,receiver_name=t.receiver_name,receiver_mobile=t.receiver_mobile,pay_time=t.pay_time,end_time=t.end_time,trade_created=t.trade_created,trade_modified=t.trade_modified,jdp_created=t.jdp_created,jdp_modified=t.jdp_modified,updated=now()";

	private static final String QUERY_VALID_TRADE = "select * from"
			+ " (select t.tid,t.sid,t.STATUS,t.seller_nick,t.seller_title,t.buyer_nick,t.payment,t.receiver_name,t.receiver_mobile,t.pay_time,t.end_time,t.trade_created,t.trade_modified,t.jdp_created,t.jdp_modified,t.created,t.updated"
			+ " from trade t, mobile m"
			+ " where (t.receiver_mobile = m.mobile OR t.receiver_mobile = m.shop_mobile) and t.updated >= ? ) r"
			+ " order by r.updated"
			+ " limit ?,?";
	
	private static final String QUERY_FULL_VALID_TRADE = "select * from"
			+ " (select t.tid,t.sid,t.STATUS,t.seller_nick,t.seller_title,t.buyer_nick,t.payment,t.receiver_name,t.receiver_mobile,t.pay_time,t.end_time,t.trade_created,t.trade_modified,t.jdp_created,t.jdp_modified,t.created,t.updated"
			+ " from trade t, mobile m"
			+ " where (t.receiver_mobile = m.mobile OR t.receiver_mobile = m.shop_mobile) and t.updated < date_format(now(), '%Y-%m-%d') and t.updated >= date_format(date_sub(now(), interval 30 day), '%Y-%m-%d')) r"
			+ " order by r.updated"
			+ " limit ?,?";
	
	public String getLastJdpModified(Long sid) throws SQLException{
		return YKDbUtils.queryOne(String.class, QUERY_LASTJDPMODIFIED, sid);
	}
	
	public int insert(List<YKTradeModel> modelList) throws SQLException{
		Object[][] paras = new Object[modelList.size()][15];
		for(int i = 0; i < modelList.size(); i++){
			YKTradeModel model = modelList.get(i);
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
			paras[i][13] = model.getJdp_created();
			paras[i][14] = model.getJdp_modified();
		}
		
		return YKDbUtils.updateBatch(INSERT, paras);
	}
	
	public int insert(YKTradeModel model) throws SQLException{
		Object[] paras = new Object[15];
		paras[0] = model.getTid();
		paras[1] = model.getSid();
		paras[2] = model.getSTATUS();
		paras[3] = model.getSeller_nick();
		paras[4] = model.getSeller_title();
		paras[5] = model.getBuyer_nick();
		paras[6] = model.getPayment();
		paras[7] = model.getReceiver_name();
		paras[8] = model.getReceiver_mobile();
		paras[9] = model.getPay_time();
		paras[10] = model.getEnd_time();
		paras[11] = model.getTrade_created();
		paras[12] = model.getTrade_modified();
		paras[13] = model.getJdp_created();
		paras[14] = model.getJdp_modified();
		
		return YKDbUtils.update(INSERT, paras);
	}
	
	public List<YKTradeModel> getValidTrade(Timestamp lastTime, int start, int size) throws SQLException{
		return YKDbUtils.queryList(YKTradeModel.class, QUERY_VALID_TRADE, lastTime, start, size);
	}

	public List<YKTradeModel> getFullValidTrade(int start, int size) throws SQLException {
		return YKDbUtils.queryList(YKTradeModel.class, QUERY_FULL_VALID_TRADE, start, size);
	}
}
