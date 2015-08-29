package com.jiuqi.dna.core.internal.db.support.mysql.sync;

import java.sql.SQLException;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.RoutineNotCreatedException;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.UserFunctionImpl;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.support.mysql.MysqlMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbInstanceInitializationException;
import com.jiuqi.dna.core.internal.db.sync.DbStructCtl;
import com.jiuqi.dna.core.internal.db.sync.DbSyncBase;
import com.jiuqi.dna.core.misc.ExceptionCatcher;

/**
 * @author houchunlei
 * 
 */
public final class MysqlDbSync
		extends
		DbSyncBase<MysqlMetadata, MysqlStructCtl, MysqlTable, MysqlColumn, MysqlDataType, MysqlIndex> {

	public MysqlDbSync(PooledConnection conn, final MysqlMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
	}

	@Override
	protected final MysqlStructCtl newStuctCtl() {
		return new MysqlStructCtl(this.conn, this.dbMetadata, this.catcher);
	}

	@Override
	protected final void createIndex(IndexDefineImpl index) throws SQLException {
		this.utl.createIndex(index);
	}

	@Override
	protected final MysqlIndex createIndex(IndexDefineImpl index,
			MysqlTable dbtable, boolean forceNonUnqiue) throws SQLException {
		this.ensureValid(index, dbtable);
		this.utl.createIndex(index);
		return dbtable.addIndexLike(index);
	}

	@Override
	public void unuse() {
		super.unuse();
	}

	public final void check(StoredProcedureDefineImpl procedure)
			throws SQLException {
		if (!this.utl.existsRoutine(MysqlStructCtl.ROUTINE_TYPE_PROCEDURE, procedure.name)) {
			throw new RoutineNotCreatedException(procedure);
		}
	}

	public final void sync(StoredProcedureDefineImpl procedure)
			throws SQLException {
		final String ddl = procedure.loadDdl(this.dbMetadata);
		this.utl.dbDropProcedureIfExist(procedure.name);
		this.utl.execute(ddl);
	}

	public final void sync(UserFunctionImpl function) throws SQLException {
		if (this.utl.existsFunction(function.getName())) {
			this.utl.dbDropFunction(function.name);
		}
		this.utl.execute(function.loaddDdl(this.dbMetadata));
	}

	@Override
	protected final void refactorColumnes(DBTableDefineImpl define,
			MysqlTable dbtable, ColumnCompareCache compareCache)
			throws SQLException {
		this.utl.dbDropColumns(dbtable, compareCache);
		dbtable.removeColumnsCascade(compareCache.dropQueue);
		this.renameUnuseColumns(define, dbtable, compareCache);
		this.utl.dbAddnAlterColumns(dbtable, compareCache);
		for (AddFieldState add : compareCache.addQueue) {
			dbtable.addColumn(add.field.namedb());
		}
	}

	/**
	 * 尝试扩展numeric列的精度，而不是直接新建字段。
	 * 
	 * @param field
	 * @param column
	 * @param post
	 *            是否post
	 * @return 是否成功的扩展了精度
	 */
	@Override
	protected final boolean tryExtendNumericPrecision(
			TableFieldDefineImpl field, MysqlColumn column, boolean post) {
		return this.tryExtendNumericPrecisionRefered(field, column, MysqlDataType.DECIMAL, post);
	}

	public void initDb() {
		try {
			if (!this.utl.existsTable(DbStructCtl.CORE_COLLATE_GBK)) {
				this.utl.createCollateGBK();
				this.utl.initCollateGBK();
			}
		} catch (Throwable e) {
			this.catcher.catchException(new DbInstanceInitializationException("准备拼音排序表错误", e), this);
		}
		try {
			this.execSqls(this.getClass(), "mysql.pkg", false);
		} catch (Throwable e) {
			this.catcher.catchException(new DbInstanceInitializationException("准备DNA程序包", e), this);
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