package com.jiuqi.dna.core.internal.db.support.kingbase.sync;

import java.sql.SQLException;
import java.util.regex.Pattern;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.UserFunctionImpl;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.support.kingbase.KingbaseMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbInstanceInitializationException;
import com.jiuqi.dna.core.internal.db.sync.DbSyncBase;
import com.jiuqi.dna.core.internal.db.sync.DbStructCtl;
import com.jiuqi.dna.core.misc.ExceptionCatcher;

public final class KingbaseDbRefactor
		extends
		DbSyncBase<KingbaseMetadata, KingbaseUtl, KingbaseTable, KingbaseColumn, KingbaseDataType, KingbaseIndex> {

	@Override
	protected final KingbaseUtl newStuctCtl() {
		return new KingbaseUtl(this.conn, this.dbMetadata, this.catcher);
	}

	private static final Pattern replaceProc = Pattern.compile("\\s*create\\s+or\\s+replace\\s+procedure\\s+.*");

	public KingbaseDbRefactor(PooledConnection conn,
			KingbaseMetadata dbMetadata, ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
	}

	@Override
	protected final void createIndex(IndexDefineImpl index) throws SQLException {
		this.ensureValid(index);
		this.utl.createIndex(index);
	}

	@Override
	protected final KingbaseIndex createIndex(IndexDefineImpl index,
			KingbaseTable dbtable, boolean forceNonUnqiue) throws SQLException {
		this.createIndex(index);
		return dbtable.addIndexLike(index);
	}

	// @Override
	// final void dropIndex(KingbaseIndex index) throws SQLException {
	// StringBuilder sql = new StringBuilder();
	// sql.append("drop index ");
	// this.dbMetadata.quoteId(sql, index.name);
	// this.statement.execute(sql.toString());
	// index.table.removeIndex(index);
	// }

	private final void ensureValid(IndexDefineImpl index) {
	}

	public final void sync(StoredProcedureDefineImpl procedure)
			throws SQLException {
		final String ddl = procedure.loadDdl(this.dbMetadata);
		if (!replaceProc.matcher(ddl).matches()) {
			if (this.utl.dbExistsProcedure(procedure)) {
				StringBuilder drop = new StringBuilder();
				drop.append("drop procedure ");
				this.dbMetadata.quoteId(drop, procedure.getName());
				this.utl.execute(drop.toString());
			}
		}
		this.utl.execute(ddl.replaceAll("\r", ""));
	}

	public final void check(StoredProcedureDefineImpl procedure) {
		if (!this.utl.dbExistsProcedure(procedure)) {
			throw new IllegalArgumentException("未能成功创建存储过程定义.");
		}
	}

	public void sync(UserFunctionImpl function) throws SQLException {
		if (this.utl.dbGetFunctionStatus(function.getName()) != null) {
			this.utl.dbDropFunction(function.name);
		}
		this.utl.execute(function.loaddDdl(this.dbMetadata));
	}

	@Override
	protected final void refactorColumnes(DBTableDefineImpl define,
			KingbaseTable dbtable, ColumnCompareCache compareCache)
			throws SQLException {
		this.renameUnuseColumns(define, dbtable, compareCache);
		this.utl.dbDropColumns(dbtable, compareCache);
		dbtable.removeColumnsCascade(compareCache.dropQueue);
		this.utl.dbAddnModifyColumn(dbtable, compareCache);
	}

	@Override
	protected final boolean tryExtendNumericPrecision(
			TableFieldDefineImpl field, KingbaseColumn column, boolean post) {
		return this.tryExtendNumericPrecisionRefered(field, column, KingbaseDataType.NUMBER, post);
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
			this.execSqls(this.getClass(), "kingbase.pkg", false);
		} catch (Throwable e) {
			this.catcher.catchException(new DbInstanceInitializationException("准备dna程序包", e), this);
		}
	}

}
