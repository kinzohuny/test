package com.eci.youku.data.push.dao;

import java.sql.SQLException;
import java.util.List;

import com.eci.youku.data.push.model.YKShopModel;
import com.eci.youku.data.push.utils.YKDbUtils;

public class YKShopDao {
	
	private final static String QUERY_LIST = "select sid,title,nick,status,created,updated"
			+ " from shop"
			+ " where status = 1";

	public List<YKShopModel> getList() throws SQLException{
		return YKDbUtils.queryList(YKShopModel.class, QUERY_LIST);
	}
}
