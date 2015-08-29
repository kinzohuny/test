package com.jiuqi.dna.core.internal.db.support.sqlserver.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.Exactly;
import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.NotSuggest;
import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.Overflow;
import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.Unable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.jiuqi.dna.core.def.table.TableType;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.IndexItemImpl;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;
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

public final class SqlserverStructCtl
		extends
		DbStructCtl<SqlserverMetadata, SqlserverTable, SqlserverColumn, SqlserverDataType, SqlserverIndex> {

	SqlserverStructCtl(PooledConnection conn, SqlserverMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
		this.loadColumns = this.prepareStatement(SELECT_TABLE_COLUMNS);
		this.loadIndexes = this.prepareStatement(SELECT_INDEX_COLUMNS);
		if (dbMetadata.beforeYukon()) {
			this.existsObject = this.prepareStatement(EXISTS_OBJECT_COMPATIBLE);
		} else {
			this.existsObject = this.prepareStatement(EXISTS_OBJECT);
		}
	}

	public static final void quote(Appendable str, String name) {
		SqlserverMetadata.quote(str, name);
	}

	@Override
	protected final SqlserverTable newDbTableOnly(String name) {
		return new SqlserverTable(name);
	}

	/**
	 * 查询表的列定义
	 * 
	 * <p>
	 * 参数
	 * <ol>
	 * <li>架构名
	 * <li>表名
	 * </ol>
	 * 
	 * <p>
	 * 输出:
	 * <ol>
	 * <li>name 列名
	 * <li>type_name 类型的名称
	 * <li>max_length 数据长度
	 * <li>precision 数据精度
	 * <li>scale 小数位
	 * <li>not_null 是否不为空
	 * <li>definition 默认值
	 * <li>default_name 默认值约束的名称
	 * <li>collation 排序规则
	 * </ol>
	 */
	private static final String SELECT_TABLE_COLUMNS = "select c.name, t.name, c.max_length, c.precision, c.scale, case c.is_nullable when 0 then 1 else 0 end not_null, d.definition default_value_definition, d.name default_constraint_name, c.collation_name from sys.schemas s inner join sys.tables o on s.schema_id = o.schema_id inner join sys.columns c on o.object_id = c.object_id inner join sys.types t on c.user_type_id = t.user_type_id left join sys.default_constraints d on c.default_object_id = d.object_id where s.name = ? and o.name = ? ";
	private final PreparedStatementWrap loadColumns;

	@Override
	protected final void loadColumns(SqlserverTable dbtable)
			throws SQLException {
		if (this.dbMetadata.beforeYukon()) {
			this.loadColumnsUsingJdbc(dbtable);
		} else {
			this.loadColumnsUsingSystemView(dbtable);
		}
	}

	private final void loadColumnsUsingSystemView(SqlserverTable dbtable)
			throws SQLException {
		try {
			this.loadColumns.setString(1, this.dbMetadata.schema);
			this.loadColumns.setString(2, dbtable.name);
			ResultSet rs = this.loadColumns.executeQuery();
			try {
				while (rs.next()) {
					SqlserverColumn column = dbtable.addColumn(rs.getString(1));
					column.type = SqlserverDataType.typeOf(rs.getString(2));
					final int length = rs.getInt(3);
					if (length == -1) {
						column.length = -1;
					} else if (column.type == SqlserverDataType.NCHAR || column.type == SqlserverDataType.NVARCHAR) {
						// HCL 非gbk编码时?!
						column.length = rs.getInt(3) / 2;
					} else {
						column.length = rs.getInt(3);
					}
					column.precision = rs.getInt(4);
					column.scale = rs.getInt(5);
					column.notNull = rs.getBoolean(6);
					column.defaultDefinition = rs.getString(7);
					column.defaultConstraint = rs.getString(8);
					column.collation = rs.getString(9);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadColumns.clearParameters();
		}
	}

	private final void loadColumnsUsingJdbc(SqlserverTable dbtable)
			throws SQLException {
		ResultSet rs = this.conn.getMetaData().getColumns(this.dbMetadata.database, this.dbMetadata.schema, dbtable.name, null);
		try {
			while (rs.next()) {
				final String name = rs.getString(4);
				SqlserverColumn column = dbtable.addColumn(name);
				final String type = rs.getString(6);
				column.type = SqlserverDataType.typeOf(type);
				final int length = rs.getInt(7);
				column.length = column.precision = length;
				column.scale = rs.getInt(9);
				String is_nullable = rs.getString(18).trim();
				column.notNull = is_nullable.equals("NO");
				String defaultDefinition = rs.getString(13);
				if (defaultDefinition != null) {
					column.defaultDefinition = defaultDefinition.trim();
					if (column.defaultDefinition.length() == 0) {
						column.defaultDefinition = null;
					} else {
						column.defaultConstraint = this.defaultConstraintNameOf(dbtable.name, column.name);
					}
				}
			}
		} finally {
			rs.close();
		}
	}

	private static final String SELECT_DEFAULT_CONSTRAINT_NAME = "select object_name(c.constid) from sysobjects o join sysconstraints c on o.id = c.id where c.status & 5 = 5 and o.name = ? and col_name(o.id, c.colid) = ?";

	/**
	 * 获取指定列的默认值约束名
	 * 
	 * @param table
	 * @param column
	 * @return
	 * @throws SQLException
	 *             列没有定义默认值时
	 */
	private final String defaultConstraintNameOf(String table, String column)
			throws SQLException {
		final PreparedStatementWrap pstmt = this.conn.prepareStatement(SELECT_DEFAULT_CONSTRAINT_NAME, SqlSource.CORE_DML);
		try {
			pstmt.setString(1, table);
			pstmt.setString(2, column);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
			throw new IllegalStateException("列[" + table + "." + column + "]没有默认值约束定义。");
		} finally {
			pstmt.close();
		}
	}

	/**
	 * 从sqlserver2005及以上版本的数据库中读取指定表的索引结构语句
	 * 
	 * 参数:
	 * <ol>
	 * <li>模式名
	 * <li>表名
	 * </ol>
	 * 
	 * 输出列:
	 * <ol>
	 * <li>INDEX_NAME 索引名称
	 * <li>IS_UNIQUE 是否唯一
	 * <li>INDEX_COLUMN 索引列
	 * <li>IS_DESC 是否降序
	 * <li>IS_PRIMARYKEY 是否主键索引
	 * </ol>
	 */
	private static final String SELECT_INDEX_COLUMNS = "select INDEX_NAME = i.name, IS_UNIQUE = i.is_unique, INDEX_COLUMN = index_col(t.name, i.index_id, ic.key_ordinal), IS_DESC = ic.is_descending_key, IS_PRIMARY_KEY = is_primary_key from sys.schemas s join sys.tables t on s.schema_id = t.schema_id join sys.indexes i on t.object_id = i.object_id join sys.index_columns ic on t.object_id = ic.object_id and i.index_id = ic.index_id where s.name = ? and t.name = ? order by ic.index_id, ic.key_ordinal";
	private final PreparedStatementWrap loadIndexes;

	@Override
	protected final void loadIndexes(SqlserverTable dbtable)
			throws SQLException {
		if (this.dbMetadata.beforeYukon()) {
			this.loadIndexesUsingJdbc(dbtable);
		} else {
			this.loadIndexesUsingSystemView(dbtable);
		}
	}

	private final void loadIndexesUsingSystemView(SqlserverTable dbtable)
			throws SQLException {
		dbtable.clearIndexes();
		try {
			this.loadIndexes.setString(1, this.dbMetadata.schema);
			this.loadIndexes.setString(2, dbtable.name);
			final ResultSet rs = this.loadIndexes.executeQuery();
			try {
				while (rs.next()) {
					final String indexName = rs.getString(1);
					final boolean desc = rs.getBoolean(4);
					final String columnName = rs.getString(3);
					SqlserverIndex index = dbtable.findIndex(indexName);
					if (index == null) {
						final boolean unique = rs.getBoolean(2);
						index = dbtable.addIndex(indexName, unique);
						if (rs.getBoolean(5)) {
							dbtable.primaryKey = index;
						}
					}
					SqlserverColumn column = dbtable.getColumn(columnName);
					index.add(column, desc);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadIndexes.clearParameters();
		}
	}

	private final void loadIndexesUsingJdbc(SqlserverTable dbtable)
			throws SQLException {
		dbtable.clearIndexes();
		final ResultSet rs = this.conn.getMetaData().getIndexInfo(this.dbMetadata.database, null, dbtable.name, false, false);
		try {
			while (rs.next()) {
				final String indexName = rs.getString(6);
				if (indexName == null || indexName.length() == 0) {
					continue;
				}
				// alwasys "A"
				final boolean desc = rs.getString(10).equals("D");
				final String columnName = rs.getString(9);
				SqlserverIndex index = dbtable.findIndex(indexName);
				if (index == null) {
					final boolean unique = rs.getInt(4) == 0;
					index = dbtable.addIndex(indexName, unique);
				}
				SqlserverColumn column = dbtable.getColumn(columnName);
				index.add(column, desc);
			}
		} finally {
			rs.close();
		}
		for (SqlserverIndex index : dbtable.indexes) {
			if (index.containsOnlyRecid()) {
				dbtable.primaryKey = index;
				break;
			}
		}
	}

	@Override
	public final boolean tableContainRows(String table) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select top 1 1 from ");
		quote(sql, table);
		return this.exist(sql);
	}

	private static final String EXISTS_OBJECT = "select 1 from sys.objects o join sys.schemas s on s.schema_id = o.schema_id where s.name = ? and o.type = ? and o.name = ?";
	private static final String EXISTS_OBJECT_COMPATIBLE = "select 1 from sysobjects o join sysusers u on o.uid = u.uid where u.name = ? and o.xtype = ? and o.name = ?";
	private PreparedStatementWrap existsObject;

	enum ObjectType {

		PROCEDURE("P"), USER_TABLE("U"), DEFAULT("D"), FUNCTION("FN"), PRIMARY_KEY(
				"PK"), ;
		final String shorten;

		ObjectType(String shorten) {
			this.shorten = shorten;
		}
	}

	final boolean existsObject(ObjectType type, String name)
			throws SQLException {
		try {
			this.existsObject.setString(1, this.dbMetadata.schema);
			this.existsObject.setString(2, type.shorten);
			this.existsObject.setString(3, name);
			return this.existsObject.exist();
		} finally {
			this.existsObject.clearParameters();
		}
	}

	@Override
	public final boolean existsTable(String table) throws SQLException {
		return this.existsObject(ObjectType.USER_TABLE, table);
	}

	@Override
	public final void createTable(DBTableDefineImpl define) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("create table ").appendId(define.namedb()).append(" (");
		sql.nNewline().pi();
		for (TableFieldDefineImpl field : define.owner.fields) {
			if (field.dbTable == define || field.isRECID()) {
				this.columnDefinition(sql, field);
				sql.nComma().nNewline();
			}
		}
		sql.appendConstraint().appendId(define.getPkeyName());
		sql.append(" primary key nonclustered ");
		sql.lp().appendId(define.owner.f_recid.namedb());
		sql.rp().nNewline();
		sql.ri().rp().uNewline();
		this.execute(sql);
	}

	final void columnDefinition(SqlBuilder sql, TableFieldDefineImpl field) {
		quote(sql, field.namedb());
		sql.append(' ');
		this.dbMetadata.format(sql, field.getType());
		if (field.isKeepValid()) {
			sql.append(" not null");
		}
		if (field.getDefault() != null) {
			sql.append(" default ");
			sql.append(defaultDefinition(field, defaultDeclare));
		}
	}

	private static final DefaultFormat defaultDeclare = new DefaultFormat() {

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "\'" + DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		protected final String formatSql(byte[] value) {
			return format(value);
		}
	};

	private final DefaultFormat defaultCompare = new DefaultFormat() {

		@Override
		public String inBoolean(ConstExpr c) throws Throwable {
			if (SqlserverStructCtl.this.dbMetadata.beforeYukon()) {
				return "(" + super.inBoolean(c) + ")";
			}
			return "((" + super.inBoolean(c) + "))";
		}

		@Override
		public String inByte(ConstExpr c) throws Throwable {
			if (SqlserverStructCtl.this.dbMetadata.beforeYukon()) {
				return "(" + super.inByte(c) + ")";
			}
			return "((" + super.inByte(c) + "))";
		}

		@Override
		public String inShort(ConstExpr c) throws Throwable {
			if (SqlserverStructCtl.this.dbMetadata.beforeYukon()) {
				return "(" + super.inShort(c) + ")";
			}
			return "((" + super.inShort(c) + "))";
		}

		@Override
		public String inInt(ConstExpr c) throws Throwable {
			if (SqlserverStructCtl.this.dbMetadata.beforeYukon()) {
				return "(" + super.inInt(c) + ")";
			}
			return "((" + super.inInt(c) + "))";
		}

		@Override
		public String inLong(ConstExpr c) throws Throwable {
			if (SqlserverStructCtl.this.dbMetadata.beforeYukon()) {
				return "(" + super.inLong(c) + ")";
			}
			return "((" + super.inLong(c) + "))";
		}

		@Override
		public String inFloat(ConstExpr c) throws Throwable {
			if (SqlserverStructCtl.this.dbMetadata.beforeYukon()) {
				return "(" + super.inFloat(c) + ")";
			}
			return "((" + super.inFloat(c) + "))";
		}

		@Override
		public String inDouble(ConstExpr c) throws Throwable {
			if (SqlserverStructCtl.this.dbMetadata.beforeYukon()) {
				return "(" + super.inDouble(c) + ")";
			}
			return "((" + super.inDouble(c) + "))";
		}

		@Override
		public String inString(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return "(" + super.inString(c, type) + ")";
		}

		@Override
		public String inBytes(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return "(" + this.formatSql(c.getBytes()) + ")";
		}

		@Override
		public String inGUID(ConstExpr c) throws Throwable {
			return "(" + super.inGUID(c) + ")";
		}

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "(\'" + DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME_MS) + "\')";
		}

		@Override
		protected final String formatSql(byte[] value) {
			return format(value);
		}

	};

	private static final String format(byte[] value) {
		return "0x" + Convert.bytesToHex(value, false, false);
	}

	@Override
	public final void renameColumnAndSetNullable(SqlserverColumn column,
			String rename) throws SQLException {
		if (column.defaultDefinition != null) {
			this.dropDefaultConstraint(column.table, column.defaultConstraint);
			column.defaultDefinition = null;
			column.defaultConstraint = null;
		}
		this.execute("exec sys.sp_rename '" + column.table.name + '.' + column.name + "', '" + rename + "', 'column'");
		if (column.notNull) {
			SqlBuilder sql = new SqlBuilder(this.dbMetadata);
			sql.append("alter table ").appendId(column.table.name);
			sql.append(" alter column ").appendId(rename).append(' ');
			column.typeDefinition(sql);
			sql.append(" null");
			this.execute(sql);
		}
	}

	@Override
	public final boolean columnContainNull(SqlserverColumn column)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select top 1 1 from ");
		quote(sql, column.table.name);
		sql.append(" where ");
		quote(sql, column.name);
		sql.append(" is null");
		return this.exist(sql);
	}

	@Override
	public final boolean defaultChanged(TableFieldDefineImpl field,
			SqlserverColumn column) {
		final ConstExpr c = field.getDefault();
		final boolean leftNull = c == null;
		final boolean rightNull = column.defaultDefinition == null;
		if (leftNull != rightNull || (!leftNull && !rightNull && !column.defaultDefinition.equals(defaultDefinition(field, this.defaultCompare)))) {
			return true;
		}
		return false;
	}

	@Override
	public final TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
			SqlserverColumn column) {
		return field.getType().detect(compatible, column);
	}

	private static final TypeDetector<TypeCompatiblity, SqlserverColumn> compatible = new TypeDetectorBase<TypeCompatiblity, SqlserverColumn>() {

		@Override
		public TypeCompatiblity inBoolean(SqlserverColumn column)
				throws Throwable {
			if (column.type == SqlserverDataType.BIT) {
				return Exactly;
			} else if (column.type == SqlserverDataType.TINYINT || column.type == SqlserverDataType.SMALLINT || column.type == SqlserverDataType.INT || column.type == SqlserverDataType.BIGINT) {
				return Overflow;
			} else if ((column.type == SqlserverDataType.NUMERIC || column.type == SqlserverDataType.DECIMAL) && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(SqlserverColumn column)
				throws Throwable {
			if (column.type == SqlserverDataType.SMALLINT) {
				return Exactly;
			} else if (column.type == SqlserverDataType.INT || column.type == SqlserverDataType.BIGINT) {
				return Overflow;
			}
			if ((column.type == SqlserverDataType.NUMERIC || column.type == SqlserverDataType.DECIMAL) && column.precision >= 5 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(SqlserverColumn column) throws Throwable {
			if (column.type == SqlserverDataType.INT) {
				return Exactly;
			} else if (column.type == SqlserverDataType.BIGINT) {
				return Overflow;
			} else if ((column.type == SqlserverDataType.NUMERIC || column.type == SqlserverDataType.DECIMAL) && column.precision >= 10 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(SqlserverColumn column) throws Throwable {
			if (column.type == SqlserverDataType.BIGINT) {
				return Exactly;
			} else if ((column.type == SqlserverDataType.NUMERIC || column.type == SqlserverDataType.DECIMAL) && column.precision - column.scale >= 19 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(SqlserverColumn column) throws Throwable {
			if (column.type == SqlserverDataType.DATETIME) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(SqlserverColumn column)
				throws Throwable {
			if (column.type == SqlserverDataType.REAL) {
				return Exactly;
			} else if (column.type == SqlserverDataType.FLOAT) {
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(SqlserverColumn column)
				throws Throwable {
			if (column.type == SqlserverDataType.FLOAT) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(SqlserverColumn column,
				int precision, int scale) throws Throwable {
			if (column.type == SqlserverDataType.NUMERIC || column.type == SqlserverDataType.DECIMAL) {
				if (column.precision == precision && column.scale == scale) {
					return Exactly;
				} else if (((column.precision - column.scale) >= (precision - scale)) && column.scale >= scale) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inChar(SqlserverColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SqlserverDataType.CHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(SqlserverColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SqlserverDataType.VARCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(SqlserverColumn column) throws Throwable {
			if (column.type == SqlserverDataType.TEXT || (column.type == SqlserverDataType.VARCHAR && column.length == -1)) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(SqlserverColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SqlserverDataType.NCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(SqlserverColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SqlserverDataType.NVARCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(SqlserverColumn column)
				throws Throwable {
			if (column.type == SqlserverDataType.NTEXT || (column.type == SqlserverDataType.NVARCHAR && column.length == -1)) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(SqlserverColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SqlserverDataType.BINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(SqlserverColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SqlserverDataType.VARBINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(SqlserverColumn column) throws Throwable {
			if (column.type == SqlserverDataType.IMAGE || (column.type == SqlserverDataType.VARBINARY && column.length == -1)) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(SqlserverColumn column) throws Throwable {
			if (column.type == SqlserverDataType.BINARY && column.length == 16) {
				return Exactly;
			} else if (column.type == SqlserverDataType.VARBINARY && column.length >= 16) {
				return NotSuggest;
			}
			return Unable;
		}

	};

	final void modifyDefaultConstraint(
			SqlserverDbSync.ColumnCompareCache compareCache)
			throws SQLException {
		for (SqlserverDbSync.ModifyFieldState state : compareCache.modifyQueue.values()) {
			if (state.get(DbSyncBase.MOD_DEFAULT)) {
				if (state.column.defaultDefinition != null) {
					this.dropDefaultConstraint(state.column.table, state.column.defaultConstraint);
				}
				if (state.field.getDefault() != null) {
					this.addDefaultConstraint(state.field, state.column.table);
				}
			}
		}
	}

	final void dropDefaultConstraint(SqlserverTable dbtable, String constraint)
			throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("alter table ").appendId(dbtable.name);
		sql.append(" drop").nSpace();
		sql.append("constraint").nSpace();
		sql.appendId(constraint);
		this.execute(sql);
	}

	final void addDefaultConstraint(TableFieldDefineImpl field,
			SqlserverTable dbtable) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("alter table ").appendId(dbtable.name);
		sql.append(" add").nSpace();
		sql.append("default").nSpace();
		sql.append(defaultDefinition(field, defaultDeclare)).nSpace();
		sql.append("for").nSpace().appendId(field.namedb());
		this.execute(sql);
	}

	final void dropDefaultConstraints(SqlserverTable dbtable,
			ArrayList<SqlserverColumn> columns) throws SQLException {
		if (columns != null && columns.size() > 0) {
			SqlBuilder sql = null;
			for (int i = 0, c = columns.size(); i < c; i++) {
				SqlserverColumn column = columns.get(i);
				if (column.defaultConstraint != null) {
					if (sql == null) {
						sql = new SqlBuilder(this.dbMetadata);
						sql.append("alter table ").appendId(dbtable.name);
						sql.nNewline().pi();
						sql.append("drop").nSpace();
					}
					sql.append("constraint").nSpace();
					sql.appendId(columns.get(i).defaultConstraint);
					sql.nComma();
				}
			}
			if (sql != null) {
				sql.uComma();
				this.execute(sql);
			}
		}
	}

	final void addColumns(SqlserverTable dbtable,
			SqlserverDbSync.ColumnCompareCache compareCache)
			throws SQLException {
		if (compareCache.addQueue.size() > 0) {
			SqlBuilder sql = new SqlBuilder(this.dbMetadata);
			sql.append("alter table ").appendId(dbtable.name);
			sql.append(" add ");
			for (SqlserverDbSync.AddFieldState state : compareCache.addQueue) {
				TableFieldDefineImpl field = state.field;
				sql.appendId(field.namedb()).nSpace();
				sql.appendType(field.getType()).nSpace();
				if (!state.forceNullable && field.isKeepValid()) {
					sql.append("not null");
					sql.nSpace();
				}
				if (field.getDefault() != null) {
					sql.appendDefault();
					sql.append(defaultDefinition(field, defaultDeclare));
					sql.nSpace();
					if (field.isKeepValid()) {
						sql.append("with values");
					}
					sql.nSpace();
				}
				sql.nComma().nNewline();
			}
			sql.uComma().uNewline();
			this.execute(sql);
		}
	}

	final void alterColumns(SqlserverTable dbtable,
			SqlserverDbSync.ColumnCompareCache compareCache)
			throws SQLException {
		if (compareCache.modifyQueue.size() == 0) {
			return;
		}
		for (SqlserverDbSync.ModifyFieldState state : compareCache.modifyQueue.values()) {
			TableFieldDefineImpl field = state.field;
			final boolean type = state.get(DbSyncBase.MOD_TYPE);
			final boolean nullable = state.get(DbSyncBase.MOD_NULLABLE);
			if (type || nullable) {
				SqlBuilder sql = new SqlBuilder(this.dbMetadata);
				sql.append("alter table ").appendId(dbtable.name);
				sql.append(" alter column ");
				sql.appendId(field.namedb());
				sql.nSpace();
				sql.appendType(field.getType());
				sql.append(field.isKeepValid() ? " not null" : " null");
				this.execute(sql);
			}
		}
	}

	final void dropColumns(SqlserverTable dbtable,
			SqlserverDbSync.ColumnCompareCache compareCache)
			throws SQLException {
		for (int i = 0, c = compareCache.dropQueue.size(); i < c; i++) {
			SqlBuilder sql = new SqlBuilder(this.dbMetadata);
			sql.appendAlter().appendTable().appendId(dbtable.name);
			sql.nNewline().pi();
			sql.appendDrop().appendColumn().appendId(compareCache.dropQueue.get(i).name);
			this.execute(sql);
		}
	}

	@Override
	public final void dropTableSilently(String tableName) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("if object_id('");
		quote(sql, tableName);
		sql.append("', 'U') is not null drop table ");
		quote(sql, tableName);
		this.execute(sql);
	}

	@Override
	public final void dropIndex(SqlserverIndex index) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("drop index ");
		sql.appendId(index.table.name);
		sql.append('.');
		sql.appendId(index.name);
		this.execute(sql);
	}

	@Override
	public final boolean indexColumnContainNull(IndexDefineImpl index)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select top 1 1 from ");
		quote(sql, index.dbTable.namedb());
		sql.append(" t where ");
		for (int i = 0, c = index.items.size(); i < c; i++) {
			IndexItemImpl item = index.items.get(i);
			if (i > 0) {
				sql.append(" or ");
			}
			sql.append("t.");
			quote(sql, item.getField().namedb());
			sql.append(" is null");
		}
		return this.exist(sql);
	}

	@Override
	public final boolean indexValueDuplicated(IndexDefineImpl index)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select top 1 1 from ");
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
		sql.append(" having count(*) > 1");
		return this.exist(sql);
	}

	final boolean existsFunction(String function) throws SQLException {
		return this.existsObject(ObjectType.FUNCTION, function);
	}

	final boolean existsProcedure(StoredProcedureDefineImpl procedure)
			throws SQLException {
		return this.existsObject(ObjectType.PROCEDURE, procedure.name);
	}

	final void dropFunction(String function) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop function ");
		quote(sql, function);
		this.execute(sql);
	}

	private static final String EXIST_SCHEMA = "select 1 from sys.schemas where name = ?";

	final void prepareSchema() throws SQLException {
		PreparedStatementWrap ps = this.conn.prepareStatement(EXIST_SCHEMA, SqlSource.CORE_DML);
		try {
			ps.setString(1, "dna");
			ResultSet rs = ps.executeQuery();
			try {
				if (rs.next()) {
					return;
				}
				this.execute("create schema dna");
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

	@Override
    public TableType loadTableType(String tableName) throws SQLException{
	    return TableType.NORMAL;
    }
}