package com.jiuqi.dna.core.internal.db.support.mysql.sync;

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
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.support.mysql.MysqlMetadata;
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

public class MysqlStructCtl
		extends
		DbStructCtl<MysqlMetadata, MysqlTable, MysqlColumn, MysqlDataType, MysqlIndex> {

	MysqlStructCtl(PooledConnection conn, MysqlMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
		this.loadColumns = this.prepareStatement(SELECT_TABLE_COLUMNS);
		this.loadIndexes = this.prepareStatement(SELECT_INDEX_COLUMNS);
		this.existTable = this.prepareStatement(EXIST_TABLE);
		this.existRoutine = this.prepareStatement(EXIST_ROUTINES);
	}

	public static final void quote(Appendable str, String name) {
		MysqlMetadata.quote(str, name);
	}

	@Override
	protected final MysqlTable newDbTableOnly(String name) {
		return new MysqlTable(name);
	}

	/**
	 * 查询表的字段定义
	 * 
	 * <ol>
	 * <li>column_name 列名
	 * <li>data_type 数据类型
	 * <li>column_type 数据类型声明
	 * <li>character_maximum_length 字符串长度
	 * <li>character_set_name 字符串字符集
	 * <li>numeric_precision 数值精度
	 * <li>numeric_scale 数值小数位
	 * <li>is_nullable
	 * <li>column_default 默认值
	 * <li>column_key 键
	 * </ol>
	 */
	private static final String SELECT_TABLE_COLUMNS = "select column_name, data_type, column_type, character_maximum_length, character_set_name, numeric_precision, numeric_scale, is_nullable, column_default, column_key from information_schema.columns where table_schema = ? and table_name = ? order by ordinal_position";
	private final PreparedStatementWrap loadColumns;

	@Override
	protected final void loadColumns(MysqlTable dbtable) throws SQLException {
		try {
			this.loadColumns.setString(1, this.dbMetadata.database);
			this.loadColumns.setString(2, dbtable.name);
			ResultSet rs = this.loadColumns.executeQuery();
			try {
				while (rs.next()) {
					final String columnName = rs.getString(1);
					final MysqlColumn column = dbtable.addColumn(columnName);
					loadColumn(rs, column);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadColumns.clearParameters();
		}
	}

	private static final void loadColumn(ResultSet rs, MysqlColumn column)
			throws SQLException {
		column.type = MysqlDataType.typeOf(rs.getString(2));
		// for lob
		column.length = (int) rs.getLong(4);
		column.charset = rs.getString(5);
		column.precision = rs.getInt(6);
		column.scale = rs.getInt(7);
		if (rs.getString(2).indexOf("unsigned") > 0) {
			throw new UnsupportedOperationException();
		}
		column.notNull = rs.getString(8).equals("NO");
		String defaultValue = rs.getString(9);
		if (defaultValue != null) {
			column.defaultDefinition = defaultValue.trim();
			if (column.defaultDefinition.length() == 0) {
				column.defaultDefinition = null;
			}
		} else {
			column.defaultDefinition = null;
		}
	}

	/**
	 * 查询索引列信息
	 * 
	 * <ol>
	 * <li>index_name
	 * <li>non_unique
	 * <li>column_name
	 * </ol>
	 */
	private static final String SELECT_INDEX_COLUMNS = "select index_name, non_unique, column_name from information_schema.statistics where table_schema = ? and table_name = ? order by index_name, seq_in_index";
	private final PreparedStatementWrap loadIndexes;

	@Override
	protected final void loadIndexes(MysqlTable dbtable) throws SQLException {
		try {
			this.loadIndexes.setString(1, this.dbMetadata.database);
			this.loadIndexes.setString(2, dbtable.name);
			ResultSet rs = this.loadIndexes.executeQuery();
			try {
				while (rs.next()) {
					final String indexName = rs.getString(1);
					MysqlIndex index = dbtable.findIndex(indexName);
					if (index == null) {
						final boolean unique = rs.getInt(2) == 0;
						index = dbtable.addIndex(indexName, unique);
					}
					final MysqlColumn column = dbtable.getColumn(rs.getString(3));
					index.add(column, false);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadIndexes.clearParameters();
		}
	}

	@Override
	public final boolean tableContainRows(String table) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, table);
		sql.append(" limit 1");
		return this.exist(sql);
	}

	private static final String EXIST_TABLE = "select 1 from information_schema.tables where table_schema = ? and table_name = ?";
	private final PreparedStatementWrap existTable;

	@Override
	public final boolean existsTable(String table) throws SQLException {
		try {
			this.existTable.setString(1, this.dbMetadata.database);
			this.existTable.setString(2, table);
			ResultSet rs = this.existTable.executeQuery();
			try {
				if (rs.next()) {
					return true;
				}
				return false;
			} finally {
				rs.close();
			}
		} finally {
			this.existTable.clearParameters();
		}
	}

	@Override
	public final void createTable(DBTableDefineImpl define) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.appendCreate().appendTable().appendId(define.name).lp();
		sql.nNewline().pi();
		for (TableFieldDefineImpl field : define.owner.fields) {
			if (field.isRECID()) {
				sql.appendId(field.namedb()).nSpace();
				sql.appendType(field.getType()).nSpace();
				sql.appendNot().appendNull().nSpace();
				sql.appendPrimaryKey();
			} else if (field.dbTable == define) {
				appendAddColumnDefinition(sql, field, false);
			}
			sql.nComma().nNewline();
		}
		sql.uComma().ri().rp();
		this.execute(sql);
	}

	@Override
	public final void dropTableSilently(String tableName) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop table if exists ");
		quote(sql, tableName);
		this.execute(sql.toString());
	}

	final void dbAddnAlterColumns(MysqlTable dbtable,
			MysqlDbSync.ColumnCompareCache compareCache) throws SQLException {
		if (compareCache.addQueue.size() == 0 && compareCache.modifyQueue.size() == 0) {
			return;
		}
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("alter table ").appendId(dbtable.name);
		if (compareCache.addQueue.size() > 0) {
			sql.append(" add column (");
			for (MysqlDbSync.AddFieldState state : compareCache.addQueue) {
				appendAddColumnDefinition(sql, state.field, state.forceNullable);
				sql.nComma();
			}
			sql.uComma().append(')');
			sql.nComma();
		}
		if (compareCache.modifyQueue.size() > 0) {
			for (MysqlDbSync.ModifyFieldState state : compareCache.modifyQueue.values()) {
				sql.append(" modify column ");
				TableFieldDefineImpl field = state.field;
				sql.appendId(field.namedb()).nSpace();
				sql.appendType(field.getType()).nSpace();
				if (state.get(DbSyncBase.MOD_NULLABLE)) {
					if (field.isKeepValid()) {
						sql.appendNot().appendNull().nSpace();
					} else {
						sql.appendNull().nSpace();
					}
				}
				if (state.get(DbSyncBase.MOD_DEFAULT)) {
					if (field.getDefault() != null) {
						sql.appendDefault();
						sql.append(defaultDefinition(field, defaultDeclare));
						sql.nSpace();
					} else if (!state.get(DbSyncBase.MOD_NULLABLE) || !field.isKeepValid()) {
						sql.appendDefault().appendNull().nSpace();
					}
				}
				sql.nComma();
			}
		}
		sql.uComma();
		this.execute(sql);
	}

	/**
	 * 格式化增加列的sql语句定义
	 * 
	 * @param sql
	 *            sql语句对象
	 * @param field
	 *            需要增加的列定义
	 * @param forceNullable
	 *            是否强制列可为空
	 */
	private static final void appendAddColumnDefinition(SqlBuilder sql,
			TableFieldDefineImpl field, boolean forceNullable) {
		sql.appendId(field.namedb()).nSpace();
		sql.appendType(field.getType()).nSpace();
		if (!forceNullable && field.isKeepValid()) {
			sql.appendNot().appendNull().nSpace();
		}
		if (field.getDefault() != null) {
			sql.appendDefault();
			sql.append(defaultDefinition(field, defaultDeclare));
			sql.nSpace();
		}
	}

	private static final DefaultFormat defaultDeclare = new DefaultFormat() {

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "timestamp\'" + DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		protected final String formatSql(byte[] value) {
			if (value.length == 0) {
				return "''";
			}
			return "0x" + Convert.bytesToHex(value, false, false);
		}
	};

	private static final DefaultFormat defaultCompare = new DefaultFormat() {

		@Override
		public String inString(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return escape(c.getString());
		}

		@Override
		public String inBoolean(ConstExpr c) throws Throwable {
			return c.getBoolean() ? "b'1'" : "b'0'";
		}

		@Override
		public String inNumeric(ConstExpr userData, int precision, int scale)
				throws Throwable {
			final String v = userData.toString();
			if (scale > 0) {
				int i = v.indexOf('.');
				if (i > 0) {
					int s = v.length() - i - 1;
					if (s >= scale) {
						return v;
					} else {
						final StringBuilder sb = new StringBuilder(v);
						while (s++ < scale) {
							sb.append('0');
						}
						return sb.toString();
					}
				} else {
					final StringBuilder sb = new StringBuilder(v);
					sb.append('.');
					while (--scale >= 0) {
						sb.append('0');
					}
					return sb.toString();
				}
			}
			return v;
		}

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "timestamp\'" + DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		protected final String formatSql(byte[] value) {
			return "unhex('" + Convert.bytesToHex(value, false, false) + "\')";
		}
	};

	@Override
	public final boolean columnContainNull(MysqlColumn column)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, column.table.name);
		sql.append(" where ");
		quote(sql, column.name);
		sql.append(" is null limit 1");
		return this.exist(sql);
	}

	@Override
	public final boolean defaultChanged(TableFieldDefineImpl field,
			MysqlColumn column) {
		final boolean leftNull = field.getDefault() == null;
		final boolean rightNull = column.defaultDefinition == null;
		if (!leftNull && !rightNull && !column.defaultDefinition.equals(defaultDefinition(field, defaultCompare))) {
			return true;
		}
		return false;
	}

	@Override
	public final TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
			MysqlColumn column) {
		return field.getType().detect(compatible, column);
	}

	private static final TypeDetector<TypeCompatiblity, MysqlColumn> compatible = new TypeDetectorBase<TypeCompatiblity, MysqlColumn>() {

		@Override
		public TypeCompatiblity inBoolean(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.BIT || column.type == MysqlDataType.TINYINT) {
				return Exactly;
			} else if (column.type == MysqlDataType.SMALLINT || column.type == MysqlDataType.MEDIUMINT || column.type == MysqlDataType.INT || column.type == MysqlDataType.BIGINT) {
				return Overflow;
			} else if (column.type == MysqlDataType.DECIMAL && column.precision >= 1 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.SMALLINT) {
				return Exactly;
			} else if (column.type == MysqlDataType.MEDIUMINT || column.type == MysqlDataType.INT || column.type == MysqlDataType.BIGINT) {
				return Overflow;
			} else if (column.type == MysqlDataType.DECIMAL && column.precision >= 5 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.INT) {
				return Exactly;
			} else if (column.type == MysqlDataType.BIGINT) {
				return Overflow;
			} else if (column.type == MysqlDataType.DECIMAL && column.precision >= 10 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.BIGINT) {
				return Exactly;
			} else if (column.type == MysqlDataType.DECIMAL && column.precision >= 19 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.FLOAT) {
				return Exactly;
			} else if (column.type == MysqlDataType.DOUBLE) {
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.DOUBLE) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(MysqlColumn column, int precision,
				int scale) throws Throwable {
			if (column.type == MysqlDataType.DECIMAL) {
				if (column.precision == precision && column.scale == scale) {
					return Exactly;
				} else if (((column.precision - column.scale) >= (precision - scale)) && column.scale >= scale) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inChar(MysqlColumn column, SequenceDataType type)
				throws Throwable {
			if (column.type == MysqlDataType.CHAR && !column.national()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(MysqlColumn column,
				SequenceDataType type) throws Throwable {
			if (column.type == MysqlDataType.VARCHAR && !column.national()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.LONGTEXT && !column.national()) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(MysqlColumn column,
				SequenceDataType type) throws Throwable {
			if (column.type == MysqlDataType.CHAR && column.national()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(MysqlColumn column,
				SequenceDataType type) throws Throwable {
			if (column.type == MysqlDataType.VARCHAR && column.national()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.LONGTEXT && column.national()) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(MysqlColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == MysqlDataType.BINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			} else if (column.type == MysqlDataType.VARBINARY && column.length >= length) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(MysqlColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == MysqlDataType.VARBINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.LONGBLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.BINARY && column.length == 16) {
				return Exactly;
			} else if (column.type == MysqlDataType.VARBINARY && column.length >= 16) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(MysqlColumn column) throws Throwable {
			if (column.type == MysqlDataType.TIMESTAMP) {
				return Exactly;
			} else if (column.type == MysqlDataType.DATETIME) {
				return Exactly;
			}
			return Unable;
		}

	};

	@Override
	public final void renameColumnAndSetNullable(MysqlColumn column,
			String rename) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("alter table ").appendId(column.table.name);
		sql.append(" change column ").appendId(column.name);
		sql.append(' ').appendId(rename).append(' ');
		column.defineType(sql, column);
		if (column.notNull) {
			sql.append(" null");
		}
		this.execute(sql);
	}

	final void dbDropColumns(MysqlTable dbtable,
			MysqlDbSync.ColumnCompareCache compareCache) throws SQLException {
		if (compareCache.dropQueue.size() == 0) {
			return;
		}
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("alter table ").appendId(dbtable.name).nNewline().pi();
		for (int i = 0, c = compareCache.dropQueue.size(); i < c; i++) {
			sql.appendDrop().appendColumn().appendId(compareCache.dropQueue.get(i).name).nComma();
		}
		sql.uComma();
		this.execute(sql);
	}

	final void dbDropFunction(String functionName) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop function ");
		quote(sql, functionName);
		this.execute(sql);
	}

	private static final String EXIST_ROUTINES = "select 1 from information_schema.ROUTINES where routine_schema = ? and routine_type = ? and routine_name = ? ";
	private final PreparedStatementWrap existRoutine;

	static final String ROUTINE_TYPE_FUNCTION = "FUNCTION";
	static final String ROUTINE_TYPE_PROCEDURE = "PROCEDURE";

	final boolean existsRoutine(String type, String name) throws SQLException {
		try {
			this.existRoutine.setString(1, this.dbMetadata.database);
			this.existRoutine.setString(2, type);
			this.existRoutine.setString(3, name);
			return this.existRoutine.exist();
		} finally {
			this.existRoutine.clearParameters();
		}
	}

	final boolean existsFunction(String function) throws SQLException {
		return this.existsRoutine(ROUTINE_TYPE_FUNCTION, function);
	}

	final boolean existsProcedure(String procedure) throws SQLException {
		return this.existsRoutine(ROUTINE_TYPE_PROCEDURE, procedure);
	}

	final void dbDropProcedureIfExist(String procedureName) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop procedure if exists ");
		quote(sql, procedureName);
		this.execute(sql.toString());
	}

	@Override
	public final void dropIndex(MysqlIndex index) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop index ");
		quote(sql, index.name);
		sql.append(" on ");
		quote(sql, index.table.name);
		this.execute(sql);
	}

	@Override
	public final boolean indexValueDuplicated(IndexDefineImpl index)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, index.dbTable.namedb());
		sql.append(" t group by ");
		for (int i = 0; i < index.items.size(); i++) {
			IndexItemImpl item = index.items.get(i);
			if (i > 0) {
				sql.append(", ");
			}
			sql.append("t.");
			quote(sql, item.getField().namedb());
		}
		sql.append(" having count(*) > 1 limit 1");
		return this.exist(sql);
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
		sql.append(" limit 1");
		return this.exist(sql);
	}

	final void createCollateGBK() throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("create table ");
		quote(sql, CORE_COLLATE_GBK);
		sql.append(" (");
		quote(sql, CORE_COLLATE_GBK_CH);
		sql.append(" varchar(2) primary key, ");
		quote(sql, CORE_COLLATE_GBK_SN);
		sql.append(" binary(2))");
		this.execute(sql.toString(), SqlSource.CORE_DDL);
	}

	final void initCollateGBK() throws SQLException, IOException {
		PreparedStatementWrap replace = this.conn.prepareStatement("replace into " + CORE_COLLATE_GBK + " (ch, sn) values (?, ?)", SqlSource.CORE_DML);
		try {
			initCollateGBK(replace, new CollateSetter() {
				public void set(PreparedStatementWrap ps, String ch, byte[] b)
						throws SQLException {
					ps.setString(1, ch);
					ps.setBytes(2, b);
				}
			}, 50);
		} finally {
			replace.close();
		}
	}

	@Override
    public TableType loadTableType(String tableName) throws SQLException{
	    return TableType.NORMAL;
    }
}