package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.SQLException;

/**
 * 数据库连接的监听接口
 * 
 * <p>
 * 监听游标的创建与释放,执行语句,提交事务.
 * 
 * @author houchunlei
 * 
 */
public interface PooledConnectionListener {

	void afterCreateStatement(long connId, long cursorId, SQLException e);

	void afterCloseStatement(long connId, long cursorId, SQLException e);

	void startExecute(long connId, long cursorId, String sql, SqlSource source);

	void finishExecute(long connId, long cursorId, String sql,
			SqlSource source, long start, SQLException e);

	void startCommit(long connId, boolean commit);

	void finishCommit(long connId, boolean commit, long start, SQLException e);

	void afterStartTran(long connId, SQLException e);

	void afterStopTran(long connId, SQLException e);
}