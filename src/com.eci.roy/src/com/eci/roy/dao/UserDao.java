package com.eci.roy.dao;

import java.sql.SQLException;

import com.eci.roy.core.DatabaseManage;
import com.eci.roy.model.UserModel;

public class UserDao {

	//private final static String SQL_INSERT = "insert into shopchannel_manager(name,password,status) values (?,md5(?),1)";
	private final static String SQL_VERIFY = "select id,name from user where status=1 and name=? and password=?";
	
	public static UserModel verify(String name, String password) throws SQLException{
		return DatabaseManage.queryObject(UserModel.class, SQL_VERIFY, name, password);
	}
	
}
