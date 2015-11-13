package com.eci.youku.data.push.dao;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.eci.youku.data.push.utils.YKDbUtils;

public class YKTradeDao {

	private static final String QUERY_LASTJDPMODIFIED = "select max(jdp_modified)"
			+ " from trade"
			+ " where sid = ?";
	
	private static final String insert = "insert into trade ()"
			+ " values()"
			+ " where sid = ?";
	
	/**
	 * TODO trade insert
	 * tid;
		sid;
		STATUS;
		seller_nick;
		seller_title;
		buyer_nick;
		payment;
		receiver_name;
		receiver_mobile;
		pay_time;
		end_time;
		trade_created;
		trade_modified;
		jdp_created;
		jdp_modified;
	 */
	
	public Timestamp getLastJdpModified(Long sid) throws SQLException{
		return YKDbUtils.queryOne(Timestamp.class, QUERY_LASTJDPMODIFIED, sid);
	}
}
