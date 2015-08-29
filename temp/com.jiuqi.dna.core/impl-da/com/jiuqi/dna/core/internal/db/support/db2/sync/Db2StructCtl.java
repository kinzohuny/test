package com.jiuqi.dna.core.internal.db.support.db2.sync;

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
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.support.db2.Db2Metadata;
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

final class Db2StructCtl extends
		DbStructCtl<Db2Metadata, Db2Table, Db2Column, Db2DataType, Db2Index> {

	Db2StructCtl(PooledConnection conn, Db2Metadata dbMetadata,
			ExceptionCatcher catcher) {
		super(conn, dbMetadata, catcher);
		this.loadColumns = this.prepareStatement(SELECT_TABLE_COLUMNS);
		this.loadIndexes = this.prepareStatement(SELECT_INDEX_COLUMNS);
		this.existsTable = this.prepareStatement(EXISTS_TABLE);
		this.snapIndexes = this.prepareStatement(SELECT_INDEX_NAMES);
		this.existsFunction = this.prepareStatement(EXISTS_FUNCTION);
		this.getProcedureSpecific = this.prepareStatement(SELECT_PROC_SPECIFICNAME);
	}

	public static final void quote(Appendable str, String name) {
		Db2Metadata.quote(str, name);
	}

	@Override
	protected final Db2Table newDbTableOnly(String name) {
		return new Db2Table(name);
	}

	/**
	 * <ol>
	 * <li>column_name
	 * <li>column_type
	 * <li>column_length
	 * <li>column_scale
	 * <li>not_null
	 * <li>default_value
	 * <li>codepage
	 * </ol>
	 * 
	 * <ol>
	 * <li>owner 即jdbc_schema
	 * <li>table_name
	 * </ol>
	 */
	private static final String SELECT_TABLE_COLUMNS = "select c.colname, c.typename, c.length, c.scale, case c.nulls when 'Y' then 0 else 1 end, c.default, c.codepage from syscat.columns c where tabschema = ? and tabname = ? order by colno";
	private final PreparedStatementWrap loadColumns;

	@Override
	protected final void loadColumns(Db2Table dbtable) throws SQLException {
		this.loadColumnsUsingSyscat(dbtable);
	}

	private final void loadColumnsUsingSyscat(Db2Table dbtable)
			throws SQLException {
		try {
			this.loadColumns.setString(1, this.dbMetadata.user);
			this.loadColumns.setString(2, dbtable.name);
			final ResultSet rs = this.loadColumns.executeQuery();
			try {
				while (rs.next()) {
					Db2Column column = dbtable.addColumn(rs.getString(1));
					final String type = rs.getString(2).trim();
					final int length = rs.getInt(3);
					final int scale = rs.getInt(4);
					final boolean notnull = rs.getBoolean(5);
					final String defVal = rs.getString(6);
					final int codepage = rs.getInt(7);
					column.precision = column.length = length;
					// trim !!! disgusting
					// binary type unsupported
					column.type = Db2DataType.valueOf(type);
					column.scale = scale;
					column.notNull = notnull;
					column.codepage = codepage;
					String defaultDefinition = defVal;
					if (!rs.wasNull() && defaultDefinition != null) {
						column.defaultDefinition = defaultDefinition;
					}
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadColumns.clearParameters();
		}
	}

	// final void loadUsingJdbc(DBAdapterImpl adapter, Db2Metadata dbMetadata)
	// throws SQLException {
	// ResultSet rs = adapter.getMetaData().getColumns(dbMetadata.database,
	// dbMetadata.user, this.name, null);
	// try {
	// while (rs.next()) {
	// Db2Column column = this.addColumn(rs.getString(4));
	// column.type = Db2DataType.jdbcTypeOf(column, rs.getInt(5),
	// rs.getString(6));
	// column.length = column.precision = rs.getInt(7);
	// column.notNull = rs.getString(18).equals("NO");
	// column.defaultDefinition = rs.getString(13);
	// }
	// } finally {
	// rs.close();
	// }
	// }

	/**
	 * 输出:
	 * <ol>
	 * <li>索引名称
	 * <li>索引规则
	 * <li>索引列
	 * <li>索引列排序
	 * </ol>
	 * 
	 * 参数
	 * <ol>
	 * <li>索引模式
	 * <li>表模式
	 * <li>表名
	 * </ol>
	 */
	private static final String SELECT_INDEX_COLUMNS = "select i.indname, i.uniquerule, icu.colname, case icu.colorder when 'D' then 1 else 0 end desc from syscat.indexes i join syscat.indexcoluse icu on i.indschema = icu.indschema and i.indname = icu.indname where i.indschema = ? and i.tabschema = ? and i.tabname = ? order by icu.colseq";
	private final PreparedStatementWrap loadIndexes;

	@Override
	protected final void loadIndexes(Db2Table dbtable) throws SQLException {
		try {
			this.loadIndexes.setString(1, this.dbMetadata.user);
			this.loadIndexes.setString(2, this.dbMetadata.user);
			this.loadIndexes.setString(3, dbtable.name);
			ResultSet rs = this.loadIndexes.executeQuery();
			try {
				while (rs.next()) {
					final String indname = rs.getString(1);
					Db2Index index = dbtable.findIndex(indname);
					if (index == null) {
						final String rulename = rs.getString(2);
						final Db2IndexUniqueRule rule = Db2IndexUniqueRule.valueOf(rulename);
						index = dbtable.addIndex(indname, rule.unqiue);
						if (rule.primaryKey && dbtable.primaryKey == null) {
							dbtable.primaryKey = index;
						}
					}
					final Db2Column column = dbtable.getColumn(rs.getString(3));
					index.add(column, rs.getBoolean(4));
				}
			} finally {
				rs.close();
			}
		} finally {
			this.loadIndexes.close();
		}
	}

	@Override
	public final boolean tableContainRows(String table) throws SQLException {
		final StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, table);
		sql.append(" fetch first 1 row only");
		try {
			return this.exist(sql);
		} catch (Throwable e) {
			this.reorg(table);
			return this.exist(sql);
		}
	}

	private static final String EXISTS_TABLE = "select 1 from syscat.tables where tabschema = ? and tabname = ? and type = 'T'";
	private final PreparedStatementWrap existsTable;

	@Override
	public final boolean existsTable(String tableName) throws SQLException {
		try {
			this.existsTable.setString(1, this.dbMetadata.user);
			this.existsTable.setString(2, tableName);
			return this.existsTable.exist();
		} finally {
			this.existsTable.clearParameters();
		}
	}

	@Override
	public final void createTable(DBTableDefineImpl define) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.appendCreate().appendTable().appendId(define.namedb()).lp();
		sql.nNewline().pi();
		for (int i = 0, c = define.owner.fields.size(); i < c; i++) {
			TableFieldDefineImpl field = define.owner.fields.get(i);
			if (field.dbTable == define || field.isRECID()) {
				columnDefinition(sql, field, false);
				sql.nComma().nNewline();
			}
		}
		outlineRecidConstraint(sql, define);
		sql.nNewline().ri().rp();
		this.execute(sql);
	}

	@Override
	public final void dropTableSilently(String tableName) throws SQLException {
		if (this.existsTable(tableName)) {
			this.dropTable(tableName);
		}
	}

	@Override
	public final void dropIndex(Db2Index index) throws SQLException {
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("drop index ").appendId(index.name);
		this.execute(sql);
	}

	/**
	 * column_name, data_type, column_options
	 */
	private static final void columnDefinition(SqlBuilder sql,
			TableFieldDefineImpl field, boolean forceNullable) {
		sql.appendId(field.namedb()).nSpace();
		sql.appendType(field.getType());
		final ConstExpr df = field.getDefault();
		if (df != null) {
			sql.nSpace().appendDefault();
			sql.append(defaultDefinition(field, defaultDeclare));
		}
		if (!forceNullable && field.isKeepValid()) {
			sql.nSpace().appendNot().appendNull();
		}
	}

	private static final void columnAlteration(SqlBuilder sql,
			Db2DbSync.ModifyFieldState state) {
		TableFieldDefineImpl field = state.field;
		sql.appendId(state.column.name).nSpace();
		if (state.get(DbSyncBase.MOD_TYPE)) {
			sql.append("set data type ").appendType(field.getType());
		}
		if (state.get(DbSyncBase.MOD_NULLABLE)) {
			if (field.isKeepValid()) {
				sql.append(" set not null");
			} else {
				sql.append(" drop not null");
			}
		}
		if (state.get(DbSyncBase.MOD_DEFAULT)) {
			if (field.getDefault() != null) {
				sql.append(" set default ");
				sql.append(defaultDefinition(field, defaultDeclare));
			} else {
				sql.append(" drop default");
			}
		}
	}

	private static final DefaultFormat defaultDeclare = new DefaultFormat() {

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "\'" + DateParser.format(c.getDate(), DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		protected String formatSql(byte[] value) {
			return "x\'" + Convert.bytesToHex(value, false, false) + "\'";
		}
	};

	@Override
	public final boolean defaultChanged(TableFieldDefineImpl field,
			Db2Column column) {
		final boolean leftNull = field.getDefault() == null || field.getDefault() == NullExpr.NULL;
		final boolean rightNull = column.defaultDefinition == null;
		if (leftNull != rightNull || (!leftNull && !rightNull && !column.defaultDefinition.equals(defaultDefinition(field, defaultDeclare)))) {
			return true;
		}
		return false;
	}

	@Override
	public final TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
			Db2Column column) {
		return field.getType().detect(compatible, column);
	}

	private static final TypeDetector<TypeCompatiblity, Db2Column> compatible = new TypeDetectorBase<TypeCompatiblity, Db2Column>() {

		@Override
		public TypeCompatiblity inBoolean(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.SMALLINT) {
				return Exactly;
			} else if (column.type == Db2DataType.INTEGER || column.type == Db2DataType.BIGINT) {
				return Overflow;
			} else if (column.type == Db2DataType.DECIMAL && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.SMALLINT) {
				return Exactly;
			} else if (column.type == Db2DataType.INTEGER || column.type == Db2DataType.BIGINT) {
				return Overflow;
			} else if (column.type == Db2DataType.DECIMAL && column.precision >= 5 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.INTEGER) {
				return Exactly;
			} else if (column.type == Db2DataType.BIGINT) {
				return Overflow;
			} else if (column.type == Db2DataType.DECIMAL && column.precision >= 10 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.BIGINT) {
				return Exactly;
			} else if (column.type == Db2DataType.DECIMAL && column.precision >= 19 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.REAL) {
				return Exactly;
			} else if (column.type == Db2DataType.DOUBLE) {
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.DOUBLE) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(Db2Column column, int precision,
				int scale) throws Throwable {
			if (column.type == Db2DataType.DECIMAL) {
				if (column.precision == precision && column.scale == scale) {
					return Exactly;
				} else if (((column.precision - column.scale) >= (precision - scale)) && column.scale >= scale) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inChar(Db2Column column, SequenceDataType type)
				throws Throwable {
			if (column.forbitdata()) {
				return Unable;
			}
			if (column.type == Db2DataType.VARCHAR) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			} else if (column.type == Db2DataType.VARGRAPHIC) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(Db2Column column,
				SequenceDataType type) throws Throwable {
			if (column.forbitdata()) {
				return Unable;
			}
			if (column.type == Db2DataType.VARCHAR) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			} else if (column.type == Db2DataType.VARGRAPHIC) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.CLOB) {
				return Exactly;
			} else if (column.type == Db2DataType.DBCLOB) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(Db2Column column, SequenceDataType type)
				throws Throwable {
			if (column.forbitdata()) {
				return Unable;
			}
			if (column.type == Db2DataType.VARCHAR) {
				if (column.length == type.getMaxLength() * 2) {
					return Exactly;
				} else if (column.length > type.getMaxLength() * 2) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(Db2Column column,
				SequenceDataType type) throws Throwable {
			if (column.forbitdata()) {
				return Unable;
			}
			if (column.type == Db2DataType.VARCHAR) {
				if (column.length == type.getMaxLength() * 2) {
					return Exactly;
				} else if (column.length > type.getMaxLength() * 2) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.CLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(Db2Column column, SequenceDataType type)
				throws Throwable {
			if (column.type == Db2DataType.VARCHAR && column.forbitdata()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(Db2Column column,
				SequenceDataType type) throws Throwable {
			if (column.type == Db2DataType.VARCHAR && column.forbitdata()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.BLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(Db2Column column) throws Throwable {
			if (!column.forbitdata()) {
				return Unable;
			}
			if (column.type == Db2DataType.CHARACTER && column.length == 16) {
				return Exactly;
			} else if (column.type == Db2DataType.VARCHAR && column.length >= 16) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(Db2Column column) throws Throwable {
			if (column.type == Db2DataType.TIMESTAMP) {
				return Exactly;
			}
			return Unable;
		}

	};

	@Override
	public final void renameColumnAndSetNullable(Db2Column column, String rename)
			throws SQLException {
		throw new UnsupportedOperationException("当前版本db2不支持重命名列.");
	}

	@Override
	public final boolean columnContainNull(Db2Column column)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ");
		quote(sql, column.table.name);
		sql.append(" where ");
		quote(sql, column.name);
		sql.append(" is null fetch first 1 row only");
		return this.exist(sql);
	}

	final void addnAlterColumn(Db2Table dbtable,
			Db2DbSync.ColumnCompareCache compareCache) throws SQLException {
		if (compareCache.addQueue.size() == 0 && compareCache.modifyQueue.size() == 0) {
			return;
		}
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("alter table ").appendId(dbtable.name);
		sql.nNewline().pi();
		if (compareCache.addQueue.size() > 0) {
			for (Db2DbSync.AddFieldState state : compareCache.addQueue) {
				TableFieldDefineImpl field = state.field;
				sql.append("add column ");
				columnDefinition(sql, field, state.forceNullable);
				sql.nNewline();
			}
		}
		if (compareCache.modifyQueue.size() > 0) {
			for (Db2DbSync.ModifyFieldState state : compareCache.modifyQueue.values()) {
				sql.append("alter column ");
				columnAlteration(sql, state);
				sql.nNewline();
			}
		}
		this.execute(sql.uNewline());
		this.reorg(dbtable.name);
	}

	final void dropColumns(Db2Table dbtable,
			Db2DbSync.ColumnCompareCache compareCache) throws SQLException {
		if (compareCache.dropQueue.size() == 0) {
			return;
		}
		SqlBuilder sql = new SqlBuilder(this.dbMetadata);
		sql.append("alter table ").appendId(dbtable.name);
		sql.nNewline().pi();
		for (Db2Column column : compareCache.dropQueue) {
			sql.append("drop column ").appendId(column.name);
			sql.nNewline();
		}
		this.execute(sql);
		this.reorg(dbtable.name);
	}

	final void reorg(String table) throws SQLException {
		SqlBuilder reorg = new SqlBuilder(this.dbMetadata);
		reorg.append("call sysproc.admin_cmd('reorg table ");
		reorg.appendId(table);
		reorg.append("')");
		this.execute(reorg);
	}

	final void setColumnNullable(Db2Column column) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		quote(sql, column.table.name);
		sql.append(" alter column ");
		quote(sql, column.name);
		sql.append(" drop not null");
		this.execute(sql.toString());
	}

	private static final String SELECT_INDEX_NAMES = "select indname from syscat.indexes where indschema = ?";
	private final PreparedStatementWrap snapIndexes;

	final DbNamespace snapIndexes() throws SQLException {
		final DbNamespace namespace = new DbNamespace(true);
		try {
			this.snapIndexes.setString(1, this.dbMetadata.user);
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
		sql.append("select 1 from ");
		quote(sql, index.dbTable.namedb());
		sql.append(" t where ");
		final int c = index.items.size();
		for (int i = 0; i < c; i++) {
			IndexItemImpl item = index.items.get(i);
			if (i > 0) {
				sql.append(" or ");
			}
			sql.append("t.");
			quote(sql, item.getField().namedb());
			sql.append(" is null");
		}
		sql.append(" fetch first 1 row only");
		return this.exist(sql);
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
		sql.append(" having count(*) > 1 fetch first 1 row only");
		return this.exist(sql);
	}

	private static final String EXISTS_FUNCTION = "select funcname from syscat.functions where funcschema = ? and funcname = ?";
	private final PreparedStatementWrap existsFunction;

	final boolean existsFunction(String functionName) {
		try {
			try {
				this.existsFunction.setString(1, this.dbMetadata.user);
				this.existsFunction.setString(2, functionName);
				return this.existsFunction.exist();
			} finally {
				this.existsFunction.clearParameters();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	final void dropFunction(String functionName) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop function ");
		quote(sql, functionName);
		this.execute(sql);
	}

	private static final String SELECT_PROC_SPECIFICNAME = "select specificname from syscat.procedures where procschema = ? and procname = ?";
	private final PreparedStatementWrap getProcedureSpecific;

	final void dropProcedureIfExists(String procedureName) throws SQLException {
		try {
			this.getProcedureSpecific.setString(1, this.dbMetadata.user);
			this.getProcedureSpecific.setString(2, procedureName);
			// HCL
			ResultSet rs = this.getProcedureSpecific.executeQuery();
			try {
				while (rs.next()) {
					final String specificname = rs.getString(1);
					final StringBuilder sql = new StringBuilder();
					sql.append("drop specific procedure ");
					quote(sql, specificname);
					this.execute(sql.toString());
				}
			} finally {
				rs.close();
			}
		} finally {
			this.getProcedureSpecific.clearParameters();
		}
	}

	final void createCollateGBK() throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("create table ");
		quote(sql, CORE_COLLATE_GBK);
		sql.append(" (");
		quote(sql, CORE_COLLATE_GBK_CH);
		sql.append(" varchar(2) not null primary key, ");
		quote(sql, CORE_COLLATE_GBK_SN);
		sql.append(" char(2) for bit data)");
		this.execute(sql.toString(), SqlSource.CORE_DDL);
	}

	final void initCollateGBK() throws SQLException, IOException {
		PreparedStatementWrap merge = this.conn.prepareStatement("merge into " + CORE_COLLATE_GBK + " as t using (select 1 from sysibm.dual) s on (t.CH = ?) when not matched then insert (CH, SN) values (?, ?)", SqlSource.CORE_DML);
		try {
			for (int i = 0; i < 128; i++) {
				final String s = String.valueOf((char) i);
				final byte[] b = new byte[] { 0, (byte) i };
				merge.setString(1, s);
				merge.setString(2, s);
				merge.setBytes(3, b);
				merge.addBatch();
			}
			merge.executeBatch();
			initCollateGBK(merge, new CollateSetter() {
				public void set(PreparedStatementWrap ps, String ch, byte[] b)
						throws SQLException {
					ps.setString(1, ch);
					ps.setString(2, ch);
					ps.setBytes(3, b);
				}
			}, 216);
		} finally {
			merge.close();
		}
	}

	private static final String SELECT_DNA_FUNC = "select specificname from syscat.routines where routineschema = ? and routinename like 'DNA_%' and routinetype = 'F'";

	final void dropExistingDnaFunctions() {
		try {
			PreparedStatementWrap ps = this.conn.prepareStatement(SELECT_DNA_FUNC, SqlSource.CORE_DML);
			try {
				ps.setString(1, this.dbMetadata.user);
				ResultSet rs = ps.executeQuery();
				try {
					while (rs.next()) {
						final String routine = rs.getString(1);
						try {
							this.execute("drop specific function ".concat(routine));
						} catch (Throwable e) {
							throw new SQLException("删除旧版本的自定义函数[" + routine + "]错误：" + e.getMessage());
						}
					}
				} finally {
					rs.close();
				}
			} finally {
				ps.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException("数据源初始化异常", e);
		}
	}

	@Override
    public TableType loadTableType(String tableName) throws SQLException{
	    return TableType.NORMAL;
    }
}