package com.eci.youku.data.push.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.eci.youku.data.push.model.TBJdpTbTradeModel;
import com.eci.youku.data.push.utils.TBDbUtils;

public class TBJdpTbTradeDao {

	private static final String QUERY_LIST = "SELECT tid,STATUS,buyer_nick,created,modified,jdp_response,jdp_created,jdp_modified"
			+ " FROM jdp_tb_trade"
			+ " where seller_nick = ? and jdp_modified >= ?"
			+ " order by jdp_modified"
			+ " LIMIT ?,?";
	
	public List<TBJdpTbTradeModel> queryList(String seller_nick, Timestamp lastTime, int start, int size) throws SQLException{
		return TBDbUtils.queryList(TBJdpTbTradeModel.class, QUERY_LIST, new Object[] {seller_nick, lastTime, start, size});
	}
}
