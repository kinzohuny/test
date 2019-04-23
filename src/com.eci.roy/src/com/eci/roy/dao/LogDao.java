package com.eci.roy.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.eci.roy.core.DatabaseManage;
import com.eci.roy.model.LogModel;

public class LogDao {
	
	private static final String SQL_INSERT = "insert into log (time,user_id,content) values (?,?,?);";
	public static int logInsert(Date time, Long userId, String content) throws SQLException{
		return DatabaseManage.update(SQL_INSERT, time, userId, content);
	}
	
	private static final String SQL_QUERYLOGBYUSERID = "select id,time,user_id,content from log where user_id=? order by id desc limit 100;";
	public static List<LogModel> queryLogByUserId(Long userId) throws SQLException{
		return DatabaseManage.queryList(LogModel.class, SQL_QUERYLOGBYUSERID, userId);
	}
	
	private static final String SQL_QUERYLOG = "select log.id,time,user_id,content,user.name as user_name from log left join user on log.user_id=user.id order by id desc limit 10000;";
	public static List<LogModel> queryLog() throws SQLException{
		return DatabaseManage.queryList(LogModel.class, SQL_QUERYLOG);
	}
}
