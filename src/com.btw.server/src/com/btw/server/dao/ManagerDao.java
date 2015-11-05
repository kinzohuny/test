package com.btw.server.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.btw.server.core.DatabaseManage;

public class ManagerDao {

//	private static final String SQL_INSERT = "insert into btw_manager (username,password,status,created) values (?,md5(?),1,now())";
	private static final String SQL_VERIFY = "select count(0) as num from btw_manager where status=1 and username=? and password=?";
	private static final String SQL_UPDATE = "update btw_manager set last_login=?,last_ip=? where username=?";

	public static long verify(String username, String password)
			throws SQLException {
		List<Map<String, Object>> result = DatabaseManage.executeQuery(
				SQL_VERIFY, new Object[] { username, password });
		if (result != null && !result.isEmpty()) {
			return (Long) result.get(0).get("num");
		}
		return -1;
	}

	public static int updateLoginTime(String username, Timestamp time, String ip)
			throws SQLException {
		return DatabaseManage.executeUpdate(SQL_UPDATE, new Object[] { time,
				ip, username });
	}
}
