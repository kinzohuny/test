package com.jiuqi.dna.core.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.da.RecordIterateAction;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.obja.DynamicObject;
import com.jiuqi.dna.core.def.query.ModifyStatementDefine;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.def.query.StoredProcedureDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sql.execute.RowCountQuerier;
import com.jiuqi.dna.core.internal.db.datasource.CallableStatementWrap;
import com.jiuqi.dna.core.internal.db.datasource.DataSourceRef;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.datasource.StatementWrap;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.support.oracle.OracleMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbSync;

/**
 * 数据库适配器
 * 
 * <p>
 * 提供所有的数据库访问的接口,可以认为是JDBC的Connection的封装.
 * 
 * @author houchunlei
 * 
 */
public final class DBAdapterImpl {

	public static final DBAdapterImpl toDBAdapter(DBAdapter anInterface) {
		if (anInterface instanceof ContextImpl<?, ?, ?>) {
			return ((ContextImpl<?, ?, ?>) anInterface).getDBAdapter();
		} else if (anInterface instanceof SituationImpl) {
			return ((SituationImpl) anInterface).getDBAdapter();
		} else if (anInterface == null) {
			return null;
		} else {
			throw new IllegalArgumentException("无效的接口");
		}
	}

	static final int executeUpdate(ContextImpl<?, ?, ?> context,
			ModifyStatementDefine statement, Object... argValues) {
		context.checkValid();
		DBCommandProxy proxy = new DBCommandProxy(context, (IStatement) statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.executeUpdate();
		} finally {
			proxy.unuse();
		}
	}

	static final RecordSet[] executeProcedure(ContextImpl<?, ?, ?> context,
			StoredProcedureDefine procedure, Object... argValues) {
		context.checkValid();
		DBCommandProxy proxy = new DBCommandProxy(context, (IStatement) procedure);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.executeProcedure();
		} finally {
			proxy.unuse();
		}
	}

	static final RecordSetImpl openQuery(ContextImpl<?, ?, ?> context,
			QueryStatementDefine statement, Object... argValues) {
		context.checkValid();
		DBCommandProxy proxy = new DBCommandProxy(context, (QueryStatementImpl) statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.executeQuery();
		} finally {
			proxy.unuse();
		}
	}

	static final RecordSetImpl openQueryLimit(ContextImpl<?, ?, ?> context,
			QueryStatementDefine statement, long offset, long rowCount,
			Object... argValues) {
		if (offset < 0 || rowCount <= 0) {
			throw new IllegalArgumentException("参数行限定返回参数错误,offset[" + offset + "],rowCount[" + rowCount + "]");
		}
		context.checkValid();
		DBCommandProxy proxy = new DBCommandProxy(context, (QueryStatementImpl) statement);
		proxy.setArgumentValues(argValues);
		try {
			if (offset == 0) {
				return proxy.executeQueryTop(rowCount);
			} else {
				return proxy.executeQueryLimit(offset, rowCount);
			}
		} finally {
			proxy.unuse();
		}
	}

	static final void iterateQuery(ContextImpl<?, ?, ?> context,
			QueryStatementDefine query, RecordIterateAction action,
			Object... argValues) {
		context.checkValid();
		DBCommandProxy proxy = new DBCommandProxy(context, (QueryStatementImpl) query);
		try {
			proxy.setArgumentValues(argValues);
			proxy.iterateQuery(action);
		} finally {
			proxy.unuse();
		}
	}

	static final void iterateQueryLimit(ContextImpl<?, ?, ?> context,
			QueryStatementDefine query, long offset, long rowCount,
			RecordIterateAction action, Object... argValues) {
		if (offset < 0 || rowCount <= 0) {
			throw new IllegalArgumentException("参数行限定返回参数错误,offset[" + offset + "],rowCount[" + rowCount + "]");
		}
		context.checkValid();
		DBCommandProxy proxy = new DBCommandProxy(context, (QueryStatementImpl) query);
		try {
			proxy.setArgumentValues(argValues);
			if (offset == 0) {
				proxy.iterateQueryTop(rowCount, action);
			} else {
				proxy.iterateQueryLimit(action, offset, rowCount);
			}
		} finally {
			proxy.unuse();
		}
	}

	static final Object executeScalar(ContextImpl<?, ?, ?> context,
			QueryStatementBase statement, Object... argValues) {
		context.checkValid();
		DBCommandProxy proxy = new DBCommandProxy(context, statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.executeScalar();
		} finally {
			proxy.unuse();
		}
	}

	static final long rowCountOf(ContextImpl<?, ?, ?> context,
			QueryStatementBase statement, Object[] argValues) {
		context.checkValid();
		DBCommandProxy proxy = new DBCommandProxy(context, statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.rowCountOf();
		} finally {
			proxy.unuse();
		}
	}

	static final long rowCountOf(ContextImpl<?, ?, ?> context,
			QueryStatementBase statement, DynamicObject argValueObj) {
		DBAdapterImpl adapter = context.getDBAdapter();
		context.checkValid();
		RowCountQuerier querier = statement.getQueryRowCountSql(adapter).newExecutor(adapter, null);
		try {
			return querier.longScalar(argValueObj);
		} finally {
			querier.unuse();
		}
	}

	static final DBCommandProxy prepareStatement(ContextImpl<?, ?, ?> context,
			IStatement statement) {
		context.checkValid();
		return new DBCommandProxy(context, statement);
	}

	static final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			ContextImpl<?, ?, ?> context, MappingQueryStatementImpl mStatement) {
		context.getDBAdapter().checkNotClosed();
		if (mStatement == null) {
			throw new NullArgumentException("mappingQuery");
		}
		return new ORMAccessorProxy<TEntity>(context, mStatement);
	}

	static final DbProduct dbProduct(ContextImpl<?, ?, ?> context) {
		return context.getDBAdapter().dbMetadata.product();
	}

	final void checkNotClosed() {
		if (this.isClosed) {
			throw Utils.tryThrowException(new SQLException("数据库连接适配器已经关闭"));
		}
	}

	/**
	 * 当前适配器不在持有数据库游标,且会话不在事务中时,释放当前对象资源.
	 */
	final void tryUnuse() {
		if (this.statements == 0 && this.connection != null && !this.connection.inTrans() && this.dbRefactor == null) {
			this.unuse();
		}
	}

	final void unuse() {
		if (this.dbRefactor != null) {
			this.dbRefactor.unuse();
			this.dbRefactor = null;
		}
		if (this.connection != null) {
			PooledConnection conn = this.connection;
			this.connection = null;
			this.transaction.clearTransient(this);
			conn.release();
		}
	}

	final void close() {
		if (!this.isClosed) {
			this.isClosed = true;
			this.unuse();
		}
	}

	private volatile DbSync dbRefactor;

	final DbSync refactor() {
		if (this.dbRefactor == null) {
			this.ensureConn();
			this.dbRefactor = this.dbMetadata.newDbRefactor(this.connection, this.dataSourceRef.dataSource.manager.application.catcher);
		}
		return this.dbRefactor;
	}

	final void syncTable(TableDefineImpl table) throws SQLException {
		if (table == null) {
			throw new NullArgumentException("table");
		}
		this.refactor().sync(table);
	}
	
	final TableDefineImpl synchroTableDefine(String tableName, String title,String category) {
		if(tableName == null || "".equals(tableName)) {
			throw new NullArgumentException("物理表标识不能为空 ");
		}
		return this.refactor().synchroTableDefine(tableName, title, category);
	}

	final void postTable(TableDefineImpl post, TableDefineImpl runtime)
			throws SQLException {
		if (post == null) {
			throw new NullPointerException();
		}
		this.refactor().post(post, runtime);
	}

	/**
	 * 返回是否在该方法中第一次确定了连接
	 */
	private final boolean ensureConn() {
		if (this.isClosed) {
			this.checkNotClosed();
		}
		if (this.connection == null) {
			this.connection = this.dataSourceRef.alloc(this.transaction);
			return true;
		} else {
			return false;
		}
	}

	public final StatementWrap createStatement() {
		StatementWrap st;
		this.ensureConn();
		try {
			st = this.connection.createStatement(true);
		} catch (Throwable e) {
			this.tryUnuse();
			throw Utils.tryThrowException(e);
		}
		this.statements++;
		return st;
	}

	public final PreparedStatementWrap prepareStatement(String sql) {
		PreparedStatementWrap pstmt;
		this.ensureConn();
		try {
			pstmt = this.connection.prepareStatement(sql, SqlSource.USER_DML, true);
		} catch (Throwable e) {
			this.tryUnuse();
			throw Utils.tryThrowException(e);
		}
		this.statements++;
		return pstmt;
	}

	public final CallableStatementWrap prepareCall(String sql) {
		CallableStatementWrap cstmt;
		this.ensureConn();
		try {
			cstmt = this.connection.prepareCall(sql, SqlSource.USER_DML, true);
		} catch (Throwable e) {
			this.tryUnuse();
			throw Utils.tryThrowException(e);
		}
		this.statements++;
		return cstmt;
	}

	public final void updateTrans(boolean forUpdate) throws SQLException {
		this.transaction.site.state.checkDBAccess(forUpdate);
		this.ensureConn();
		if (forUpdate) {
			this.connection.startTrans();
		}
	}

	final void resetTrans(boolean commitTrans, boolean helpGC) {
		DBAdapterImpl dbAdapter = this;
		do {
			try {
				dbAdapter.resolveTranse(commitTrans);
			} catch (Throwable e) {
				this.transaction.getExceptionCatcher().catchException(e, dbAdapter);
				commitTrans = false;
			}
			dbAdapter = dbAdapter.nextDBAdapter;
			if (helpGC) {
				dbAdapter.nextDBAdapter = null;
			}
		} while (dbAdapter != this);
	}

	public final void resolveTranse(boolean commit) {
		if (this.connection != null) {
			try {
				this.connection.stopTrans(commit);
			} finally {
				this.unuse();
			}
		}
	}

	public final void freeStatement(StatementWrap statement) {
		try {
			statement.close();
		} catch (Throwable e) {
			this.transaction.getExceptionCatcher().catchException(e, this);
		} finally {
			// 命令被动关闭时this.connection会为null
			this.statements--;
			this.tryUnuse();
		}
	}

	public final Transaction transaction;
	public final DataSourceRef dataSourceRef;
	public final DbMetadata dbMetadata;

	private PooledConnection connection;
	private boolean isClosed;
	private int statements;

	DBAdapterImpl(Transaction transaction, DataSourceRef dataSourceRef,
			DBAdapterImpl nextDBAdapter) {
		if (transaction == null) {
			throw new NullArgumentException("transaction");
		}
		if (dataSourceRef == null) {
			throw new NullArgumentException("dataSourceRef");
		}
		this.transaction = transaction;
		this.dataSourceRef = dataSourceRef;
		this.dbMetadata = dataSourceRef.getDbMetadata();
		this.nextDBAdapter = nextDBAdapter != null ? nextDBAdapter : this;
	}

	// cycle chain
	private DBAdapterImpl nextDBAdapter;

	final DBAdapterImpl getOtherDBAdapter(DataSourceRef dataSourceRef) {
		for (DBAdapterImpl dbAdapter = this.nextDBAdapter; dbAdapter != this; dbAdapter = dbAdapter.nextDBAdapter) {
			if (dbAdapter.dataSourceRef == dataSourceRef) {
				return dbAdapter;
			}
		}
		return this.nextDBAdapter = new DBAdapterImpl(this.transaction, dataSourceRef, this.nextDBAdapter);
	}

	public final Connection testGetConnection() {
		this.ensureConn();
		return this.connection.testGetConnection();
	}

	final void checkAccessible() {
		if (this.isClosed) {
			throw new IllegalStateException("数据库适配器已经关闭");
		}
		this.transaction.checkContextValid();
	}

	public final void oracleLockTable(String table) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("lock table ");
		OracleMetadata.quote(sql, table);
		sql.append(" in exclusive mode nowait");
		final StatementWrap statement = this.createStatement();
		try {
			statement.execute(sql.toString(), SqlSource.CORE_DML);
		} finally {
			statement.close();
		}
	}
}