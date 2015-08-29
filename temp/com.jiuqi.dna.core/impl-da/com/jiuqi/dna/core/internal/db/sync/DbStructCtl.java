package com.jiuqi.dna.core.internal.db.sync;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.def.table.TableType;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.CsvReader;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.IndexItemImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.common.Charsets;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.datasource.StatementWrap;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.misc.ExceptionCatcher;

public abstract class DbStructCtl<TMetadata extends DbMetadata, TTable extends DbTable<TTable, TColumn, TDataType, TIndex>, TColumn extends DbColumn<TTable, TColumn, TDataType, TIndex>, TDataType extends DbDataType<TTable, TColumn, TDataType, TIndex>, TIndex extends DbIndex<TTable, TColumn, TDataType, TIndex>> {

	protected final PooledConnection conn;
	protected final TMetadata dbMetadata;
	protected final ExceptionCatcher catcher;

	protected DbStructCtl(PooledConnection conn, TMetadata dbMetadata,
			ExceptionCatcher catcher) {
		this.conn = conn;
		this.dbMetadata = dbMetadata;
		this.catcher = catcher;
	}

	final void dispose() {
		if (this.statement != null) {
			try {
				this.statement.close();
			} catch (SQLException e) {
				this.catcher.catchException(e, this);
			}
		}
		this.releaseStatements(this.catcher);
	}

	private ArrayList<PreparedStatementWrap> pstmts = new ArrayList<PreparedStatementWrap>();

	protected final PreparedStatementWrap prepareStatement(String sql) {
		final PreparedStatementWrap pstmt;
		try {
			pstmt = this.conn.prepareStatement(sql, SqlSource.CORE_DML);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		this.pstmts.add(pstmt);
		return pstmt;
	}

	private final void releaseStatements(ExceptionCatcher catcher) {
		SQLException th = null;
		for (PreparedStatementWrap ps : this.pstmts) {
			try {
				ps.close();
			} catch (SQLException e) {
				th = e;
				continue;
			}
		}
		this.pstmts.clear();
		if (th != null) {
			throw Utils.tryThrowException(th);
		}
	}

	private StatementWrap statement;

	private final StatementWrap ensureStatement() throws SQLException {
		if (this.statement == null) {
			this.statement = this.conn.createStatement();
		}
		return this.statement;
	}

	public final void execute(CharSequence sql) throws SQLException {
		this.execute(sql, SqlSource.USER_DDL);
	}

	public final void execute(CharSequence sql, SqlSource source)
			throws SQLException {
		final String s = sql.toString();
		this.ensureStatement().execute(s, source);
		this.executed.add(s);
	}

	protected final ResultSet query(CharSequence sql) throws SQLException {
		return this.ensureStatement().executeQuery(sql.toString(), SqlSource.CORE_DML);
	}

	protected final boolean exist(CharSequence sql) throws SQLException {
		final ResultSet rs = this.query(sql);
		try {
			if (rs.next()) {
				return true;
			}
			return false;
		} finally {
			rs.close();
		}
	}

	public final TTable loadTable(String name) {
		final TTable table = this.newDbTableOnly(name);
		try {
			this.loadColumns(table);
			if (table.columns.size() == 0) {
				return null;
			}
			if (this.dbMetadata.product() == DbProduct.SQLServer) {
				this.loadIndexes(table);
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		return table;
	}

	protected abstract TTable newDbTableOnly(String name);

	protected abstract void loadColumns(TTable dbtable) throws SQLException;

	protected abstract void loadIndexes(TTable dbtable) throws SQLException;

	public abstract boolean tableContainRows(String table) throws SQLException;

	public abstract boolean existsTable(String tableName) throws SQLException;
	
	/**
	 * 
	 * 返回表的类型，全局临时表还是普通表
	 * @param tableName 物理表名
	 * @return
	 * @throws SQLException TableType
	 */
	public abstract TableType loadTableType(String tableName) throws SQLException;
	/**
	 * 同步逻辑表
	 * 
	 * @param tableName
	 *            物理表标识
	 * @param category
	 *            目录
	 * @return
	 * @throws SQLException
	 */
	public final TableDefineImpl synchroTableDefine(String tableName,
			String title, String category) {
		TableDefineImpl tableDefine = null;
		TTable dbTable = this.loadTable(tableName);

		if (dbTable != null && dbTable.columns.size() <= 500) {
			boolean synchroable = false;
			if (dbTable.primaryKey != null) {
				synchroable = "RECID".equals(dbTable.primaryKey.name);
			}
			if (!synchroable) {
				Iterator<TColumn> columns = dbTable.columns.iterator();
				while (columns.hasNext()) {
					TColumn column = columns.next();
					if ("RECID".equals(column.name)) {
						synchroable = true;
						break;
					}
				}
			}
			if (synchroable) {
				Iterator<TColumn> columns = dbTable.columns.iterator();
				tableDefine = new TableDefineImpl(tableName, null);
				while (columns.hasNext()) {
					TColumn column = columns.next();
					if (tableDefine.findColumn(column.name) == null) {
						TableFieldDefineImpl field = tableDefine.newField(column.name, column.type.convertToDataType(column));
						field.setTitle(column.name);
					}
				}
				tableDefine.setCategory(category);
				tableDefine.setTitle(title);
			}
		}
		return tableDefine;
	}

	public abstract void createTable(DBTableDefineImpl define)
			throws SQLException;

	protected static final void outlineRecidConstraint(SqlBuilder sql,
			DBTableDefineImpl dbTable) {
		outlinePrimaryKeyConstraintDefinition(sql, dbTable.getPkeyName(), dbTable.getOwner().f_recid.getNameInDB());
	}

	protected static final void outlinePrimaryKeyConstraintDefinition(
			SqlBuilder sql, String name, String column, String... others) {
		sql.appendConstraint();
		sql.appendId(name);
		sql.appendPrimaryKey();
		sql.lp().appendId(column);
		if (others != null) {
			for (String other : others) {
				sql.nComma().appendId(other);
			}
		}
		sql.rp();
	}

	protected static final String defaultDefinition(TableFieldDefineImpl field,
			DefaultFormat fmt) {
		final ConstExpr d = field.getDefault();
		if (d == null) {
			return null;
		}
		return field.getType().detect(fmt, d);
	}

	public abstract boolean defaultChanged(TableFieldDefineImpl field,
			TColumn column);

	final void createIndex(TIndex index) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("create ");
		if (index.unique) {
			sql.append("unique ");
		}
		sql.append("index ");
		this.dbMetadata.quoteId(sql, index.name);
		sql.append(" on ");
		this.dbMetadata.quoteId(sql, index.table.name);
		sql.append('(');
		for (int i = 0, c = index.columns.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',').append(' ');
			}
			TColumn column = index.columns.get(i);
			this.dbMetadata.quoteId(sql, column.name);
			if (index.desc.get(i)) {
				sql.append(" desc");
			}
		}
		sql.append(')');
		this.execute(sql);
	}

	public final void createIndex(IndexDefineImpl index) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("create ");
		if (index.isUnique()) {
			sql.append("unique ");
		}
		sql.append("index ");
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

	public final void dropTable(String tableName) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("drop table ");
		this.dbMetadata.quoteId(sql, tableName);
		this.execute(sql.toString());
	}

	public abstract void dropTableSilently(String tableName)
			throws SQLException;

	public abstract void dropIndex(TIndex index) throws SQLException;

	public abstract boolean indexColumnContainNull(IndexDefineImpl index)
			throws SQLException;

	public abstract boolean indexValueDuplicated(IndexDefineImpl index)
			throws SQLException;

	public abstract boolean columnContainNull(TColumn column)
			throws SQLException;

	/**
	 * 重命名列名称,并且确保其可为空
	 * 
	 * @param column
	 * @param rename
	 * @throws SQLException
	 */
	public abstract void renameColumnAndSetNullable(TColumn column,
			String rename) throws SQLException;

	public abstract TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
			TColumn column);

	public static final String CORE_COLLATE_GBK = "CORE_COLLATE_GBK";
	public static final String CORE_COLLATE_GBK_CH = "CH";
	public static final String CORE_COLLATE_GBK_SN = "SN";

	protected static interface CollateSetter {

		void set(PreparedStatementWrap ps, final String ch, final byte[] b)
				throws SQLException;
	}

	private static int CORE_COLLATE_GBK_SN_START = 0x0100;

	protected static final void initCollateGBK(PreparedStatementWrap ps,
			CollateSetter setter, final int batch) throws SQLException,
			IOException {
		// all gbk character exclude 0xA892
		InputStream is = DbStructCtl.class.getResourceAsStream("collate_gbk.csv");
		try {
			int i = CORE_COLLATE_GBK_SN_START;
			int c = 0;
			CsvReader reader = new CsvReader(is, Charsets.GBK);
			while (reader.readRecord()) {
				final String ch = reader.get(0);
				final byte[] b = new byte[] { (byte) (i >> 8), (byte) i };
				setter.set(ps, ch, b);
				if (batch == 0) {
					ps.executeUpdate();
				} else {
					ps.addBatch();
					if (++c % batch == 0) {
						ps.executeBatch();
						c = 0;
					}
				}
				i++;
			}
			if (c > 0) {
				ps.executeBatch();
			}
		} finally {
			is.close();
		}
	}

	final ArrayList<String> executed = new ArrayList<String>();

	final void logExecutedSql(boolean syncOrPost) {
		if (this.executed.size() > 0) {
			final StringBuilder s = new StringBuilder();
			s.append("执行数据库[" + (syncOrPost ? "同步" : "发布") + "]操作：");
			for (String sql : this.executed) {
				s.append("\r\n").append(sql);
			}
			DNALogManager.getLogger("core/db/sync").logInfo(null, s.toString(), null, false);
			this.executed.clear();
		}
	}
}