package com.jiuqi.dna.core.internal.db.support.kingbase.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.Exactly;
import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.NotSuggest;
import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.Overflow;
import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.Unable;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jiuqi.dna.core.def.table.TableType;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.IndexItemImpl;
import com.jiuqi.dna.core.impl.NullExpr;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.support.kingbase.KingbaseMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbStructCtl;
import com.jiuqi.dna.core.internal.db.sync.DbSyncBase;
import com.jiuqi.dna.core.internal.db.sync.DefaultFormat;
import com.jiuqi.dna.core.internal.db.sync.SqlBuilder;
import com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DateParser;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public class KingbaseUtl
		extends
		DbStructCtl<KingbaseMetadata, KingbaseTable, KingbaseColumn, KingbaseDataType, KingbaseIndex> {

	KingbaseUtl(PooledConnection conn, KingbaseMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
	}

	public static final void quote(Appendable str, String name) {
		KingbaseMetadata.quote(str, name);
	}

	/**
	 * 查询表的列定义
	 * 
	 * <p>
	 * 参数
	 * <ol>
	 * <li>表名
	 * </ol>
	 * 
	 * <p>
	 * 输出:
	 * <ol>
	 * <li>column_name 列名
	 * <li>data_type 数据类型
	 * <li>data_length 数据长度
	 * <li>data_precision 数据精度
	 * <li>data_scale 小数位
	 * <li>nullable 可否为空
	 * <li>data_default 默认值
	 * </ol>
	 */
	static final String SELECT_TABLE_COLUMNS = "select a.column_name,a.data_type,character_maximum_length data_length, numeric_precision data_precision, numeric_scale data_scale,case WHEN is_nullable='YES' THEN 'Y' WHEN IS_NULLABLE='NO' THEN 'N' END nullable, column_default data_default from INFORMATION_SCHEMA.COLUMNS a where TABLE_SCHEMA != 'SYS_CATALOG' AND TABLE_SCHEMA != 'INFORMATION_SCHEMA' AND table_name = ? order by ORDINAL_POSITION";

	@Override
	protected void loadColumns(KingbaseTable dbtable) throws SQLException {
		// // HCL Auto-generated method stub
		// PreparedStatement ps = pc.prepareStatement(SELECT_TABLE_COLUMNS);
		// try {
		// ps.setString(1, this.name);
		// ResultSet rs = ps.executeQuery();
		// try {
		// while (rs.next()) {
		// String column = rs.getString(1);
		// this.addColumn(column).load(rs);
		// }
		// } finally {
		// rs.close();
		// }
		// } finally {
		// ps.close();
		// }
	}

	/**
	 * 查询索引列信息
	 * 
	 * <p>
	 * JDBC接口中的DatabaseMetaData的getColumns()方法,对于降序的索引列,不能正确返回列名.
	 * 所以直接查询Kingbase字典. 对于降序索引,从refer列中读取列名.
	 * 
	 * <p>
	 * 参数
	 * <ol>
	 * <li>模式名称即用户名称(schema)
	 * <li>索引所在表
	 * </ol>
	 * 
	 * <p>
	 * 输出：
	 * <ol>
	 * <li>indexname 索引名
	 * <li>is_unique 是否唯一
	 * <li>indexdef 建索引语句
	 * </ol>
	 */
	private static final String SELECT_INDEX_COLUMNS = "select i.indexname, case when instr(i.indexdef,' UNIQUE ')>0 then 1 else 0 end as is_unique, i.indexdef from sys_indexes i where (i.schemaname>'SYS_CATALOG' OR i.schemaname<'SYS_CATALOG')  and i.tablename = ? ";

	/**
	 * 查询表的主键约束名称
	 * 
	 * <p>
	 * 参数
	 * <ol>
	 * <li>表名
	 * </ol>
	 * 
	 * <p>
	 * 输出:
	 * <ol>
	 * <li>主键约束名称
	 * </ol>
	 */
	private static final String SELECT_PK_CON = "select indexname from sys_primarykey_indexes where relname = ?";

	@Override
	protected void loadIndexes(KingbaseTable dbtable) throws SQLException {
		// PreparedStatement ps = pc.prepareStatement(SELECT_INDEX_COLUMNS);
		// try {
		// ps.setString(1, this.name);
		// ResultSet rs = ps.executeQuery();
		// try {
		// while (rs.next()) {
		// final String indexName = rs.getString(1);
		// KingbaseIndex index = this.findIndex(indexName);
		// String cols = rs.getString(3).substring(
		// rs.getString(3).indexOf('(') + 1,
		// rs.getString(3).length() - 1);
		// final int colsLength = cols.split(",").length;
		// boolean desc = false;
		// for (int i = 0; i < colsLength; i++) {
		// String colName = null;
		// final int colsPoint = cols.indexOf(',');
		// if (colsPoint < 0) {
		// desc = cols.contains(" DESC");
		// if (desc) {
		// colName = cols.substring(0, cols.length() - 4)
		// .trim();
		// } else {
		// colName = cols.trim();
		// }
		// if (index == null) {
		// index = this.addIndex(indexName,
		// rs.getBoolean(2));
		// }
		// final KingbaseColumn column = this
		// .getColumn(colName);
		// index.add(column, desc);
		// break;
		// } else if (cols.substring(0, colsPoint).trim()
		// .contains(" DESC")) {
		// colName = cols.substring(0, colsPoint - 4).trim();
		// desc = true;
		// } else {
		// colName = cols.substring(0, colsPoint).trim();
		// // desc = true;
		// }
		// if (index == null) {
		// index = this.addIndex(indexName, rs.getBoolean(2));
		// }
		// final KingbaseColumn column = this.getColumn(colName);
		// index.add(column, desc);
		// cols = cols.substring(colsPoint + 1);
		// }
		// }
		// } finally {
		// rs.close();
		// }
		// } finally {
		// ps.close();
		// }
		// this.primary = null;
		// ps = pc.prepareStatement(SELECT_PK_CON);
		// try {
		// ps.setString(1, this.name);
		// ResultSet rs = ps.executeQuery();
		// try {
		// if (rs.next()) {
		// this.primary = this.getIndex(rs.getString(1));
		// }
		// } finally {
		// rs.close();
		// }
		// } finally {
		// ps.close();
		// }
	}

	@Override
	public boolean tableContainRows(String table) throws SQLException {
		// HCL Auto-generated method stub
		return false;
	}

	@Override
	public final boolean existsTable(String tableName) throws SQLException {
		PreparedStatementWrap ps = this.conn.prepareStatement("select 1 from user_tables where table_name = ? and rownum <= 1", SqlSource.CORE_DML);
		try {
			ps.setString(1, tableName);
			return ps.exist();
		} finally {
			ps.close();
		}
	}

	@Override
	public final void createTable(DBTableDefineImpl define) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.appendCreate().appendTable().appendId(define.namedb()).lp();
		sql.nNewline().pi();
		for (TableFieldDefineImpl field : define.owner.fields) {
			if (field.dbTable == define || field.isRECID()) {
				columnDefinition(sql, field, false);
				sql.nComma().nNewline();
			}
		}
		outlineRecidConstraint(sql, define);
		sql.nNewline().ri().rp();
		if (define.isPrimary() && define.owner.isPartitioned()) {
			final int c = define.owner.partfields.size();
			sql.nNewline();
			sql.append("partition by range (");
			for (int i = 0; i < c; i++) {
				TableFieldDefineImpl pf = define.owner.partfields.get(i);
				sql.appendId(pf.namedb());
				sql.nComma().nSpace();
			}
			sql.uComma().append(')');
			sql.nNewline();
			sql.append('(');
			sql.append("partition values less than (");
			for (int i = 0; i < c; i++) {
				sql.append("maxvalue");
				sql.nComma().nSpace();
			}
			sql.uComma().append(")) enable row movement");
		}
		this.execute(sql);
	}

	private static final void columnDefinition(SqlBuilder sql,
			TableFieldDefineImpl field, boolean forceNullable) {
		sql.appendId(field.namedb()).append(' ');
		sql.appendType(field.getType());
		if (field.getDefault() != null) {
			sql.append(" default ");
			sql.append(defaultDefinition(field, defaultDeclare));
		}
		if (!forceNullable && field.isKeepValid()) {
			sql.append("  not null");
		}
	}

	private static final void columnAlter(SqlBuilder sql,
			TableFieldDefineImpl field, boolean forceNullable) {
		sql.appendId(field.namedb()).append(' ');
		sql.appendType(field.getType());
		if (field.getDefault() != null) {
			sql.append(" ,alter column ");
			sql.appendId(field.namedb()).append(' ');
			sql.append(" set default ");
			sql.append(defaultDefinition(field, defaultDeclare));
		}
		if (!forceNullable && field.isKeepValid()) {
			sql.append(" ,alter column ");
			sql.append(field.namedb()).append(' ');
			sql.append("  set not null");
		}
	}

	private static final void columnModification(SqlBuilder sql,
			KingbaseDbRefactor.ModifyFieldState state) {
		int fieldAdd = 0;
		sql.appendId(state.column.name);
		if (state.get(DbSyncBase.MOD_TYPE)) {
			sql.append(" type ").appendType(state.field.getType());
			fieldAdd++;
		}
		if (state.get(DbSyncBase.MOD_DEFAULT)) {
			if (fieldAdd > 0) {
				sql.append(" , alter column ").append(state.column.name);
			}
			final ConstExpr df = state.field.getDefault();
			if (df != null) {
				sql.append(" set default ");
				sql.append(defaultDefinition(state.field, defaultDeclare));
			} else {
				sql.append(" drop default");
			}
			fieldAdd++;
		}
		if (state.get(DbSyncBase.MOD_NULLABLE)) {
			if (fieldAdd > 0) {
				sql.append(" , alter column ").append(state.column.name);
			}
			if (state.field.isKeepValid()) {
				sql.append(" set not null");
			} else {
				sql.append(" drop not null");
			}
		}
	}

	private static final DefaultFormat defaultDeclare = new DefaultFormat() {

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "timestamp\'" + DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		protected final String formatSql(byte[] value) {
			return new StringBuffer("X'").append(Convert.bytesToHex(value, false, false)).append('\'').toString();
		}
	};

	@Override
	public final void dropTableSilently(String tableName) throws SQLException {
		if (this.existsTable(tableName)) {
			this.dropTable(tableName);
		}
	}

	final boolean dbExistsIndex(String indexName) {
		try {
			PreparedStatementWrap ps = this.conn.prepareStatement("select 1 from sys_indexes where indexname = ?", SqlSource.CORE_DML);
			try {
				ps.setString(1, indexName);
				return ps.exist();
			} finally {
				ps.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public final void dropIndex(KingbaseIndex index) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.appendDrop().appendIndex().appendId(index.name);
		this.execute(sql);
	}

	private static final String SELECT_OBJECT_STATUS = "select status from user_objects where object_name = ? and object_type = ?";
	private static final String OBJECT_TYPE_FUNCTION = "FUNCTION";

	final String dbGetFunctionStatus(String functionName) {
		try {
			PreparedStatementWrap ps = this.conn.prepareStatement(SELECT_OBJECT_STATUS, SqlSource.CORE_DML);
			try {
				ps.setString(1, functionName);
				ps.setString(2, OBJECT_TYPE_FUNCTION);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return rs.getString(1);
				}
				return null;
			} finally {
				ps.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	final void dbDropFunction(String functionName) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop function ");
		this.dbMetadata.quoteId(sql, functionName);
		this.execute(sql);
	}

	@Override
	public final boolean defaultChanged(TableFieldDefineImpl field,
			KingbaseColumn column) {
		final ConstExpr c = field.getDefault();
		final boolean leftNull = c == null || c == NullExpr.NULL;
		final boolean rightNull = column.defaultDefinition == null;
		if (leftNull != rightNull || (!leftNull && !rightNull && !column.defaultDefinition.equals(defaultDefinition(field, defaultDeclare)))) {
			return true;
		}
		return false;
	}

	@Override
	public final void renameColumnAndSetNullable(KingbaseColumn column,
			String rename) throws SQLException {
		SqlBuilder renameSql = new SqlBuilder(this.dbMetadata);
		renameSql.append("alter table ").appendId(column.table.name);
		renameSql.append(" rename column ").appendId(column.name).append(" to ").appendId(rename);
		this.execute(renameSql);
		if (column.notNull) {
			SqlBuilder setNullable = new SqlBuilder(this.dbMetadata);
			setNullable.append("alter table ").appendId(column.table.name);
			setNullable.append(" alter column ").appendId(rename).append(" drop not null");
			this.execute(setNullable);
		}
	}

	@Override
	public final boolean columnContainNull(KingbaseColumn column) {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		this.dbMetadata.quoteId(sql, column.table.name);
		sql.append(" where ");
		this.dbMetadata.quoteId(sql, column.name);
		sql.append(" is null and rownum <= 1");
		try {
			PreparedStatementWrap ps = this.conn.prepareStatement(sql.toString(), SqlSource.CORE_DML);
			try {
				return ps.exist();
			} finally {
				ps.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	final void dbDropColumns(KingbaseTable dbtable,
			KingbaseDbRefactor.ColumnCompareCache compareCache)
			throws SQLException {
		if (compareCache.dropQueue.size() > 0) {
			SqlBuilder sql = new SqlBuilder(this.dbMetadata);
			sql.append("alter table ").appendId(dbtable.name);
			for (int i = 0, c = compareCache.dropQueue.size(); i < c; i++) {
				sql.append(" drop ");
				sql.appendId(compareCache.dropQueue.get(i).name).nComma().nSpace();
			}
			sql.uComma().uSpace().ri();
			this.execute(sql);
		}
	}

	final void dbAddnModifyColumn(KingbaseTable dbtable,
			KingbaseDbRefactor.ColumnCompareCache compareCache)
			throws SQLException {
		if (compareCache.addQueue.size() == 0 && compareCache.modifyQueue.size() == 0) {
			return;
		}
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		int fieldAdd = 0;
		sql.append("alter table ").appendId(dbtable.name);
		if (compareCache.addQueue.size() > 0) {
			for (KingbaseDbRefactor.AddFieldState state : compareCache.addQueue) {
				if (fieldAdd > 0) {
					sql.append(",");
				}
				sql.append(" add column ");
				columnAlter(sql, state.field, state.forceNullable);
				fieldAdd++;
			}
		}
		if (compareCache.modifyQueue.size() > 0) {
			for (KingbaseDbRefactor.ModifyFieldState state : compareCache.modifyQueue.values()) {
				if (fieldAdd > 0) {
					sql.append(",");
				}
				sql.append(" alter column ");
				columnModification(sql, state);
				fieldAdd++;
			}
		}
		this.execute(sql);
	}

	@Override
	public final TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
			KingbaseColumn column) {
		return field.getType().detect(compatible, column);
	}

	// for Kingbase 10
	private static final TypeDetector<TypeCompatiblity, KingbaseColumn> compatible = new TypeDetectorBase<TypeCompatiblity, KingbaseColumn>() {

		@Override
		public TypeCompatiblity inBoolean(KingbaseColumn column)
				throws Throwable {
			if (column.type == KingbaseDataType.NUMBER && column.scale == 0) {
				if (column.precision == 1) {
					return Exactly;
				}
				return Overflow;
			} else if (column.type == KingbaseDataType.FLOAT) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(KingbaseColumn column) throws Throwable {
			if (column.type == KingbaseDataType.NUMBER && column.scale == 0) {
				if (column.precision == 16) {
					return Exactly;
				} else if (column.precision > 16 || column.precision == 0) {
					return Overflow;
				}
			} else if (column.type == KingbaseDataType.FLOAT) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(KingbaseColumn column) throws Throwable {
			if (column.type == KingbaseDataType.NUMBER && column.scale == 0) {
				if (column.precision <= 32 && column.type != KingbaseDataType.FLOAT) {
					return Exactly;
				} else if (column.precision > 32 || column.precision == 0) {
					return Overflow;
				}
			} else if (column.type == KingbaseDataType.FLOAT) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(KingbaseColumn column) throws Throwable {
			if ((column.type == KingbaseDataType.BIGINT || column.type == KingbaseDataType.NUMBER) && column.scale == 0) {
				if (column.precision == 64 || column.precision == 32 || column.precision == 16) {
					return Exactly;
				} else if (column.precision > 64 || column.precision == 0) {
					return Overflow;
				}
			} else if (column.type == KingbaseDataType.FLOAT) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(KingbaseColumn column) throws Throwable {
			if ((column.type == KingbaseDataType.FLOAT || column.type == KingbaseDataType.NUMBER) && (column.precision <= 24 || column.precision == 32 || column.precision == 64)) {
				return Exactly;
			} else if (column.type == KingbaseDataType.BINARY_FLOAT || column.type == KingbaseDataType.BINARY_DOUBLE || column.type == KingbaseDataType.FLOAT) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(KingbaseColumn column)
				throws Throwable {
			if ((column.type == KingbaseDataType.FLOAT || column.type == KingbaseDataType.NUMBER) && (column.precision <= 53 || column.precision == 64)) {
				return Exactly;
			} else if (column.type == KingbaseDataType.BINARY_FLOAT || column.type == KingbaseDataType.BINARY_DOUBLE) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(KingbaseColumn column, int precision,
				int scale) throws Throwable {
			if (column.type == KingbaseDataType.NUMBER) {
				if (((column.precision / 3.2) == precision || (column.precision / 3.2) - 1 == precision) && column.scale == scale) {
					return Exactly;
				} else if (((column.precision - column.scale) >= (precision - scale)) && column.scale >= scale) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inChar(KingbaseColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == KingbaseDataType.CHAR || column.type == KingbaseDataType.VARCHAR || column.type == KingbaseDataType.VARCHAR2) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			} else if (column.type == KingbaseDataType.NCHAR || column.type == KingbaseDataType.NVARCHAR || column.type == KingbaseDataType.NVARCHAR2) {
				if (column.length * 2 >= length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(KingbaseColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == KingbaseDataType.CHAR || column.type == KingbaseDataType.VARCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			} else if (column.type == KingbaseDataType.NCHAR || column.type == KingbaseDataType.NVARCHAR) {
				if (column.length * 2 >= length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(KingbaseColumn column) throws Throwable {
			if (column.type == KingbaseDataType.TEXT) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(KingbaseColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == KingbaseDataType.NCHAR || column.type == KingbaseDataType.NVARCHAR2 || column.type == KingbaseDataType.NVARCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			} else if (column.type == KingbaseDataType.CHAR || column.type == KingbaseDataType.VARCHAR2 || column.type == KingbaseDataType.VARCHAR) {
				if (length * 2 <= column.length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(KingbaseColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == KingbaseDataType.NCHAR || column.type == KingbaseDataType.NVARCHAR2 || column.type == KingbaseDataType.NVARCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			} else if (column.type == KingbaseDataType.CHAR || column.type == KingbaseDataType.VARCHAR2 || column.type == KingbaseDataType.VARCHAR) {
				if (length * 2 <= column.length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(KingbaseColumn column) throws Throwable {
			if (column.type == KingbaseDataType.NCLOB || column.type == KingbaseDataType.CLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(KingbaseColumn column,
				SequenceDataType type) throws Throwable {
			if (column.type == KingbaseDataType.BYTEA) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(KingbaseColumn column,
				SequenceDataType type) throws Throwable {
			if (column.type == KingbaseDataType.BYTEA) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(KingbaseColumn column) throws Throwable {
			if (column.type == KingbaseDataType.BLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(KingbaseColumn column) throws Throwable {
			if (column.type == KingbaseDataType.BYTEA) {
				if (column.length == 16) {
					return Exactly;
				} else if (column.length >= 0) {
					return Exactly;
				} else if (column.length > 16) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(KingbaseColumn column) throws Throwable {
			if (column.type == KingbaseDataType.TIMESTAMP) {
				return Exactly;
			} else if (column.type == KingbaseDataType.TIMESTAMP_WITH_TIME_ZONE || column.type == KingbaseDataType.TIMESTAMP_WITH_LOCAL_TIME_ZONE) {
				return Overflow;
			}
			return Unable;
		}

	};

	private static final String SELECT_PROC_NAME = "select 1 from user_objects where object_name = ? and object_type = 'PROCEDURE'";

	final boolean dbExistsProcedure(StoredProcedureDefineImpl procedure) {
		try {
			PreparedStatementWrap ps = this.conn.prepareStatement(SELECT_PROC_NAME, SqlSource.CORE_DML);
			try {
				ps.setString(1, procedure.getName());
				return ps.exist();
			} finally {
				ps.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	protected final KingbaseTable newDbTableOnly(String name) {
		return new KingbaseTable(name);
	}

	@Override
	public final boolean indexColumnContainNull(IndexDefineImpl index)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, index.dbTable.namedb());
		sql.append(" t where ");
		final int c = index.items.size();
		if (c > 1) {
			sql.append("(");
		}
		for (int i = 0; i < c; i++) {
			IndexItemImpl item = index.items.get(i);
			if (i > 0) {
				sql.append(" or ");
			}
			sql.append("t.");
			quote(sql, item.getField().namedb());
			sql.append(" is null");
		}
		if (c > 1) {
			sql.append(")");
		}
		sql.append(" and rownum <= 1");
		return this.exist(sql);
	}

	@Override
	public final boolean indexValueDuplicated(IndexDefineImpl index)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, index.dbTable.namedb());
		sql.append(" t where exists (select 1 from ");
		quote(sql, index.dbTable.namedb());
		sql.append(" i where t.recid <> i.recid");
		for (IndexItemImpl item : index.items) {
			final String fn = item.getField().namedb();
			sql.append(" and (t.");
			quote(sql, fn);
			sql.append(" is null and i.");
			quote(sql, fn);
			sql.append(" is null or t.");
			quote(sql, fn);
			sql.append(" = i.");
			quote(sql, fn);
			sql.append(")");
		}
		sql.append(") and rownum <= 1");
		return this.exist(sql);
	}

	final void createCollateGBK() throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("create table ");
		this.dbMetadata.quoteId(sql, CORE_COLLATE_GBK);
		sql.append(" (");
		this.dbMetadata.quoteId(sql, CORE_COLLATE_GBK_CH);
		sql.append(" varchar(2) not null primary key, ");
		this.dbMetadata.quoteId(sql, CORE_COLLATE_GBK_SN);
		sql.append(" bytea)");
		this.execute(sql, SqlSource.CORE_DDL);
	}

	final void initCollateGBK() throws SQLException, IOException {
		PreparedStatementWrap merge = this.conn.prepareStatement("merge into " + CORE_COLLATE_GBK + " t using (select 1 from dual) on (t.CH = ?) when not matched then insert (CH,SN) values (?,?)", SqlSource.CORE_DML);
		try {
			initCollateGBK(merge, new CollateSetter() {
				public void set(final PreparedStatementWrap ps, String ch,
						byte[] b) throws SQLException {
					ps.setString(1, ch);
					ps.setString(2, ch);
					ps.setBytes(3, b);
				}
			}, 500);
		} finally {
			merge.close();
		}
	}

	@Override
    public TableType loadTableType(String tableName) throws SQLException{
	    return TableType.NORMAL;
    }
}
