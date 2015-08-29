package com.jiuqi.dna.core.internal.db.support.dm.sync;

import java.sql.SQLException;

import com.jiuqi.dna.core.def.table.IndexType;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.RoutineInvalidException;
import com.jiuqi.dna.core.impl.RoutineNotCreatedException;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.UserFunctionImpl;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.support.dm.DmMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbInstanceInitializationException;
import com.jiuqi.dna.core.internal.db.sync.DbSyncBase;
import com.jiuqi.dna.core.misc.ExceptionCatcher;

public final class DmDbSync
		extends
		DbSyncBase<DmMetadata, DmStructCtl, DmTable, DmColumn, DmDataType, DmIndex> {

	public DmDbSync(PooledConnection conn, DmMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
	}

	public void initDb() {
		try {
			this.execSqls(this.getClass(), "dm.pkg", false);
		} catch (Throwable e) {
			this.catcher.catchException(new DbInstanceInitializationException("准备DNA程序包错误", e), this);
		}
	}

	public void sync(StoredProcedureDefineImpl procedure) throws SQLException {
		if (this.utl.getProcedureStatus(procedure) != null) {
			this.utl.dropProcedure(procedure.getName());
		}
		final String ddl = procedure.loadDdl(this.dbMetadata);
		this.utl.execute(ddl.replaceAll("\r", ""));
	}

	public void check(StoredProcedureDefineImpl procedure) throws SQLException {
		final DmObjectStatus status = this.utl.getProcedureStatus(procedure);
		if (status == null) {
			throw new RoutineNotCreatedException(procedure);
		} else if (!status.equals(DmObjectStatus.VALID)) {
			throw new RoutineInvalidException(procedure);
		}
		// DM不支持 user_arguments 视图，未检查存储过程的参数定义
	}

	public void sync(UserFunctionImpl function) throws SQLException {
		if (this.utl.getFunctionStatus(function.getName()) != null) {
			this.utl.dbDropFunction(function.name);
		}
		final String str = function.loaddDdl(this.dbMetadata);
		this.utl.execute(str.replaceAll("\r", ""));
	}

	@Override
	protected DmStructCtl newStuctCtl() {
		return new DmStructCtl(this.conn, this.dbMetadata, this.catcher);
	}

	@Override
	protected void createIndex(IndexDefineImpl index) throws SQLException {
		ensureValidAsSchemaObject(index, this.dbMetadata, this.utl.snapIndexes());
		this.utl.createIndex(index);
	}

	@Override
	protected DmIndex createIndex(IndexDefineImpl index, DmTable dbtable,
			boolean forceNonUnqiue) throws SQLException {
		if (index.getType() == IndexType.B_TREE || index.getType() == null) {
			this.createIndex(index);
			return dbtable.addIndexLike(index);
		} else {
			return null;
		}
	}

	@Override
	protected boolean tryExtendNumericPrecision(TableFieldDefineImpl field,
			DmColumn column, boolean post) {
		return this.tryExtendNumericPrecisionRefered(field, column, DmDataType.NUMERIC, post);
	}

	@Override
	protected void refactorColumnes(DBTableDefineImpl define, DmTable dbtable,
			ColumnCompareCache compareCache) throws SQLException {
		this.renameUnuseColumns(define, dbtable, compareCache);
		this.utl.dbDropColumns(dbtable, compareCache);
		dbtable.removeColumnsCascade(compareCache.dropQueue);
		this.utl.addOrModifyColumns(dbtable, compareCache);
		for (AddFieldState add : compareCache.addQueue) {
			dbtable.addColumn(add.field.namedb());
		}
	}
}
