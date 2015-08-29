package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.SQLException;

import com.jiuqi.dna.core.impl.ContextVariableIntl;

public final class SqlConsolePrinter implements ComboDataSourceListener,
		PooledConnectionListener {

	static final SqlConsolePrinter INSTANCE = new SqlConsolePrinter();

	private SqlConsolePrinter() {
	}

	public void startConnect(long connId) {
	}

	public void finishConnect(long connId, SQLException e) {
	}

	public void finishConnect(ListenedConnection conn) {
		conn.addListener(this);
	}

	public void startClose(ListenedConnection conn) {
	}

	public void finishClose(long connId, SQLException e) {
	}

	public void startCheck(long connId) {
	}

	public void finishCheck(long connId, SQLException e) {
	}

	public void afterAlloc(ListenedConnection conn) {
	}

	public void beforeRevoke(ListenedConnection conn) {
	}

	public void startWait(long waitId) {
	}

	public void finishWait(long waitId, InterruptedException e) {
	}

	public void beforeDispose() {
	}

	public void afterCreateStatement(long connId, long cursorId, SQLException e) {
	}

	public void afterCloseStatement(long connId, long cursorId, SQLException e) {
	}

	public void startExecute(long connId, long cursorId, String sql,
			SqlSource source) {
	}

	public void finishExecute(long connId, long cursorId, String sql,
			SqlSource source, long start, SQLException e) {
		if (e != null) {
			System.err.println(prefix(connId, e, start, false) + sql);
		} else if (printSql(source)) {
			System.out.println(prefix(connId, e, start, ContextVariableIntl.isDebugSqlDuration()) + sql);
		}
	}

	public void startCommit(long connId, boolean commit) {
	}

	public void finishCommit(long connId, boolean commit, long start,
			SQLException e) {
		if (e != null) {
			System.err.println(prefix(connId, e, start, false) + (commit ? sql_commit : sql_rollback));
		} else if (ContextVariableIntl.isDebugSqlDuration()) {
			System.out.println(prefix(connId, e, start, ContextVariableIntl.isDebugSqlDuration()) + (commit ? sql_commit : sql_rollback));
		}
	}

	public void afterStartTran(long connId, SQLException e) {
		if (e != null) {
			System.err.println("[ERR] [connId:" + connId + "]: " + sql_auto_commit_off);
		} else if (ContextVariableIntl.isDebugSqlDuration()) {
			System.out.println("[connId:" + connId + "]: " + sql_auto_commit_off);
		}
	}

	public void afterStopTran(long connId, SQLException e) {
		if (e != null) {
			System.err.println("[ERR] [connId:" + connId + "]: " + sql_auto_commit_on);
		} else if (ContextVariableIntl.isDebugSqlDuration()) {
			System.out.println("[connId:" + connId + "]: " + sql_auto_commit_on);
		}
	}

	public static final String sql_check = "<check connection>";
	public static final String sql_auto_commit_on = "<set auto commit on>";
	public static final String sql_auto_commit_off = "<set auto commit off>";
	public static final String sql_commit = "<commit transaction>";
	public static final String sql_rollback = "<rollback transaction>";

	private static final String prefix(long connId, SQLException e, long start,
			boolean time) {
		return (e != null ? "[ERR] " : "") + "[connId:" + connId + (time ? ", elapse:" + (System.currentTimeMillis() - start) : "") + "]: ";
	}

	public static final boolean printSql(SqlSource source) {
		if (source == SqlSource.CORE_DDL || source == SqlSource.CORE_DML) {
			return false;
		}
		if (ContextVariableIntl.isDebugSqlDuration()) {
			return true;
		}
		if (source == SqlSource.USER_DML && ContextVariableIntl.isDebugDML()) {
			return true;
		}
		if (source == SqlSource.USER_DDL && ContextVariableIntl.isDebugDDL()) {
			return true;
		}
		return false;
	}
}
