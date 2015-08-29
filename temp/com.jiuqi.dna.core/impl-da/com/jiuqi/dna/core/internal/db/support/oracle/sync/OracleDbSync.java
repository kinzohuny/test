package com.jiuqi.dna.core.internal.db.support.oracle.sync;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.jiuqi.dna.core.def.query.ArgumentOutput;
import com.jiuqi.dna.core.def.table.IndexType;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.RoutineDefineInconsistentException;
import com.jiuqi.dna.core.impl.RoutineInvalidException;
import com.jiuqi.dna.core.impl.RoutineNotCreatedException;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.UserFunctionImpl;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.support.oracle.OracleMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbInstanceInitializationException;
import com.jiuqi.dna.core.internal.db.sync.DbStructCtl;
import com.jiuqi.dna.core.internal.db.sync.DbSyncBase;
import com.jiuqi.dna.core.misc.ExceptionCatcher;

/**
 * @author houchunlei
 * 
 */
public final class OracleDbSync
		extends
		DbSyncBase<OracleMetadata, OracleStructCtl, OracleTable, OracleColumn, OracleDataType, OracleIndex> {

	public OracleDbSync(PooledConnection conn, OracleMetadata metadata,
			ExceptionCatcher catcher) {
		super(conn, metadata, catcher);
	}

	@Override
	protected final OracleStructCtl newStuctCtl() {
		return new OracleStructCtl(this.conn, this.dbMetadata, this.catcher);
	}

	@Override
	protected final void createIndex(IndexDefineImpl index) throws SQLException {
		ensureValidAsSchemaObject(index, this.dbMetadata, this.utl.snapIndexes());
		if (index.getType() == IndexType.B_TREE || index.getType() == null) {
			this.utl.createIndex(index);
		} else {
			this.utl.createBitmap(index);
		}
	}

	@Override
	protected final OracleIndex createIndex(IndexDefineImpl index,
			OracleTable dbtable, boolean forceNonUnqiue) throws SQLException {
		if (index.getType() == IndexType.B_TREE || index.getType() == null) {
			this.createIndex(index);
		} else {
			this.createBitmap(index);
		}
		return dbtable.addIndexLike(index);
	}

	final void createBitmap(IndexDefineImpl index) throws SQLException {
		if (this.utl.enableBitMappedIndexes()) {
			ensureValidAsSchemaObject(index, this.dbMetadata, this.utl.snapIndexes());
			this.utl.createBitmap(index);
		}
	}

	public final void sync(StoredProcedureDefineImpl procedure)
			throws SQLException {
		final String ddl = procedure.loadDdl(this.dbMetadata);
		if (this.utl.getProcedureStatus(procedure) != null) {
			this.utl.dropProcedure(procedure.getName());
		}
		this.utl.execute(ddl.replaceAll("\r", ""));
	}

	/**
	 * <ol>
	 * <li>argument_name 无实质意义.
	 * <li>data_level 只能为0,否则意味着复合类型,其实通过data_type也可以判断出是否复合类型.
	 * <li>data_type 需要为框架支持的数据类型.
	 * <li>in_out
	 * </ol>
	 */
	private static final String SELECT_PROC_ARGUMENTS = "select argument_name, data_level, data_type, in_out from user_objects o join user_arguments a on o.object_id = a.object_id where o.object_type = 'PROCEDURE' and o.object_name = ? order by sequence";

	private static final String REF_CURSOR = "REF CURSOR";

	static final ArgumentOutput getArgumentOutput(String inOut) {
		if (inOut.equals("IN")) {
			return ArgumentOutput.IN;
		} else if (inOut.equals("IN/OUT")) {
			return ArgumentOutput.IN_OUT;
		} else if (inOut.equals("OUT")) {
			return ArgumentOutput.OUT;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public final void check(StoredProcedureDefineImpl procedure)
			throws SQLException {
		final OracleObjectStatus status = this.utl.getProcedureStatus(procedure);
		if (status == null) {
			throw new RoutineNotCreatedException(procedure);
		} else if (!status.equals(OracleObjectStatus.VALID)) {
			throw new RoutineInvalidException(procedure);
		}
		final PreparedStatementWrap ps = this.conn.prepareStatement(SELECT_PROC_ARGUMENTS, SqlSource.CORE_DML);
		try {
			ps.setString(1, procedure.getName());
			ResultSet rs = ps.executeQuery();
			try {
				int i = 0;
				final int arg = procedure.getArguments().size();
				final int result = procedure.getResultSets();
				while (rs.next()) {
					final String type = rs.getString(3);
					final int level = rs.getInt(2);
					// final ArgumentOutput inOut = getArgumentOutput(rs.getString(4));
					if (i < arg) {
						if (type.equals(REF_CURSOR)) {
							throw new RoutineDefineInconsistentException(procedure, "参数个数不足-期望[" + arg + "]个参数");
						}
						if (level != 0) {
							throw new UnsupportedOperationException("存储过程[" + procedure.getName() + "]包含复合类型参数.");
						}
					} else if (i < arg + result) {
						if (!type.equals(REF_CURSOR)) {
							throw new UnsupportedOperationException("存储过程[" + procedure.getName() + "]结果集参数类型不是REF CURSOR.");
						}
					}
					i++;
				}
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

	@Override
	protected final void refactorColumnes(DBTableDefineImpl define,
			OracleTable dbtable, ColumnCompareCache compareCache)
			throws SQLException {
		this.renameUnuseColumns(define, dbtable, compareCache);
		this.utl.dbDropColumns(dbtable, compareCache);
		dbtable.removeColumnsCascade(compareCache.dropQueue);
		this.utl.dbAddnModifyColumn(dbtable, compareCache);
		for (AddFieldState add : compareCache.addQueue) {
			dbtable.addColumn(add.field.namedb());
		}
	}

	@Override
	protected final boolean tryExtendNumericPrecision(
			TableFieldDefineImpl field, OracleColumn column, boolean post) {
		return this.tryExtendNumericPrecisionRefered(field, column, OracleDataType.NUMBER, post);
	}

	// private final class OracleHierarchySync extends HierarchyRefactor {
	//
	// @Override
	// final HierarchyState detectState(HierarchyDefineImpl hierarchy)
	// throws SQLException {
	// if (hierarchy.tableName() == null) {
	// return CREATE_NEW;
	// } else if (!this.sync.tableSync.namespace.contains(hierarchy
	// .tableName())) {
	// return CREATE_NEW;
	// }
	// PreparedStatement ps = this.sync.adapter
	// .prepareStatement(OracleTable.SELECT_TABLE_COLUMNS);
	// try {
	// ps.setString(1, hierarchy.tableName());
	// ResultSet rs = ps.executeQuery();
	// try {
	// int length = 0;
	// if (rs.next()) {
	// if (!rs.getString(1).equals(
	// HierarchyDefineImpl.COLUMN_NAME_RECID)) {
	// return CREATE_NEW;
	// } else if (!rs.getString(2).equals("RAW")) {
	// return CREATE_NEW;
	// } else if (rs.getInt(3) != 16) {
	// return CREATE_NEW;
	// }
	// } else {
	// // unreachable
	// return CREATE_NEW;
	// }
	// if (rs.next()) {
	// if (!rs.getString(1).equals(
	// HierarchyDefineImpl.COLUMN_NAME_PATH)) {
	// return CREATE_NEW;
	// } else if (!rs.getString(2).equals("RAW")) {
	// return CREATE_NEW;
	// }
	// length = rs.getInt(3);
	// } else {
	// return CREATE_NEW;
	// }
	// if (rs.next()) {
	// if (!rs.getString(1).equals(
	// HierarchyDefineImpl.COLUMN_NAME_STATUS)) {
	// return CREATE_NEW;
	// } else if (!rs.getString(2).equals("NUMBER")) {
	// return CREATE_NEW;
	// }
	// } else {
	// return CREATE_NEW;
	// }
	// if (rs.next()) {
	// return CREATE_NEW;
	// }
	// if (length < hierarchy.getPathLength()) {
	// return EXTEND_PATH;
	// }
	// return DO_NOTHING;
	// } finally {
	// rs.close();
	// }
	// } finally {
	// this.sync.adapter.freeStatement(ps);
	// }
	// }
	//
	// @Override
	// final void createHierarchyTable(HierarchyDefineImpl hierarchy)
	// throws SQLException {
	// SqlBuilder sql = new SqlBuilder(this.sync.dbMetadata);
	// sql.appendCreate().appendTable().appendId(hierarchy);
	// sql.nSpace().lp().nNewline().pi();
	// sql.appendId(HierarchyDefineImpl.COLUMN_NAME_RECID).nSpace()
	// .appendType(TypeFactory.GUID).nSpace();
	// sql.appendConstraint().appendId(hierarchy.pkIndex())
	// .appendPrimaryKey().nComma().nNewline();
	// sql.appendId(HierarchyDefineImpl.COLUMN_NAME_PATH)
	// .nSpace()
	// .appendType(
	// TypeFactory.VARBINARY(hierarchy.getPathLength()))
	// .nComma().nNewline();
	// sql.appendId(HierarchyDefineImpl.COLUMN_NAME_STATUS).nSpace()
	// .appendType(TypeFactory.INT).appendDefault().append("1")
	// .nNewline();
	// sql.ri().rp();
	// this.sync.statement.execute(sql);
	//
	// }
	//
	// @Override
	// final void extendPath(HierarchyDefineImpl hierarchy)
	// throws SQLException {
	// SqlBuilder sql = new SqlBuilder(this.sync.dbMetadata);
	// sql.append("alter table ").appendId(hierarchy);
	// sql.append(" modify (");
	// sql.appendId(HierarchyDefineImpl.COLUMN_NAME_PATH).nSpace();
	// sql.appendType(TypeFactory.VARBINARY(hierarchy.getPathLength()))
	// .rp();
	// this.sync.statement.execute(sql);
	// }
	//
	// }
	//
	// private final class OracleProcRefactor extends ProcRefactor {
	//
	// OracleProcRefactor() throws SQLException {
	// super(true);
	// }
	//
	// @Override
	// final void initNamespace() throws SQLException {
	// PreparedStatement ps = this.sync.adapter
	// .prepareStatement("select object_name from user_objects where object_type = 'PROCEDURE'");
	// try {
	// ResultSet rs = ps.executeQuery();
	// try {
	// while (rs.next()) {
	// final String on = rs.getString(1);
	// this.namespace.add(on);
	// }
	// } finally {
	// rs.close();
	// }
	// } finally {
	// ps.close();
	// }
	// }
	//
	// }

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
			this.execSqls(this.getClass(), "oracle.pkg", true);
		} catch (Throwable e) {
			this.catcher.catchException(new DbInstanceInitializationException("准备DNA程序包错误", e), this);
		}
		// try {
		// if (!this.utl.existsTable(DataSourceLockConstants.TABLE_NAME)) {
		// this.utl.createCoreDbTenant();
		// this.utl.ensureCoreDbTenantRow();
		// } else if (!this.utl.existsColumn(
		// DataSourceLockConstants.TABLE_NAME,
		// DataSourceLockConstants.COL_SILENZ)) {
		// this.utl.tenantAddLckCol();
		// }
		// } catch (Throwable e) {
		// this.catcher.catchException(new DbInstanceInitializationException(
		// "准备DNA数据库的独占锁表错误", e), this);
		// }
	}

	public final void sync(UserFunctionImpl function) throws SQLException {
		if (this.utl.getFunctionStatus(function.getName()) != null) {
			this.utl.dbDropFunction(function.name);
		}
		final String str = function.loaddDdl(this.dbMetadata);
		this.utl.execute(str.replaceAll("\r", ""));
	}

}