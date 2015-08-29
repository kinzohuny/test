package com.jiuqi.dna.core.internal.db.support.hana.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.Exactly;
import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.NotSuggest;
import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.Overflow;
import static com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity.Unable;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.def.table.TableType;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.NullExpr;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.internal.db.support.hana.HanaMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbNamespace;
import com.jiuqi.dna.core.internal.db.sync.DbStructCtl;
import com.jiuqi.dna.core.internal.db.sync.DefaultFormat;
import com.jiuqi.dna.core.internal.db.sync.SqlBuilder;
import com.jiuqi.dna.core.internal.db.sync.TypeCompatiblity;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DateParser;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public class HanaStructCtl
		extends
		DbStructCtl<HanaMetadata, HanaTable, HanaColumn, HanaDataType, HanaIndex> {

	static final void quote(Appendable s, String id) {
		DbProduct.Hana.quote(s, id);
	}

	private static final String EXIST_TABLE = "select 1 from sys.tables where schema_name = ? and table_name = ?";
	private final PreparedStatementWrap existTable;

	private static final String SELECT_INDEX_NAMES = "select index_name from sys.indexes where schema_name = ? union select table_name from sys.tables where schema_name = ?";
	private final PreparedStatementWrap snapIndexes;

	final DbNamespace snapIndexes() throws SQLException {
		final DbNamespace namespace = new DbNamespace(true);
		try {
			this.snapIndexes.setString(1, this.dbMetadata.schema);
			this.snapIndexes.setString(2, this.dbMetadata.schema);
			final ResultSet rs = this.snapIndexes.executeQuery();
			try {
				while (rs.next()) {
					final String index = rs.getString(1);
					namespace.add(index);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.snapIndexes.clearParameters();
		}
		return namespace;
	}

	protected HanaStructCtl(PooledConnection conn, HanaMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
		this.loadColumns = this.prepareStatement(SELECT_TABLE_COLUMNS);
		this.loadIndexes = this.prepareStatement(SELECT_INDEX_COLUMNS);
		this.existTable = this.prepareStatement(EXIST_TABLE);
		this.snapIndexes = this.prepareStatement(SELECT_INDEX_NAMES);
		// HANA Auto-generated constructor stub
	}

	@Override
	protected HanaTable newDbTableOnly(String name) {
		return new HanaTable(name);
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
	 * <li>data_type_name 数据类型
	 * <li>length 长度
	 * <li>data_scale 小数位
	 * <li>is_nullable 可否为空
	 * <li>default_value 默认值
	 * </ol>
	 */
	static final String SELECT_TABLE_COLUMNS = "select column_name, data_type_name, length, scale, is_nullable, default_value from sys.table_columns where schema_name = ? and table_name = ? order by position";
	private final PreparedStatementWrap loadColumns;

	@Override
	protected void loadColumns(HanaTable dbtable) throws SQLException {
		try {
			this.loadColumns.setString(1, this.dbMetadata.schema);
			this.loadColumns.setString(2, dbtable.name);
			ResultSet rs = this.loadColumns.executeQuery();
			try {
				while (rs.next()) {
					final String columnName = rs.getString(1);
					final HanaColumn column = dbtable.addColumn(columnName);
					column.type = HanaDataType.valueOf(rs.getString(2));
					column.type.tired(column, rs);
					column.notNull = "FALSE".equals(rs.getString(5));
					column.defaultDefinition = rs.getString(6);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadColumns.clearParameters();
		}
	}

	private static final String SELECT_INDEX_COLUMNS = "select i.index_name, i.constraint, ic.column_name, ic.ascending_order  from sys.indexes i join sys.index_columns ic on i.index_oid = ic.index_oid where i.schema_name = ? and i.table_name = ? and i.constraint in ('PRIMARY KEY', 'UNIQUE') order by 1, ic.position";
	private final PreparedStatementWrap loadIndexes;

	@Override
	protected void loadIndexes(HanaTable dbtable) throws SQLException {
		try {
			this.loadIndexes.setString(1, this.dbMetadata.schema);
			this.loadIndexes.setString(2, dbtable.name);
			ResultSet rs = this.loadIndexes.executeQuery();
			try {
				while (rs.next()) {
					final String indexName = rs.getString(1);
					HanaIndex index = dbtable.findIndex(indexName);
					if (index == null) {
						final String constraint = rs.getString(2);
						final boolean primary = "PRIMARY KEY".equals(constraint);
						final boolean unique = "UNIQUE".equals(constraint);
						index = dbtable.addIndex(indexName, primary || unique);
						if (primary) {
							dbtable.primaryKey = index;
						}
					}
					final HanaColumn column = dbtable.getColumn(rs.getString(3));
					final boolean desc = "FALSE".equals(rs.getString(4));
					index.add(column, desc);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadIndexes.clearParameters();
		}
	}

	@Override
	public boolean tableContainRows(String table) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, table);
		sql.append(" limit 1");
		return this.exist(sql);
	}

	@Override
	public boolean existsTable(String tableName) throws SQLException {
		try {
			this.existTable.setString(1, this.dbMetadata.schema);
			this.existTable.setString(2, tableName);
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

	private static final void columnDefinition(SqlBuilder sql,
			TableFieldDefineImpl field, boolean forceNullable) {
		sql.appendId(field.namedb()).append(' ');
		sql.appendType(field.getType());
		if (field.getDefault() != null) {
			sql.append(" default ");
			sql.append(defaultDefinition(field, defaultDeclare));
		}
		if (!forceNullable && field.isKeepValid()) {
			sql.append(" not null");
		}
		if (field.isRECID()) {
			sql.append(" primary key");
		}
	}

	private static final DefaultFormat defaultCompare = new DefaultFormat() {

		@Override
		public String inBoolean(ConstExpr c) throws Throwable {
			return c.getBoolean() ? "1" : "0";
		}

		@Override
		public String inByte(ConstExpr c) throws Throwable {
			return Byte.toString(c.getByte());
		}

		@Override
		public String inShort(ConstExpr c) throws Throwable {
			return Short.toString(c.getShort());
		}

		@Override
		public String inInt(ConstExpr c) throws Throwable {
			return Integer.toString(c.getInt());
		}

		@Override
		public String inLong(ConstExpr c) throws Throwable {
			return Long.toString(c.getLong());
		}

		@Override
		public String inFloat(ConstExpr c) throws Throwable {
			return Double.toString(c.getDouble());
		}

		@Override
		public String inDouble(ConstExpr c) throws Throwable {
			return Double.toString(c.getDouble());
		}

		@Override
		public String inString(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return c.getString();
		}

		@Override
		public String inBytes(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return this.formatSql(c.getBytes());
		}

		@Override
		public String inGUID(ConstExpr c) throws Throwable {
			return this.formatSql(c.getBytes());
		}

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME);
		}

		@Override
		protected final String formatSql(byte[] value) {
			return Convert.bytesToHex(value, false, false);
		}

	};

	private static final DefaultFormat defaultDeclare = new DefaultFormat() {

		@Override
		public String inBoolean(ConstExpr c) throws Throwable {
			return c.getBoolean() ? "1" : "0";
		}

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "timestamp\'" + DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME) + "\'";
		}

		@Override
		protected String formatSql(byte[] value) {
			return "x'" + Convert.bytesToHex(value, false, false) + "'";
		}
	};

	@Override
	public void createTable(DBTableDefineImpl define) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("create column table ");
		quote(sql, define.namedb());
		sql.lp().nNewline().pi();
		for (TableFieldDefineImpl field : define.owner.fields) {
			if (field.dbTable == define || field.isRECID()) {
				columnDefinition(sql, field, false);
				sql.nComma().nNewline();
			}
		}
		sql.uComma().ri().rp();
		this.execute(sql);
	}

	@Override
	public boolean defaultChanged(TableFieldDefineImpl field, HanaColumn column) {
		final ConstExpr c = field.getDefault();
		final boolean leftNull = c == null || c == NullExpr.NULL;
		final boolean rightNull = column.defaultDefinition == null;
		if (leftNull != rightNull || (!leftNull && !rightNull && !column.defaultDefinition.equals(defaultDefinition(field, defaultDeclare)))) {
			return true;
		}
		return false;
	}

	@Override
	public void dropTableSilently(String tableName) throws SQLException {
		if (this.existsTable(tableName)) {
			this.dropTable(tableName);
		}
	}

	@Override
	public void dropIndex(HanaIndex index) throws SQLException {
		// HANA Auto-generated method stub

	}

	@Override
	public boolean indexColumnContainNull(IndexDefineImpl index)
			throws SQLException {
		// HANA Auto-generated method stub
		return false;
	}

	@Override
	public boolean indexValueDuplicated(IndexDefineImpl index)
			throws SQLException {
		// HANA Auto-generated method stub
		return false;
	}

	@Override
	public boolean columnContainNull(HanaColumn column) throws SQLException {
		// HANA Auto-generated method stub
		return false;
	}

	@Override
	public void renameColumnAndSetNullable(HanaColumn column, String rename)
			throws SQLException {
		// HANA Auto-generated method stub

	}

	@Override
	public TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
			HanaColumn column) {
		return field.getType().detect(compatible, column);
	}

	private static final TypeDetector<TypeCompatiblity, HanaColumn> compatible = new TypeDetectorBase<TypeCompatiblity, HanaColumn>() {

		@Override
		public TypeCompatiblity inBoolean(HanaColumn c) throws Throwable {
			if (c.type == HanaDataType.TINYINT) {
				return TypeCompatiblity.Exactly;
			} else if (c.type == HanaDataType.SMALLINT || c.type == HanaDataType.INTEGER || c.type == HanaDataType.BIGINT) {
				return Overflow;
			} else if (c.type == HanaDataType.DECIMAL && c.precision >= 1 && c.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(HanaColumn c) throws Throwable {
			if (c.type == HanaDataType.SMALLINT) {
				return Exactly;
			} else if (c.type == HanaDataType.INTEGER || c.type == HanaDataType.BIGINT) {
				return Overflow;
			} else if (c.type == HanaDataType.DECIMAL && c.precision >= 5 && c.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(HanaColumn c) throws Throwable {
			if (c.type == HanaDataType.INTEGER) {
				return Exactly;
			} else if (c.type == HanaDataType.BIGINT) {
				return Overflow;
			} else if (c.type == HanaDataType.DECIMAL && c.precision >= 10 && c.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(HanaColumn c) throws Throwable {
			if (c.type == HanaDataType.BIGINT) {
				return Exactly;
			} else if (c.type == HanaDataType.DECIMAL && c.precision >= 19 && c.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(HanaColumn c) throws Throwable {
			if (c.type == HanaDataType.DOUBLE) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(HanaColumn c) throws Throwable {
			if (c.type == HanaDataType.DOUBLE) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(HanaColumn c, int precision, int scale)
				throws Throwable {
			if (c.type == HanaDataType.DECIMAL) {
				if (c.precision == precision && c.scale == scale) {
					return Exactly;
				} else if (((c.precision - c.scale) >= (precision - scale)) && c.scale >= scale) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inChar(HanaColumn c, SequenceDataType type)
				throws Throwable {
			if (c.type == HanaDataType.NVARCHAR) {
				if (c.length == type.getMaxLength()) {
					return Exactly;
				} else if (c.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(HanaColumn c, SequenceDataType type)
				throws Throwable {
			if (c.type == HanaDataType.NVARCHAR) {
				if (c.length == type.getMaxLength()) {
					return Exactly;
				} else if (c.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(HanaColumn column) throws Throwable {
			if (column.type == HanaDataType.NCLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(HanaColumn c, SequenceDataType type)
				throws Throwable {
			if (c.type == HanaDataType.NVARCHAR) {
				if (c.length == type.getMaxLength()) {
					return Exactly;
				} else if (c.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(HanaColumn c, SequenceDataType type)
				throws Throwable {
			if (c.type == HanaDataType.NVARCHAR) {
				if (c.length == type.getMaxLength()) {
					return Exactly;
				} else if (c.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(HanaColumn column) throws Throwable {
			if (column.type == HanaDataType.NCLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(HanaColumn c, SequenceDataType type)
				throws Throwable {
			final int length = type.getMaxLength();
			if (c.type == HanaDataType.VARBINARY) {
				if (c.length == length) {
					return Exactly;
				} else if (c.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(HanaColumn c, SequenceDataType type)
				throws Throwable {
			final int length = type.getMaxLength();
			if (c.type == HanaDataType.VARBINARY) {
				if (c.length == length) {
					return Exactly;
				} else if (c.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(HanaColumn column) throws Throwable {
			if (column.type == HanaDataType.BLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(HanaColumn c) throws Throwable {
			if (c.type == HanaDataType.VARBINARY && c.length == 16) {
				return Exactly;
			} else if (c.type == HanaDataType.VARBINARY && c.length >= 16) {
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(HanaColumn column) throws Throwable {
			if (column.type == HanaDataType.TIMESTAMP) {
				return Exactly;
			}
			return Unable;
		}
	};

	@Override
    public TableType loadTableType(String tableName) throws SQLException{
	    return TableType.NORMAL;
    }

}
