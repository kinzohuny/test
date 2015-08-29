package com.jiuqi.dna.core.internal.db.support.db2.sync;

import java.sql.SQLException;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.UserFunctionImpl;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.support.db2.Db2Metadata;
import com.jiuqi.dna.core.internal.db.sync.DbInstanceInitializationException;
import com.jiuqi.dna.core.internal.db.sync.DbStructCtl;
import com.jiuqi.dna.core.internal.db.sync.DbSyncBase;
import com.jiuqi.dna.core.misc.ExceptionCatcher;

/**
 * @author houchunlei
 * 
 */
public final class Db2DbSync
		extends
		DbSyncBase<Db2Metadata, Db2StructCtl, Db2Table, Db2Column, Db2DataType, Db2Index> {

	public Db2DbSync(PooledConnection conn, Db2Metadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
	}

	@Override
	protected final Db2StructCtl newStuctCtl() {
		return new Db2StructCtl(this.conn, this.dbMetadata, this.catcher);
	}

	public final void sync(StoredProcedureDefineImpl procedure)
			throws SQLException {
		final String ddl = procedure.loadDdl(this.dbMetadata);
		this.utl.dropProcedureIfExists(procedure.name);
		this.utl.execute(ddl);
	}

	public final void check(StoredProcedureDefineImpl procedure) {
		// HCL Auto-generated method stub
	}

	public void sync(UserFunctionImpl function) throws SQLException {
		if (this.utl.existsFunction(function.getName())) {
			this.utl.dropFunction(function.name);
		}
		final String str = function.loaddDdl(this.dbMetadata);
		this.utl.execute(str.replaceAll("\r", ""));
	}

	@Override
	protected final void createIndex(IndexDefineImpl index) throws SQLException {
		ensureValidAsSchemaObject(index, this.dbMetadata, this.utl.snapIndexes());
		this.utl.createIndex(index);
	}

	@Override
	protected final Db2Index createIndex(IndexDefineImpl index,
			Db2Table dbtable, boolean forceNonUnqiue) throws SQLException {
		this.createIndex(index);
		return dbtable.addIndexLike(index);
	}

	@Override
	protected final void refactorColumnes(DBTableDefineImpl define,
			Db2Table dbtable, ColumnCompareCache compareCache)
			throws SQLException {
		this.setUnuseColumnNullable(compareCache);
		this.utl.dropColumns(dbtable, compareCache);
		dbtable.removeColumnsCascade(compareCache.dropQueue);
		this.utl.addnAlterColumn(dbtable, compareCache);
		for (AddFieldState add : compareCache.addQueue) {
			dbtable.addColumn(add.field.namedb());
		}
	}

	private final void setUnuseColumnNullable(ColumnCompareCache compareCache)
			throws SQLException {
		for (int i = 0, c = compareCache.unuseQueue.size(); i < c; i++) {
			Db2Column column = compareCache.unuseQueue.get(i);
			if (column.notNull) {
				this.utl.setColumnNullable(column);
				column.notNull = false;
			}
		}
	}

	@Override
	protected final boolean tryExtendNumericPrecision(
			TableFieldDefineImpl field, Db2Column column, boolean post) {
		return this.tryExtendNumericPrecisionRefered(field, column, Db2DataType.DECIMAL, post);
	}

	public final void initDb() {
		try {
			if (!this.utl.existsTable(DbStructCtl.CORE_COLLATE_GBK)) {
				this.utl.createCollateGBK();
				this.utl.initCollateGBK();
			}
		} catch (Throwable e) {
			this.catcher.catchException(new DbInstanceInitializationException("准备拼音排序表错误", e), this);
		}
		try {
			this.utl.dropExistingDnaFunctions();
			this.execSqls(this.getClass(), "db2.pkg", false);
		} catch (Throwable e) {
			this.catcher.catchException(new DbInstanceInitializationException("准备DNA程序包错误", e), this);
		}
		// try {
		// if (!this.utl.existsTable(DataSourceTenantConstants.TABLE_NAME)) {
		// this.utl.createCoreDbTenant();
		// this.utl.ensureCoreDbTenantRow();
		// }
		// } catch (Throwable e) {
		// this.catcher.catchException(new DbInstanceInitializationException(
		// "准备DNA数据库的独占锁表错误", e), this);
		// }
	}

}