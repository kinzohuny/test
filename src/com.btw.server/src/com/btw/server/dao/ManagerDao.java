package com.btw.server.dao;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.btw.server.core.DatabaseManage;

public class ManagerDao {

//	private static final String SQL_INSERT = "insert into btw_manager (username,password,status,created) values (?,md5(?),1,now())";
	private static final String SQL_VERIFY = "select count(0) as num from btw_manager where status=1 and username=? and password=?";
	private static final String SQL_UPDATE = "update btw_manager set last_login=?,last_ip=? where username=?";

	public static long verify(String username, String password)
			throws SQLException {
		return DatabaseManage.queryOne(Long.class, SQL_VERIFY, username, password);
	}

	public static int updateLoginTime(String username, Timestamp time, String ip)
			throws SQLException {
		return DatabaseManage.update(SQL_UPDATE, time, ip, username);
	}
}
