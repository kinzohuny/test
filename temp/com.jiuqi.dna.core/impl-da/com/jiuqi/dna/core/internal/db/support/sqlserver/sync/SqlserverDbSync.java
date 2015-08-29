package com.jiuqi.dna.core.internal.db.support.sqlserver.sync;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.RoutineNotCreatedException;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.UserFunctionImpl;
import com.jiuqi.dna.core.internal.common.Charsets;
import com.jiuqi.dna.core.internal.common.Strings;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbInstanceInitializationException;
import com.jiuqi.dna.core.internal.db.sync.DbSyncBase;
import com.jiuqi.dna.core.misc.ExceptionCatcher;

/**
 * @author houchunlei
 * 
 */
public final class SqlserverDbSync
		extends
		DbSyncBase<SqlserverMetadata, SqlserverStructCtl, SqlserverTable, SqlserverColumn, SqlserverDataType, SqlserverIndex> {

	public SqlserverDbSync(PooledConnection conn, SqlserverMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
	}

	@Override
	protected final SqlserverStructCtl newStuctCtl() {
		return new SqlserverStructCtl(this.conn, this.dbMetadata, this.catcher);
	}

	@Override
	protected final SqlserverIndex createIndex(final IndexDefineImpl index,
			final SqlserverTable dbtable, boolean forceNonUnqiue)
			throws SQLException {
		this.ensureValid(index, dbtable);
		this.utl.createIndex(index);
		return dbtable.addIndexLike(index);
	}

	public final void sync(StoredProcedureDefineImpl procedure)
			throws SQLException {
		StringBuilder s = new StringBuilder();
		s.append("if object_id('").append(procedure.getName()).append("','P') is not null drop procedure ");
		this.dbMetadata.quoteId(s, procedure.getName());
		this.utl.execute(s.toString());
		this.utl.execute(procedure.loadDdl(this.dbMetadata));
	}

	public final void sync(UserFunctionImpl function) throws SQLException {
		if (this.utl.existsFunction(function.getName())) {
			this.utl.dropFunction(function.name);
		}
		this.utl.execute(function.loaddDdl(this.dbMetadata));
	}

	public final void check(StoredProcedureDefineImpl procedure)
			throws SQLException {
		if (!this.utl.existsProcedure(procedure)) {
			throw new RoutineNotCreatedException(procedure);
		}
	}

	@Override
	protected final void refactorColumnes(DBTableDefineImpl define,
			SqlserverTable dbtable, ColumnCompareCache compareCache)
			throws SQLException {
		dbtable.fillIndexContainingColumn(compareCache.indexesCache, compareCache.dropQueue);
		dbtable.fillIndexContainingColumn(compareCache.indexesCache, compareCache.unuseQueue);
		for (ModifyFieldState state : compareCache.modifyQueue.values()) {
			dbtable.fillIndexContainColumn(compareCache.indexesCache, state.column);
		}
		for (SqlserverIndex index : compareCache.indexesCache) {
			this.dropIndex(index);
		}
		dbtable.removeIndexes(compareCache.indexesCache);
		this.utl.dropDefaultConstraints(dbtable, compareCache.dropQueue);
		this.utl.dropColumns(dbtable, compareCache);
		dbtable.removeColumnsCascade(compareCache.dropQueue);
		this.renameUnuseColumns(define, dbtable, compareCache);
		this.utl.addColumns(dbtable, compareCache);
		for (AddFieldState add : compareCache.addQueue) {
			dbtable.addColumn(add.field.namedb());
		}
		this.utl.modifyDefaultConstraint(compareCache);
		this.utl.alterColumns(dbtable, compareCache);
	}

	@Override
	protected final boolean tryExtendNumericPrecision(
			TableFieldDefineImpl field, SqlserverColumn column, boolean post) {
		return this.tryExtendNumericPrecisionRefered(field, column, SqlserverDataType.DECIMAL, post);
	}

	private static final String pkg = "sqlserver.pkg";

	public final void initDb() {
		if (this.dbMetadata.beforeYukon()) {
			String[] sqls = null;
			try {
				InputStream is = SqlserverDbSync.class.getResourceAsStream(pkg);
				if (is == null) {
					this.dbInitErr("DNA包的安装配置文件[" + pkg + "]不存在。");
					return;
				}
				try {
					sqls = Strings.readLines(is, Charsets.GBK);
				} finally {
					is.close();
				}
			} catch (IOException e) {
				this.dbInitErr("读取DNA包的安装配置文件[" + pkg + "]错误。", e);
				return;
			}
			if (sqls == null || sqls.length == 0) {
				return;
			}
			for (String sql : sqls) {
				String ddl = null;
				try {
					InputStream is = SqlserverDbSync.class.getResourceAsStream(sql);
					if (is == null) {
						this.dbInitErr("DNA包的安装脚本[" + sql + "]不存在。");
						continue;
					}
					try {
						ddl = Strings.readString(is, Charsets.GBK);
					} finally {
						is.close();
					}
				} catch (IOException e) {
					this.dbInitErr("读取DNA包的安装脚本[" + sql + "]错误。", e);
					continue;
				}
				if (ddl == null || ddl.trim().length() == 0) {
					continue;
				}
				ddl = ddl.replaceAll("dna.", this.dbMetadata.schema + ".");
				try {
					this.utl.execute(ddl, SqlSource.CORE_DDL);
				} catch (SQLException e) {
					this.dbInitErr("执行DNA包的安装脚本[" + sql + "]错误。", e);
					continue;
				}
			}
		} else {
			try {
				this.utl.prepareSchema();
			} catch (SQLException e) {
				this.catcher.catchException(new DbInstanceInitializationException("准备DNA架构错误", e), this);
			}
			try {
				this.execSqls(this.getClass(), "sqlserver.pkg", false);
			} catch (Throwable e) {
				this.catcher.catchException(new DbInstanceInitializationException("准备DNA程序包错误", e), this);
			}
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

	@Override
	protected final void createIndex(IndexDefineImpl index) throws SQLException {
		this.utl.createIndex(index);
	}
}