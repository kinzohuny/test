package com.eci.youku.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eci.youku.core.DatabaseManage;
import com.eci.youku.model.MobileModel;

public class MobileDao {

	private static String QUERY_LIST = "select mobile, shop_mobile, created, updated"
			+ " from mobile"
			+ " where updated >= ?"
			+ " order by updated";
	
	public List<MobileModel> queryList(String lastUpdated) throws SQLException{
		
		List<Map<String, Object>> resultList = DatabaseManage.executeQuery(QUERY_LIST, 	lastUpdated);
		List<MobileModel> list = new ArrayList<MobileModel>();
		for(Map<String, Object> result : resultList){
			MobileModel model = new MobileModel();
			model.setMobile((String)result.get("mobile"));
			model.setShop_mobile((String)result.get("shop_mobile"));
			model.setCreated((Timestamp)result.get("created"));
			model.setUpdated((Timestamp)result.get("updated"));
			list.add(model);
		}
		return list;
	}
}
