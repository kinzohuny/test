package com.eci.youku.data.push.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.eci.youku.data.push.model.YKMobileModel;
import com.eci.youku.data.push.utils.YKDbUtils;

public class YKMobileDao {

	private static final String QUERY_LAST_UPDATED = "select max(updated) from mobile"; 
	
	private static final String INSERT = "insert into mobile (mobile,created,updated,shop_mobile)"
			+ " select * from (select ? mobile,? created,? updated,? shop_mobile from dual) t"
			+ " on duplicate key update mobile=t.mobile,created=t.created,updated=t.updated,shop_mobile=t.shop_mobile";
	
	public Timestamp getLastUpdated() throws SQLException{
		return YKDbUtils.queryOne(Timestamp.class, QUERY_LAST_UPDATED);
	}
	
	public int insert(List<YKMobileModel> modelList) throws SQLException{
		Object[][] paras = new Object[modelList.size()][4];
		for(int i = 0; i < modelList.size(); i++){
			YKMobileModel model = modelList.get(i);
			paras[i][0] = model.getMobile();
			paras[i][1] = model.getCreated();
			paras[i][2] = model.getUpdated();
			paras[i][3] = model.getShop_mobile();
		}
		
		return YKDbUtils.updateBatch(INSERT, paras);
	}
}
