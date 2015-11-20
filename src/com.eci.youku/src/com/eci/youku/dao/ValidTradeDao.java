package com.eci.youku.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.eci.youku.core.DatabaseManage;
import com.eci.youku.model.virtual.ToVerifyTradeVModel;

public class ValidTradeDao {
	
	private static final String SQL_FETCH = "insert ignore into youku_valid_trade (tid,sid,verify_status,vip_status,status,seller_nick,seller_title,buyer_nick,payment,receiver_name,receiver_mobile,pay_time,end_time,trade_created,trade_modified,created)"
			+ " select tid,sid,0,0,status,seller_nick,seller_title,buyer_nick,payment,receiver_name,receiver_mobile,pay_time,end_time,trade_created,trade_modified,now() from youku_trade where status = 'TRADE_FINISHED'";

	private static final String SQL_QUERY_TOVERIFY = "select vt.receiver_mobile as mobile,m.shop_mobile,group_concat(distinct vt.seller_title) as titles,group_concat(vt.tid) as tids,count(vt.tid) as trade_num,sum(vt.payment) as payment_sum,"
			+ " case when sum(vt.payment)>=300 then 3 when sum(vt.payment)>=100 then 1 else 0 end as vip_type,"
			+ " m.created as mobile_created,min(vt.trade_created) as trade_min_created,max(vt.trade_created) as trade_max_created,max(vt.end_time) as trade_finish_time,t.unfinish_trade,vt.vip_status"
			+ " from youku_valid_trade vt join youku_mobile m on vt.receiver_mobile = m.mobile"
			+ " left join (select receiver_mobile,count(tid) unfinish_trade from youku_trade where status in ('WAIT_BUYER_CONFIRM_GOODS','WAIT_SELLER_SEND_GOODS','SELLER_CONSIGNED_PART','TRADE_BUYER_SIGNED')) t on vt.receiver_mobile = t.receiver_mobile"
			+ " where vt.verify_status = 0"
			+ " group by 1"
			+ " order by trade_finish_time";

	private static final String SQL_UPDATE_VERIFY_STATUS = "update youku_valid_trade"
			+ " set verify_status = ?, verify_stamp = ?"
			+ " where tid = ? and verify_status = 0";
	
	public int fetch() throws SQLException{
		return DatabaseManage.update(SQL_FETCH);
	}
	
	public int updateVerifyStatus(String[] tids, String status, Timestamp verify_stamp) throws SQLException{
		Object[][] paras = new Object[tids.length][3];
		for(int i = 0; i < tids.length; i++){
			paras[i][0] = status;
			paras[i][1] = verify_stamp;
			paras[i][2] = tids[i];
		}
		return DatabaseManage.updateBatch(SQL_UPDATE_VERIFY_STATUS, paras);
	}

	public List<ToVerifyTradeVModel> queryToVerify() throws SQLException{
		return DatabaseManage.queryList(ToVerifyTradeVModel.class, SQL_QUERY_TOVERIFY);
	}
	
}
