package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.jiuqi.dna.core.impl.DNALogManagerInternal;
import com.jiuqi.dna.core.log.Logger;

/**
 * 池化连接
 * 
 * @author houchunlei
 * 
 */
public final class PooledConnection implements ListenedConnection {

	public final ComboDataSource pool;
	public final long connId;

	PooledConnection(ComboDataSource pool, long connId, Connection conn) {
		this.pool = pool;
		this.connId = connId;
		this.physical = conn;
	}

	private final Connection physical;

	PooledConnection next;

	final ArrayList<PooledConnectionListener> listeners = new ArrayList<PooledConnectionListener>();

	public final long connId() {
		return this.connId;
	}

	public final void addListener(PooledConnectionListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	public final void removeListener(PooledConnectionListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}

	@Deprecated
	public final Connection testGetConnection() {
		return this.physical;
	}

	/**
	 * 检查连接
	 * 
	 * <p>
	 * 会触发连接池的监听事件
	 * 
	 * @throws SQLException
	 */
	final void check() throws SQLException {
		SQLException th = null;
		for (ComboDataSourceListener listener : this.pool.listeners) {
			listener.startCheck(this.connId);
		}
		this.enter();
		try {
			Statement s = this.physical.createStatement();
			try {
				s.execute(this.pool.getMetadata().getCheckConnSql());
			} finally {
				s.close();
			}
		} catch (SQLException e) {
			throw th = e;
		} finally {
			for (ComboDataSourceListener listener : this.pool.listeners) {
				listener.finishCheck(this.connId, th);
			}
			this.leave(th);
		}
	}

	/**
	 * 关闭连接，连接必须已经从所有链表中移除。
	 * 
	 * <p>
	 * 会触发数据源的监听，不会调用enter或leave。
	 */
	final void close() {
		SQLException th = null;
		for (ComboDataSourceListener listener : this.pool.listeners) {
			listener.startClose(this);
		}
		try {
			this.physical.close();
		} catch (SQLException e) {
			this.pool.catcher.catchException(th = e, this);
		} finally {
			for (ComboDataSourceListener listener : this.pool.listeners) {
				listener.finishClose(this.connId, th);
			}
		}
	}

	/**
	 * 释放连接，回收到连接池。
	 */
	public final void release() {
		if (this.inTrans) {
			this.stopTrans(false);
		}
		this.pool.revoke(this);
	}

	private final void setQueryTimeOut(Statement stmt) throws SQLException {
		final int commandTimeoutS = this.pool.commandTimeoutS;
		if (commandTimeoutS > 0) {
			stmt.setQueryTimeout(commandTimeoutS);
		}
	}

	private final void listenCreateStatement(long cursorId, SQLException e) {
		if (this.listeners.size() > 0) {
			for (PooledConnectionListener listener : this.listeners) {
				listener.afterCreateStatement(this.connId, cursorId, e);
			}
		}
	}

	private final AtomicLong cursorIdGen = new AtomicLong();

	final long nextCursorId() {
		return this.cursorIdGen.getAndIncrement();
	}

	final Statement jdbcCreateStatement(long cursorId) throws SQLException {
		SQLException th = null;
		this.enter();
		try {
			final Statement stmt = this.physical.createStatement();
			try {
				this.setQueryTimeOut(stmt);
			} catch (SQLException e) {
				stmt.close();
				throw e;
			}
			return stmt;
		} catch (SQLException e) {
			throw th = e;
		} finally {
			this.listenCreateStatement(cursorId, th);
			this.leave(th);
		}
	}

	final PreparedStatement jdbcPrepareStatement(long cursorId, String sql)
			throws SQLException {
		SQLException th = null;
		this.enter();
		try {
			final PreparedStatement pstmt = this.physical.prepareStatement(sql);
			try {
				this.setQueryTimeOut(pstmt);
			} catch (SQLException e) {
				pstmt.close();
				throw e;
			}
			return pstmt;
		} catch (SQLException e) {
			throw th = e;
		} finally {
			this.listenCreateStatement(cursorId, th);
			this.leave(th);
		}
	}

	final CallableStatement jdbcPrepareCall(long cursorId, String sql)
			throws SQLException {
		SQLException th = null;
		this.enter();
		try {
			final CallableStatement cstmt = this.physical.prepareCall(sql);
			try {
				this.setQueryTimeOut(cstmt);
			} catch (SQLException e) {
				cstmt.close();
				throw e;
			}
			return cstmt;
		} catch (SQLException e) {
			throw th = e;
		} finally {
			this.listenCreateStatement(cursorId, th);
			this.leave(th);
		}
	}

	final void closeStatement(long cursorId, Statement stmt)
			throws SQLException {
		SQLException th = null;
		this.enter();
		try {
			stmt.close();
		} catch (SQLException e) {
			throw th = e;
		} finally {
			if (this.listeners.size() > 0) {
				for (PooledConnectionListener listener : this.listeners) {
					listener.afterCloseStatement(this.connId, cursorId, th);
				}
			}
			this.leave(th);
		}
	}

	public final StatementWrap createStatement() throws SQLException {
		return this.createStatement(false);
	}

	public final StatementWrap createStatement(boolean eager)
			throws SQLException {
		return new StatementWrap(this, eager);
	}

	public final PreparedStatementWrap prepareStatement(String sql,
			SqlSource source) throws SQLException {
		return this.prepareStatement(sql, source, false);
	}

	public final PreparedStatementWrap prepareStatement(String sql,
			SqlSource source, boolean eager) throws SQLException {
		return new PreparedStatementWrap(this, sql, source, eager);
	}

	public final CallableStatementWrap prepareCall(String sql, SqlSource source)
			throws SQLException {
		return this.prepareCall(sql, source, false);
	}

	public final CallableStatementWrap prepareCall(String sql,
			SqlSource source, boolean eager) throws SQLException {
		return new CallableStatementWrap(this, sql, source, eager);
	}

	/**
	 * 最后一次访问时间
	 */
	private long lastAccess;

	/**
	 * 最后一次访问时间
	 */
	public final long lastAccess() {
		return this.lastAccess;
	}

	private boolean lastAccessException;

	public final boolean lastAccessException() {
		return this.lastAccessException;
	}

	/**
	 * 开始
	 * 
	 * <ul>
	 * <li>创建游标
	 * <li>执行语句
	 * <li>关闭游标
	 * <li>检查连接
	 * <li>提交或回滚事务
	 * <li>开启或关闭事务
	 * </ul>
	 */
	final void enter() {
		this.lastAccess = System.currentTimeMillis();
	}

	final void leave(SQLException e) {
		this.lastAccess = System.currentTimeMillis();
		this.lastAccessException = e != null;
	}

	private boolean inTrans;

	public final boolean inTrans() {
		return this.inTrans;
	}

	public final void startTrans() throws SQLException {
		if (!this.inTrans) {
			SQLException th = null;
			this.enter();
			try {
				this.physical.setAutoCommit(false);
			} catch (SQLException e) {
				throw th = e;
			} finally {
				if (this.listeners.size() > 0) {
					for (PooledConnectionListener listener : this.listeners) {
						listener.afterStartTran(this.connId, th);
					}
				}
				this.leave(th);
			}
			this.inTrans = true;
		}
	}

	public final void stopTrans(boolean commit) {
		if (this.inTrans) {
			SQLException th = null;
			try {
				if (!this.physical.getAutoCommit()) {
					this.enter();
					if (this.listeners.size() > 0) {
						for (PooledConnectionListener listener : this.listeners) {
							listener.startCommit(this.connId, commit);
						}
					}
					final long start = System.currentTimeMillis();
					try {
						if (commit) {
							this.physical.commit();
						} else {
							this.physical.rollback();
						}
					} catch (SQLException e) {
						final Logger logger = this.logger();
						if (logger != null) {
							final Throwable ex = new IllegalStateException("在连接池[" + this.pool.name + "]，数据库的" + (commit ? "提交" : "回滚") + "操作发生异常。", e);
							logger.logFatal(null, ex, false);
						}
						throw th = e;
					} finally {
						if (this.listeners.size() > 0) {
							for (PooledConnectionListener listener : this.listeners) {
								listener.finishCommit(this.connId, commit, start, th);
							}
						}
						this.leave(th);
					}
					try {
						this.physical.setAutoCommit(true);
					} catch (SQLException e) {
						final Logger logger = this.logger();
						if (logger != null) {
							final Throwable ex = new IllegalStateException("在接池[" + this.pool.name + "]，连接在切换事务自动提交属性时发生异常。", e);
							logger.logFatal(null, ex, false);
						}
						throw th = e;
					} finally {
						if (this.listeners.size() > 0) {
							for (PooledConnectionListener listener : this.listeners) {
								listener.afterStopTran(this.connId, th);
							}
						}
						this.leave(th);
					}
					this.inTrans = false;
				}
			} catch (SQLException e) {
				th = e;
				this.pool.catcher.catchException(th, this);
			}
		}
	}

	public final DatabaseMetaData getMetaData() throws SQLException {
		return this.physical.getMetaData();
	}

	private final Logger logger() {
		try {
			return DNALogManagerInternal.getLogger("core/connpool");
		} catch (Throwable e) {
			return null;
		}
	}
}