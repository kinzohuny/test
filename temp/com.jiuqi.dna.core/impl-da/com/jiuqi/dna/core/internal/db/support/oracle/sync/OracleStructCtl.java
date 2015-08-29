package com.jiuqi.dna.core.internal.db.support.oracle.sync;

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
import com.jiuqi.dna.core.internal.db.support.oracle.OracleMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbNamespace;
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

final class OracleStructCtl
		extends
		DbStructCtl<OracleMetadata, OracleTable, OracleColumn, OracleDataType, OracleIndex> {

	OracleStructCtl(PooledConnection conn, OracleMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
		this.loadColumns = this.prepareStatement(SELECT_TABLE_COLUMNS);
		this.loadIndexes = this.prepareStatement(SELECT_INDEX_COLUMNS);
		this.loadPKeyConst = this.prepareStatement(SELECT_PKEY_CONST);
		this.getObjStatus = this.prepareStatement(SELECT_OBJECT_STATUS);
		this.dropTableSilently = this.prepareStatement(DROP_TABLE_SILENTLY);
		this.snapIndexes = this.prepareStatement(SELECT_INDEX_NAMES);
		this.ensureBitMap = this.prepareStatement(ENABLE_BIT_MAP);
		this.loadTableType = this.prepareStatement(TABLE_TYPE);
	}

	public static final void quote(Appendable str, String name) {
		OracleMetadata.quote(str, name);
	}

	@Override
	protected final OracleTable newDbTableOnly(String name) {
		return new OracleTable(name);
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
	static final String SELECT_TABLE_COLUMNS = "select column_name, data_type, data_length, data_precision, data_scale, nullable, data_default from user_tab_cols where table_name = ? and virtual_column = 'NO' order by column_id";
	private final PreparedStatementWrap loadColumns;

	@Override
	protected final void loadColumns(OracleTable dbtable) throws SQLException {
		try {
			this.loadColumns.setString(1, dbtable.name);
			ResultSet rs = this.loadColumns.executeQuery();
			try {
				while (rs.next()) {
					final String columnName = rs.getString(1);
					final OracleColumn column = dbtable.addColumn(columnName);
					this.loadColumn(rs, column);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadColumns.clearParameters();
		}
	}

	private final void loadColumn(ResultSet rs, OracleColumn column)
			throws SQLException {
		column.type = OracleDataType.typeOf(rs.getString(2));
		if (column.type == OracleDataType.NCHAR || column.type == OracleDataType.NVARCHAR2) {
			column.length = rs.getInt(3) / 2;
		} else {
			column.length = rs.getInt(3);
		}
		column.precision = rs.getInt(4);
		column.scale = rs.getInt(5);
		column.notNull = rs.getString(6).equals("N");
		String defaultVal = rs.getString(7);
		if (defaultVal != null) {
			column.defaultDefinition = defaultVal.trim();
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
	 * <p>
	 * JDBC接口中的DatabaseMetaData的getColumns()方法,对于降序的索引列,不能正确返回列名.
	 * 所以直接查询oracle字典. 对于降序索引,从refer列中读取列名.
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
	 * <li>index_name 索引名
	 * <li>is_unique 是否唯一
	 * <li>column_name 索引字段名称(索引项升序时才有效)
	 * <li>is_desc 是否降序索引
	 * <li>desc_column_refer 降序索引时的字段名称
	 * </ol>
	 */
	private static final String SELECT_INDEX_COLUMNS = "select i.index_name, decode(i.uniqueness,'UNIQUE',1,0) as is_unique, ic.column_name, decode(ic.descend,'DESC',1, 0 ) as is_desc, tc.data_default as desc_column_refer, i.index_type from user_indexes i inner join user_ind_columns ic on i.table_name = ic.table_name and i.index_name = ic.index_name inner join user_tab_cols tc on i.table_name = tc.table_name and ic.column_name = tc.column_name where i.table_owner = ?  and i.table_name = ? order by ic.column_position";
	private final PreparedStatementWrap loadIndexes;

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
	private static final String SELECT_PKEY_CONST = "select constraint_name from user_constraints where table_name = ? and constraint_type = 'P'";
	private final PreparedStatementWrap loadPKeyConst;

	@Override
	protected final void loadIndexes(OracleTable dbtable) throws SQLException {
		try {
			this.loadIndexes.setString(1, this.dbMetadata.user);
			this.loadIndexes.setString(2, dbtable.name);
			ResultSet rs = this.loadIndexes.executeQuery();
			try {
				while (rs.next()) {
					final String indexName = rs.getString(1);
					OracleIndex index = dbtable.findIndex(indexName);
					final boolean desc = rs.getBoolean(4);
					String columnName;
					if (desc) {
						String refer = rs.getString(5);
						columnName = refer.substring(1, refer.length() - 1);
					} else {
						columnName = rs.getString(3);
					}
					if (index == null) {
						index = dbtable.addIndex(indexName, rs.getBoolean(2));
						if (rs.getString(6).equals("BITMAP")) {
							index.bitmap = true;
						}
					}
					final OracleColumn column = dbtable.getColumn(columnName);
					index.add(column, desc);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadIndexes.clearParameters();
		}
		dbtable.primaryKey = null;
		try {
			this.loadPKeyConst.setString(1, dbtable.name);
			ResultSet rs = this.loadPKeyConst.executeQuery();
			try {
				if (rs.next()) {
					dbtable.primaryKey = dbtable.getIndex(rs.getString(1));
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadPKeyConst.clearParameters();
		}
	}

	@Override
	public final boolean tableContainRows(String table) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, table);
		sql.append(" where rownum <= 1");
		return this.exist(sql);
	}

	private static final String SELECT_OBJECT_STATUS = "select status from user_objects where object_type = ? and object_name = ?";
	private final PreparedStatementWrap getObjStatus;

	final OracleObjectStatus getObjectStatus(OracleObjectType type, String name) {
		try {
			try {
				this.getObjStatus.setString(1, type.name());
				this.getObjStatus.setString(2, name);
				ResultSet rs = this.getObjStatus.executeQuery();
				try {
					if (rs.next()) {
						final String status = rs.getString(1);
						if (status.equals(OracleObjectStatus.VALID.value)) {
							return OracleObjectStatus.VALID;
						} else if (status.equals(OracleObjectStatus.INVALID.value)) {
							return OracleObjectStatus.INVALID;
						} else {
							return OracleObjectStatus.NA;
						}
					}
					return null;
				} finally {
					rs.close();
				}
			} finally {
				this.getObjStatus.clearParameters();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	final OracleObjectStatus getProcedureStatus(
			StoredProcedureDefineImpl procedure) {
		return this.getObjectStatus(OracleObjectType.PROCEDURE, procedure.name);
	}

	final OracleObjectStatus getFunctionStatus(String function) {
		return this.getObjectStatus(OracleObjectType.FUNCTION, function);
	}

	@Override
	public final boolean existsTable(String table) throws SQLException {
		return this.getObjectStatus(OracleObjectType.TABLE, table) != null;
	}

	@Override
	public final void createTable(DBTableDefineImpl define) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		TableType tableType = define.getTableType();
		if(tableType.equals(TableType.GLOBAL_TEMPORARY)) {
			//创建全局临时表的语句
			sql.appendCreate().appendGlobal().appendTemporary().appendTable().appendId(define.namedb()).lp();
		} else {
			//创建普通表
			sql.appendCreate().appendTable().appendId(define.namedb()).lp();
		}
		sql.nNewline().pi();
		for (TableFieldDefineImpl field : define.owner.fields) {
			if (field.dbTable == define || field.isRECID()) {
				columnDefinition(sql, field, false);
				sql.nComma().nNewline();
			}
		}
		outlineRecidConstraint(sql, define);
		sql.nNewline().ri().rp();
		
		if(tableType.equals(TableType.GLOBAL_TEMPORARY)) {
			//标注创建基于事物型的全局临时表
			sql.nNewline().appendOn().appendCommit().appendDelete().appendRows();
		} else {//临时表不能进行表分区
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
		}
		this.execute(sql);
	}

	private static final DefaultFormat defaultDeclare = new DefaultFormat() {

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "timestamp\'" + DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		protected String formatSql(byte[] value) {
			return "hextoraw('" + Convert.bytesToHex(value, false, false) + "\')";
		}
	};

	final void dbAddnModifyColumn(OracleTable dbtable,
			OracleDbSync.ColumnCompareCache compareCache) throws SQLException {
		if (compareCache.addQueue.size() == 0 && compareCache.modifyQueue.size() == 0) {
			return;
		}
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("alter table ").appendId(dbtable.name);
		if (compareCache.addQueue.size() > 0) {
			sql.append(" add (");
			for (OracleDbSync.AddFieldState state : compareCache.addQueue) {
				columnDefinition(sql, state.field, state.forceNullable);
				sql.nComma();
			}
			sql.uComma().rp();
		}
		if (compareCache.modifyQueue.size() > 0) {
			sql.uSpace().append(" modify (");
			for (OracleDbSync.ModifyFieldState state : compareCache.modifyQueue.values()) {
				columnModification(sql, state);
				sql.nComma();
			}
			sql.uComma().rp();
		}
		this.execute(sql);
	}

	private static final void columnModification(SqlBuilder sql,
			OracleDbSync.ModifyFieldState state) {
		sql.appendId(state.column.name);
		if (state.get(DbSyncBase.MOD_TYPE)) {
			sql.append(' ').appendType(state.field.getType());
		}
		if (state.get(DbSyncBase.MOD_DEFAULT)) {
			sql.append(" default ");
			final ConstExpr df = state.field.getDefault();
			if (df != null) {
				sql.append(defaultDefinition(state.field, defaultDeclare));
			} else {
				sql.append("null");
			}
		}
		if (state.get(DbSyncBase.MOD_NULLABLE)) {
			if (state.field.isKeepValid()) {
				sql.append(" not null");
			} else {
				sql.append(" null");
			}
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
	}

	@Override
	public final boolean defaultChanged(TableFieldDefineImpl field,
			OracleColumn column) {
		final ConstExpr c = field.getDefault();
		final boolean leftNull = c == null || c == NullExpr.NULL;
		final boolean rightNull = column.defaultDefinition == null;
		if (leftNull != rightNull || (!leftNull && !rightNull && !column.defaultDefinition.equals(defaultDefinition(field, defaultDeclare)))) {
			return true;
		}
		return false;
	}

	@Override
	public final TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
			OracleColumn column) {
		return field.getType().detect(compatible, column);
	}

	// for oracle 10
	private static final TypeDetector<TypeCompatiblity, OracleColumn> compatible = new TypeDetectorBase<TypeCompatiblity, OracleColumn>() {

		@Override
		public TypeCompatiblity inBoolean(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NUMBER && column.scale == 0) {
				if (column.precision == 1) {
					return Exactly;
				}
				return Overflow;
			} else if (column.type == OracleDataType.FLOAT) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NUMBER && column.scale == 0) {
				if (column.precision == 5) {
					return Exactly;
				} else if (column.precision > 5 || column.precision == 0) {
					return Overflow;
				}
			} else if (column.type == OracleDataType.FLOAT) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NUMBER && column.scale == 0) {
				if (column.precision == 10) {
					return Exactly;
				} else if (column.precision > 10 || column.precision == 0) {
					return Overflow;
				}
			} else if (column.type == OracleDataType.FLOAT) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NUMBER && column.scale == 0) {
				if (column.precision == 19) {
					return Exactly;
				} else if (column.precision > 19 || column.precision == 0) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.FLOAT && column.precision >= 63) {
				return Exactly;
			} else if (column.type == OracleDataType.BINARY_FLOAT || column.type == OracleDataType.BINARY_DOUBLE) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.FLOAT && column.precision >= 126) {
				return Exactly;
			} else if (column.type == OracleDataType.BINARY_FLOAT || column.type == OracleDataType.BINARY_DOUBLE) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(OracleColumn column, int precision,
				int scale) throws Throwable {
			if (column.type == OracleDataType.NUMBER) {
				if (column.precision == precision && column.scale == scale) {
					return Exactly;
				} else if (((column.precision - column.scale) >= (precision - scale)) && column.scale >= scale) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inChar(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.CHAR || column.type == OracleDataType.VARCHAR2) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			} else if (column.type == OracleDataType.NCHAR || column.type == OracleDataType.NVARCHAR2) {
				// GBKONLY
				if (column.length * 2 >= length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.CHAR || column.type == OracleDataType.VARCHAR2) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			} else if (column.type == OracleDataType.NCHAR || column.type == OracleDataType.NVARCHAR2) {
				// GBKONLY
				if (column.length * 2 >= length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.CLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.NCHAR || column.type == OracleDataType.NVARCHAR2) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			} else if (column.type == OracleDataType.CHAR || column.type == OracleDataType.VARCHAR2) {
				// GBKONLY
				if (length * 2 <= column.length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.NCHAR || column.type == OracleDataType.NVARCHAR2) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			} else if (column.type == OracleDataType.CHAR || column.type == OracleDataType.VARCHAR2) {
				// GBKONLY
				if (length * 2 <= column.length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.NCLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.RAW) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(OracleColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == OracleDataType.RAW) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.BLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.RAW) {
				if (column.length == 16) {
					return Exactly;
				} else if (column.length > 16) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(OracleColumn column) throws Throwable {
			if (column.type == OracleDataType.TIMESTAMP) {
				return Exactly;
			} else if (column.type == OracleDataType.TIMESTAMP_WITH_TIME_ZONE || column.type == OracleDataType.TIMESTAMP_WITH_LOCAL_TIME_ZONE) {
				return Overflow;
			}
			return Unable;
		}
	};

	@Override
	public final boolean columnContainNull(OracleColumn column)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, column.table.name);
		sql.append(" where ");
		quote(sql, column.name);
		sql.append(" is null and rownum <= 1");
		return this.exist(sql);
	}

	final void dbDropColumns(OracleTable dbtable,
			OracleDbSync.ColumnCompareCache compareCache) throws SQLException {
		if (compareCache.dropQueue.size() > 0) {
			SqlBuilder sql = new SqlBuilder(this.dbMetadata);
			sql.append("alter table ").appendId(dbtable.name);
			sql.nNewline().pi().append("drop (");
			for (int i = 0, c = compareCache.dropQueue.size(); i < c; i++) {
				sql.appendId(compareCache.dropQueue.get(i).name).nComma().nSpace();
			}
			sql.uComma().uSpace().rp().ri();
			this.execute(sql);
		}
	}

	@Override
	public final void renameColumnAndSetNullable(OracleColumn column,
			String rename) throws SQLException {
		final OracleMetadata omd = this.dbMetadata;
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		omd.quoteId(sql, column.table.name);
		sql.append(" rename column ");
		omd.quoteId(sql, column.name);
		sql.append(" to ");
		omd.quoteId(sql, rename);
		this.execute(sql);
		if (column.notNull) {
			StringBuilder set = new StringBuilder();
			set.append("alter table ");
			omd.quoteId(set, column.table.name);
			set.append(" modify (");
			omd.quoteId(set, rename);
			set.append(" null)");
			this.execute(set);
		}
	}

	private static final String DROP_TABLE_SILENTLY = "call dna.silent_drop_table(?)";
	private final PreparedStatementWrap dropTableSilently;

	@Override
	public final void dropTableSilently(String table) throws SQLException {
		try {
			this.dropTableSilently.setString(1, table);
			this.dropTableSilently.execute();
		} finally {
			this.dropTableSilently.clearParameters();
		}
	}

	@Override
	public final void dropIndex(OracleIndex index) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop index ");
		quote(sql, index.name);
		this.execute(sql);
	}

	private static final String SELECT_INDEX_NAMES = "select index_name from user_indexes";
	private final PreparedStatementWrap snapIndexes;

	final DbNamespace snapIndexes() throws SQLException {
		final DbNamespace namespace = new DbNamespace(true);
		try {
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

	@Override
	public final boolean indexColumnContainNull(IndexDefineImpl index)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select /*+ FIRST_ROWS (1) */ 1 from ");
		this.dbMetadata.quoteId(sql, index.dbTable.namedb());
		sql.append(" t group by ");
		for (int i = 0; i < index.items.size(); i++) {
			IndexItemImpl item = index.items.get(i);
			if (i > 0) {
				sql.append(", ");
			}
			sql.append("t.");
			this.dbMetadata.quoteId(sql, item.getField().namedb());
		}
		sql.append(" having count(*) > 1");
		return this.exist(sql);
	}

	@Override
	public final boolean indexValueDuplicated(IndexDefineImpl index)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select /*+ FIRST_ROWS (1) */ 1 from ");
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

	final void dropProcedure(String procedure) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop procedure ");
		quote(sql, procedure);
		this.execute(sql);
	}

	final void dbDropFunction(String functionName) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop function ");
		quote(sql, functionName);
		this.execute(sql);
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

	final void createCollateGBK() throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("create table ");
		quote(sql, CORE_COLLATE_GBK);
		sql.append(" (");
		quote(sql, CORE_COLLATE_GBK_CH);
		sql.append(" nvarchar2(1) primary key, ");
		quote(sql, CORE_COLLATE_GBK_SN);
		sql.append(" raw(2)) organization index");
		this.execute(sql.toString(), SqlSource.CORE_DDL);
	}

	final void createCoreDbTenant() {
		// StringBuilder sql = new StringBuilder();
		// sql.append("create table ");
		// quote(sql, DataSourceLockConstants.TABLE_NAME);
		// sql.append(" (");
		// quote(sql, DataSourceLockConstants.COL_ID);
		// sql.append(" raw(16) primary key, ");
		// quote(sql, DataSourceLockConstants.COL_LAST_TENANT);
		// sql.append(" timestamp, ");
		// quote(sql, DataSourceLockConstants.COL_TENANT_INTERVAL);
		// sql.append(" number(10), ");
		// quote(sql, DataSourceLockConstants.COL_RUNTIME);
		// sql.append(" varchar2(216), ");
		// quote(sql, DataSourceLockConstants.COL_SILENZ);
		// sql.append(" varchar2(7))");
		// this.execute(sql.toString());
	}

	final boolean existsColumn(String table, String column) throws SQLException {
		String sql = "select 1 from user_tab_columns where table_name = ? and column_name = ?";
		PreparedStatementWrap pstmt = this.conn.prepareStatement(sql, SqlSource.CORE_DML);
		try {
			pstmt.setString(1, table);
			pstmt.setString(2, column);
			ResultSet resultSet = pstmt.executeQuery();
			try {
				if (resultSet.next()) {
					return true;
				}
				return false;
			} finally {
				pstmt.close();
			}
		} finally {
			pstmt.close();
		}
	}

	final void tenantAddLckCol() {
		// this.execute("alter table " + DataSourceLockConstants.TABLE_NAME
		// + " add " + DataSourceLockConstants.COL_SILENZ + " varchar2(7)");
	}

	final void ensureCoreDbTenantRow() {
		// StringBuilder sql = new StringBuilder();
		// sql.append("insert into ");
		// quote(sql, DataSourceLockConstants.TABLE_NAME);
		// sql.append("(" + DataSourceLockConstants.COL_ID + ","
		// + DataSourceLockConstants.COL_LAST_TENANT + ","
		// + DataSourceLockConstants.COL_TENANT_INTERVAL + ")");
		// sql.append(" values (hextoraw('00000000000000000000000000000000'), timestamp'2011-05-25 19:30:00', 532)");
		// this.execute(sql);
	}

	final void createBitmap(IndexDefineImpl index) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("create bitmap index ");
		this.dbMetadata.quoteId(sql, index.namedb());
		sql.append(" on ");
		this.dbMetadata.quoteId(sql, index.dbTable.namedb());
		sql.append('(');
		for (int i = 0, c = index.items.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',').append(' ');
			}
			IndexItemImpl item = index.items.get(i);
			this.dbMetadata.quoteId(sql, item.getField().getNameInDB());
			if (item.isDesc()) {
				sql.append(" desc");
			}
		}
		sql.append(')');
		this.execute(sql);
	}
	
	/**
	 * 判断oracle是否启用Bit-mapped索引
	 */
	private static final String ENABLE_BIT_MAP = "select value from v$option where parameter = 'Bit-mapped indexes'";
	private final PreparedStatementWrap ensureBitMap;
	
	final boolean enableBitMappedIndexes() {
		try {
			ResultSet result = this.ensureBitMap.executeQuery();
			if (result.next()) {
				String s = result.getString(1);
				if (s != null && s.trim().length() > 0) {
					return Boolean.valueOf(s);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * oracle中获取表的类型（普通表还是全局临时表）
	 */
	private static final String TABLE_TYPE = "select count(*) from user_tables where table_name = ? and upper(temporary) = 'Y' ";
	private final PreparedStatementWrap loadTableType;
	@Override
    public final TableType loadTableType(String tableName) throws SQLException{
		this.loadTableType.setString(1, tableName);
		try {
			ResultSet result = this.loadTableType.executeQuery();
			if (result.next()) {
				int count = result.getInt(1);
				if (count > 0 ) {
					return TableType.GLOBAL_TEMPORARY;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return TableType.NORMAL;
    }
}