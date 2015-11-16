package com.eci.youku.dao;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.eci.youku.core.DatabaseManage;

public class ManagerDao {

	//private final static String SQL_INSERT = "insert into shopchannel_manager(name,password,status) values (?,md5(?),1)";
	private final static String SQL_VERIFY = "select count(0) as num from youku_admin where status=1 and name=? and password=?";
	private final static String SQL_UPDATE = "update youku_admin set last_login=?,last_ip=? where name=?";
	
	public static long verify(String name, String password) throws SQLException{
		return DatabaseManage.queryOne(Long.class, SQL_VERIFY, name, password);
	}
	
	public int updateLoginTime(String name, Timestamp time, String ip) throws SQLException{
		return DatabaseManage.update(SQL_UPDATE, time, ip, name );
	}
}
