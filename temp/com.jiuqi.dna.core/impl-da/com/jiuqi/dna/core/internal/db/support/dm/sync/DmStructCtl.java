package com.jiuqi.dna.core.internal.db.support.dm.sync;

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
import com.jiuqi.dna.core.impl.IndexItemImpl;
import com.jiuqi.dna.core.impl.NullExpr;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.internal.db.support.dm.DmMetadata;
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

final class DmStructCtl extends
		DbStructCtl<DmMetadata, DmTable, DmColumn, DmDataType, DmIndex> {

	protected DmStructCtl(PooledConnection conn, DmMetadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
		this.loadColumns = this.prepareStatement(SELECT_TABLE_COLUMNS);
		this.loadIndexes = this.prepareStatement(SELECT_INDEX_COLUMNS);
		this.loadPKeyConst = this.prepareStatement(SELECT_PKEY_CONST);
		this.existsTable = this.prepareStatement(EXISTS_TABLE);
		this.snapIndexes = this.prepareStatement(SELECT_INDEX_NAMES);
		this.getObjStatus = this.prepareStatement(SELECT_OBJECT_STATUS);
		this.loadTableType = this.prepareStatement(TABLE_TYPE);
	}

	@Override
	protected DmTable newDbTableOnly(String name) {
		return new DmTable(name);
	}

	private static final String SELECT_TABLE_COLUMNS = "select column_name, data_type, data_length, data_scale, nullable, data_default  from USER_TAB_COLUMNS where table_name = ? order by column_id";
	private final PreparedStatementWrap loadColumns;

	@Override
	protected void loadColumns(DmTable dbtable) throws SQLException {
		try {
			this.loadColumns.setString(1, dbtable.name);
			ResultSet rs = this.loadColumns.executeQuery();
			try {
				while (rs.next()) {
					final String columnName = rs.getString(1);
					final DmColumn column = dbtable.addColumn(columnName);
					this.loadColumn(rs, column);
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadColumns.clearParameters();
		}
	}

	private final void loadColumn(ResultSet rs, DmColumn column)
			throws SQLException {
		column.type = DmDataType.get(rs.getString(2));
		column.length = rs.getInt(3);
		// if (column.type == DmDataType.CHAR || column.type ==
		// DmDataType.VARCHAR || column.type == DmDataType.NCHAR || column.type
		// == DmDataType.NVARCHAR) {
		// column.length = column.length / 2;
		// }
		column.precision = column.length;
		column.scale = rs.getInt(4);
		column.notNull = rs.getString(5).equals("N");
		column.defaultDefinition = rs.getString(6);
	}

	private static final String SELECT_INDEX_COLUMNS = "select i.index_name, i.index_type, decode(i.uniqueness,'UNIQUE',1,0),ic.column_name,decode(ic.descend,'ASC',0,1) from user_indexes i join user_ind_columns ic on i.index_name = ic.index_name where i.table_name = ? order by i.index_name, ic.column_position";
	private final PreparedStatementWrap loadIndexes;

	private static final String SELECT_PKEY_CONST = "select constraint_name, index_name from user_constraints where table_name = ? and constraint_type = 'P'";
	private final PreparedStatementWrap loadPKeyConst;

	@Override
	protected void loadIndexes(DmTable dbtable) throws SQLException {
		try {
			this.loadIndexes.setString(1, dbtable.name);
			ResultSet rs = this.loadIndexes.executeQuery();
			try {
				while (rs.next()) {
					final String indexName = rs.getString(1);
					DmIndex index = dbtable.findIndex(indexName);
					if (index == null) {
						index = dbtable.addIndex(indexName, rs.getBoolean(3));
						// if (rs.getString(2).equals("BITMAP")) {
						// index.bitmap = true;
						// }
					}
					final boolean desc = rs.getBoolean(5);
					final String columnName = rs.getString(4);
					final DmColumn column = dbtable.getColumn(columnName);
					index.add(column, desc);
				}
				for (DmIndex index : dbtable.indexes) {
					if(index.containsOnlyRecid()){
						dbtable.primaryKey=index;
						return;
					}
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadIndexes.clearParameters();
		}
		// dbtable.primaryKey = null;
		// try {
		// this.loadPKeyConst.setString(1, dbtable.name);
		// ResultSet rs = this.loadPKeyConst.executeQuery();
		// try {
		// if (rs.next()) {
		// final String pkIndexName = rs.getString(2);
		// dbtable.primaryKey = dbtable.getIndex(pkIndexName);
		// }
		// } finally {
		// rs.close();
		// }
		// } finally {
		// this.loadPKeyConst.clearParameters();
		// }
	}

	public static final void quote(Appendable str, String name) {
		DbProduct.Dameng.quote(str, name);
	}

	@Override
	public boolean tableContainRows(String table) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, table);
		sql.append(" where rownum <= 1");
		return this.exist(sql);
	}

	private static final String EXISTS_TABLE = "select 1 from user_tables where table_name = ?";
	private final PreparedStatementWrap existsTable;

	@Override
	public boolean existsTable(String tableName) throws SQLException {
		try {
			this.existsTable.setString(1, tableName);
			return this.existsTable.exist();
		} finally {
			this.existsTable.clearParameters();
		}
	}

	@Override
	public void createTable(DBTableDefineImpl define) throws SQLException {
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
			sql.append(" not null");
		}
	}

	private static final DefaultFormat defaultDeclare = new DefaultFormat() {

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "timestamp\'" + DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME) + "\'";
		}

		@Override
		protected String formatSql(byte[] value) {
			return Convert.bytesToHex(value, true, true);
		}

		@Override
		public String inNumeric(ConstExpr userData, int precision, int scale)
				throws Throwable {
			if (userData.getDouble() == 0D) {
				return "0.";
			}
			return super.inNumeric(userData, precision, scale);
		}
	};

	@Override
	public boolean defaultChanged(TableFieldDefineImpl field, DmColumn column) {
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
	public void dropIndex(DmIndex index) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop index ");
		quote(sql, index.name);
		this.execute(sql);
	}

	@Override
	public boolean indexColumnContainNull(IndexDefineImpl index)
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
			this.dbMetadata.quoteId(sql, item.getField().namedb());
		}
		sql.append(" having count(*) > 1");
		return this.exist(sql);
	}

	@Override
	public boolean indexValueDuplicated(IndexDefineImpl index)
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
		sql.append(" having count(*) > 1");
		return this.exist(sql);
	}

	@Override
	public boolean columnContainNull(DmColumn column) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, column.table.name);
		sql.append(" where ");
		quote(sql, column.name);
		sql.append(" is null and rownum <= 1");
		return this.exist(sql);
	}

	@Override
	public void renameColumnAndSetNullable(DmColumn column, String rename)
			throws SQLException {
		final DmMetadata omd = this.dbMetadata;
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		omd.quoteId(sql, column.table.name);
		sql.append(" alter column ");
		omd.quoteId(sql, column.name);
		sql.append(" rename to ");
		omd.quoteId(sql, rename);
		this.execute(sql);
		if (column.notNull) {
			StringBuilder set = new StringBuilder();
			set.append("alter table ");
			omd.quoteId(set, column.table.name);
			set.append(" alter ");
			omd.quoteId(set, rename);
			set.append(" set null");
			this.execute(set);
		}
	}

	@Override
	public TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
			DmColumn column) {
		return field.getType().detect(compatible, column);
	}

	private static final TypeDetector<TypeCompatiblity, DmColumn> compatible = new TypeDetectorBase<TypeCompatiblity, DmColumn>() {

		@Override
		public TypeCompatiblity inBoolean(DmColumn column) throws Throwable {
			if (column.type == DmDataType.BIT) {
				return Exactly;
			} else if (column.type == DmDataType.SMALLINT || column.type == DmDataType.INT || column.type == DmDataType.BIGINT) {
				return Overflow;
			} else if (column.type == DmDataType.NUMERIC && column.precision >= 1 && column.scale == 0) {
				// TODO 临时
				return Overflow;
				// return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(DmColumn column) throws Throwable {
			if (column.type == DmDataType.SMALLINT) {
				return Exactly;
			} else if (column.type == DmDataType.INT || column.type == DmDataType.BIGINT) {
				return Overflow;
			} else if (column.type == DmDataType.NUMERIC && column.precision >= 5 && column.scale == 0) {
				// TODO 临时
				return Overflow;
				// return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(DmColumn column) throws Throwable {
			if (column.type == DmDataType.INT) {
				return Exactly;
			} else if (column.type == DmDataType.BIGINT) {
				return Overflow;
			} else if (column.type == DmDataType.NUMERIC && column.precision >= 10 && column.scale == 0) {
				// TODO 临时
				return Overflow;
				// return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(DmColumn column) throws Throwable {
			if (column.type == DmDataType.BIGINT) {
				return Exactly;
			} else if (column.type == DmDataType.NUMERIC && column.precision >= 19 && column.scale == 0) {
				// TODO 临时
				return Overflow;
				// return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(DmColumn column) throws Throwable {
			if (column.type == DmDataType.REAL) {
				return Exactly;
			} else if (column.type == DmDataType.DOUBLE) {
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(DmColumn column) throws Throwable {
			if (column.type == DmDataType.DOUBLE) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(DmColumn column, int precision,
				int scale) throws Throwable {
			if (column.type == DmDataType.NUMERIC) {
				if (column.precision == precision && column.scale == scale) {
					return Exactly;
				} else if (((column.precision - column.scale) >= (precision - scale)) && column.scale >= scale) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inChar(DmColumn column, SequenceDataType type)
				throws Throwable {
			if (column.type == DmDataType.CHAR || column.type == DmDataType.NCHAR) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return NotSuggest;
				}
			} else if (column.type == DmDataType.VARCHAR || column.type == DmDataType.NVARCHAR) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(DmColumn column, SequenceDataType type)
				throws Throwable {
			if (column.type == DmDataType.NVARCHAR || column.type == DmDataType.VARCHAR) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(DmColumn column) throws Throwable {
			if (column.type == DmDataType.CLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(DmColumn column, SequenceDataType type)
				throws Throwable {
			if (column.type == DmDataType.CHAR || column.type == DmDataType.NCHAR) {
				if (column.length == type.getMaxLength() * 2) {
					return Exactly;
				} else if (column.length > type.getMaxLength() * 2) {
					return NotSuggest;
				}
			} else if (column.type == DmDataType.VARCHAR || column.type == DmDataType.NVARCHAR) {
				if (column.length == type.getMaxLength() * 2) {
					return Exactly;
				} else if (column.length > type.getMaxLength() * 2) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(DmColumn column,
				SequenceDataType type) throws Throwable {
			if (column.type == DmDataType.NVARCHAR || column.type == DmDataType.VARCHAR) {
				final int adjust = type.getMaxLength() * 2;// GBK编码长度
				if (column.length == adjust) {
					return Exactly;
				} else if (column.length > adjust) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(DmColumn column) throws Throwable {
			if (column.type == DmDataType.CLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(DmColumn column, SequenceDataType type)
				throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == DmDataType.BINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			} else if (column.type == DmDataType.VARBINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(DmColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == DmDataType.VARBINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(DmColumn column) throws Throwable {
			if (column.type == DmDataType.BLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(DmColumn column) throws Throwable {
			if (column.type == DmDataType.BINARY && column.length == 16) {
				return Exactly;
			} else if (column.type == DmDataType.VARBINARY && column.length >= 16) {
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(DmColumn column) throws Throwable {
			if (column.type == DmDataType.TIMESTAMP) {
				return Exactly;
			}
			return Unable;
		}
	};

	final void addOrModifyColumns(DmTable dbtable,
			DmDbSync.ColumnCompareCache compareCache) throws SQLException {
		if (compareCache.modifyQueue.size() > 0) {
			for (DmDbSync.ModifyFieldState state : compareCache.modifyQueue.values()) {
				TableFieldDefineImpl field = state.field;
				if (state.get(DbSyncBase.MOD_TYPE)) {
					SqlBuilder sql = new SqlBuilder(this.dbMetadata);
					sql.append("alter table ").appendId(dbtable.name);
					sql.append(" modify ");
					sql.appendId(field.namedb());
					sql.nSpace();
					sql.appendType(field.getType());
					this.execute(sql);
				}
				if (state.get(DbSyncBase.MOD_NULLABLE)) {
					SqlBuilder sql = new SqlBuilder(this.dbMetadata);
					sql.append("alter table ").appendId(dbtable.name);
					sql.append(" alter column ");
					sql.appendId(field.namedb());
					sql.append(" set ");
					if (field.isKeepValid()) {
						sql.append(" not null");
					} else {
						sql.append(" null");
					}
					this.execute(sql);
				}
				if (state.get(DbSyncBase.MOD_DEFAULT)) {
					SqlBuilder sql = new SqlBuilder(this.dbMetadata);
					sql.append("alter table ").appendId(dbtable.name);
					sql.append(" alter column ");
					sql.appendId(field.namedb());
					if (field.getDefault() == null || field.getDefault().isNull()) {
						sql.append(" drop default");
					} else {
						sql.append(" set default ");
						sql.append(defaultDefinition(field, defaultDeclare));
					}
					this.execute(sql);
				}
			}
		}
		if (compareCache.addQueue.size() > 0) {
			for (DmDbSync.AddFieldState state : compareCache.addQueue) {
				TableFieldDefineImpl field = state.field;
				SqlBuilder sql = new SqlBuilder(this.dbMetadata);
				sql.append("alter table ").appendId(dbtable.name);
				sql.append(" add column ");
				columnDefinition(sql, field, false);
				this.execute(sql);
			}
		}
	}
	
	final void dbDropColumns(DmTable dbtable,
			DmDbSync.ColumnCompareCache compareCache) throws SQLException {
		if (compareCache.dropQueue.size() > 0) {
			for (int i = 0, c = compareCache.dropQueue.size(); i < c; i++) {
				SqlBuilder sql = new SqlBuilder(this.dbMetadata);
				sql.append("alter table ").appendId(dbtable.name);
				sql.nNewline().pi().append("drop ");
				sql.appendId(((DmColumn)compareCache.dropQueue.get(i)).name);
				this.execute(sql);
			}
		}
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
	
	private static final String SELECT_OBJECT_STATUS = "select status from user_objects where object_type = ? and object_name = ?";
	private final PreparedStatementWrap getObjStatus;

	final DmObjectStatus getObjectStatus(DmObjectType type, String name) {
		try {
			try {
				this.getObjStatus.setString(1, type.name());
				this.getObjStatus.setString(2, name);
				ResultSet rs = this.getObjStatus.executeQuery();
				try {
					if (rs.next()) {
						final String status = rs.getString(1);
						// DM数据库存储过程状态为null，编译不通过不会保存
						if (status == null) {
							return DmObjectStatus.VALID;
						} else if (status.equals(DmObjectStatus.VALID.value)) {
							return DmObjectStatus.VALID;
						} else if (status.equals(DmObjectStatus.INVALID.value)) {
							return DmObjectStatus.INVALID;
						} else {
							return DmObjectStatus.NA;
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

	final DmObjectStatus getProcedureStatus(
			StoredProcedureDefineImpl procedure) {
		return this.getObjectStatus(DmObjectType.PROCEDURE, procedure.name);
	}

	final DmObjectStatus getFunctionStatus(String function) {
		return this.getObjectStatus(DmObjectType.FUNCTION, function);
	}

	final void dbDropFunction(String functionName) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop function ");
		quote(sql, functionName);
		this.execute(sql);
	}
	
	final void dropProcedure(String procedure) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop procedure ");
		quote(sql, procedure);
		this.execute(sql);
	}
	
	/**
	 * DM数据库中获取表的类型（普通表还是全局临时表）
	 */
	private static final String TABLE_TYPE = "select count(*) from user_tables where table_name = ? and upper(temporary) = 'Y' ";
	private final PreparedStatementWrap loadTableType;
	@Override
    public TableType loadTableType(String tableName) throws SQLException{
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
