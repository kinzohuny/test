package com.jiuqi.dna.core.internal.db.support.hana.sync;

import java.sql.SQLException;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.UserFunctionImpl;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.support.hana.HanaMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbSyncBase;
import com.jiuqi.dna.core.misc.ExceptionCatcher;

public final class HanaDbSync
		extends
		DbSyncBase<HanaMetadata, HanaStructCtl, HanaTable, HanaColumn, HanaDataType, HanaIndex> {

	public HanaDbSync(PooledConnection conn, HanaMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
	}

	public void initDb() {
		// HANA Auto-generated method stub
	}

	// @Override
	// protected void createTable(DBTableDefineImpl define) throws SQLException
	// {
	// this.ensureValid(define);
	// this.utl.createTable(define);
	// define.removeDuplicatedIndex();
	// // 只创建唯一索引，作为约束
	// this.createIndexes(define, true);
	// }

	public void sync(StoredProcedureDefineImpl procedure) throws SQLException {
		// HANA Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void check(StoredProcedureDefineImpl procedure) throws SQLException {
		// HANA Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void sync(UserFunctionImpl function) throws SQLException {
		// HANA Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected HanaStructCtl newStuctCtl() {
		return new HanaStructCtl(this.conn, this.dbMetadata, this.catcher);
	}

	@Override
	protected void createIndex(IndexDefineImpl index) throws SQLException {
		ensureValidAsSchemaObject(index, this.dbMetadata, this.utl.snapIndexes());
		this.utl.createIndex(index);
	}

	@Override
	protected HanaIndex createIndex(IndexDefineImpl index, HanaTable dbtable,
			boolean forceNonUnqiue) throws SQLException {
		// HANA Auto-generated method stub
		return null;
	}

	@Override
	protected boolean tryExtendNumericPrecision(TableFieldDefineImpl field,
			HanaColumn column, boolean post) {
		// HANA Auto-generated method stub
		return false;
	}

	@Override
	protected void refactorColumnes(DBTableDefineImpl define,
			HanaTable dbtable, ColumnCompareCache compareCache)
			throws SQLException {
		// HANA Auto-generated method stub

	}
}