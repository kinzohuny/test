package com.eci.youku.data.push.dao;

import java.sql.SQLException;
import java.util.List;

import com.eci.youku.data.push.model.YKShopModel;
import com.eci.youku.data.push.utils.YKDbUtils;

public class YKShopDao {
	
	private final static String QUERY_LIST = "select sid,title,nick,status,created,updated"
			+ " from shop"
			+ " where status = 1";
	
	private final static String QUERY_BY_SID = "select sid,title,nick,status,created,updated"
			+ " from shop"
			+ " where sid = ?";

	public List<YKShopModel> getList() throws SQLException{
		return YKDbUtils.queryList(YKShopModel.class, QUERY_LIST);
	}
	
	public YKShopModel getBySid(Long sid) throws SQLException{
		return YKDbUtils.queryObject(YKShopModel.class, QUERY_BY_SID, sid);
	}
}
