package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jiuqi.dna.core.da.SQLExecutionException;

/**
 * JDBC的Statement接口的包装
 * 
 * <p>
 * 用于lazy模式的申请数据库资源，并且便于监控各种数据库访问操作。
 * 
 * @author houchunlei
 * 
 */
public class StatementWrap {

	final PooledConnection conn;
	public final long cursorId;

	StatementWrap(PooledConnection conn, boolean eager) throws SQLException {
		this.conn = conn;
		this.cursorId = conn.nextCursorId();
		if (eager) {
			this.ensure();
		}
	}

	Statement stmt;

	void ensure() throws SQLException {
		if (this.stmt == null) {
			this.stmt = this.conn.jdbcCreateStatement(this.cursorId);
		}
	}

	public void close() throws SQLException {
		if (this.stmt != null) {
			this.conn.closeStatement(this.cursorId, this.stmt);
			this.stmt = null;
		}
	}

	public final void setQueryTimeout(int seconds) throws SQLException {
		this.ensure();
		this.stmt.setQueryTimeout(seconds);
	}

	final void listenStartExecute(String sql, SqlSource source) {
		if (this.conn.listeners != null) {
			for (PooledConnectionListener listener : this.conn.listeners) {
				listener.startExecute(this.conn.connId, this.cursorId, sql, source);
			}
		}
	}

	final void listenFinishExecute(String sql, SqlSource source, long start,
			SQLException e) {
		if (this.conn.listeners != null) {
			for (PooledConnectionListener listener : this.conn.listeners) {
				listener.finishExecute(this.conn.connId, this.cursorId, sql, source, start, e);
			}
		}
	}

	public boolean execute(String sql, SqlSource source) throws SQLException {
		this.ensure();
		SQLException th = null;
		this.conn.enter();
		this.listenStartExecute(sql, source);
		final long start = System.currentTimeMillis();
		try {
			return this.stmt.execute(sql);
		} catch (SQLException e) {
			th = e;
			throw this.raise(e, sql);
		} finally {
			this.listenFinishExecute(sql, source, start, th);
			this.conn.leave(th);
		}
	}

	public int executeUpdate(String sql, SqlSource source) throws SQLException {
		this.ensure();
		SQLException th = null;
		this.conn.enter();
		this.listenStartExecute(sql, source);
		final long start = System.currentTimeMillis();
		try {
			return this.stmt.executeUpdate(sql);
		} catch (SQLException e) {
			th = e;
			throw this.raise(e, sql);
		} finally {
			this.listenFinishExecute(sql, source, start, th);
			this.conn.leave(th);
		}
	}

	public ResultSet executeQuery(String sql, SqlSource source)
			throws SQLException {
		this.ensure();
		SQLException th = null;
		this.conn.enter();
		this.listenStartExecute(sql, source);
		final long start = System.currentTimeMillis();
		try {
			return this.stmt.executeQuery(sql);
		} catch (SQLException e) {
			th = e;
			throw this.raise(e, sql);
		} finally {
			this.listenFinishExecute(sql, source, start, th);
			this.conn.leave(th);
		}
	}

	public final void setMaxRows(int max) throws SQLException {
		this.ensure();
		this.stmt.setMaxRows(max);
	}

	final SQLExecutionException raise(SQLException e, String sql) {
		return this.conn.pool.getMetadata().raise(e, sql);
	}
}