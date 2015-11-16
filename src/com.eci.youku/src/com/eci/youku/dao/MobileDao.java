package com.eci.youku.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.eci.youku.core.DatabaseManage;
import com.eci.youku.model.MobileModel;

public class MobileDao {

	private static String QUERY_LIST_BYUPDATED = "select mobile, shop_mobile, created, updated"
			+ " from youku_mobile"
			+ " where updated >= ?"
			+ " order by updated";
	
	public List<MobileModel> queryListByUpdated(Timestamp lastUpdated) throws SQLException{
		
		return DatabaseManage.queryList(MobileModel.class ,QUERY_LIST_BYUPDATED, lastUpdated);
	}
}
