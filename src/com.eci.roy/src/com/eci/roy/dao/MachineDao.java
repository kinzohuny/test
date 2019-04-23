package com.eci.roy.dao;

import java.sql.SQLException;
import java.util.List;

import com.eci.roy.core.DatabaseManage;
import com.eci.roy.model.MachineModel;

public class MachineDao {
	
	private static String QUERY_LIST_BYUSERID = "select id, user_id, mac"
			+ " from machine"
			+ " where user_id = ?"
			+ " order by id";
	
	public static List<MachineModel> queryMachineByUserId(Long userId) throws SQLException{
		return DatabaseManage.queryList(MachineModel.class, QUERY_LIST_BYUSERID, userId);
	}
	

}
