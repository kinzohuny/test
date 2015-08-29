package com.jiuqi.dna.core.internal.db.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.da.SQLExecutionException;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.internal.common.Charsets;
import com.jiuqi.dna.core.internal.common.Strings;
import com.jiuqi.dna.core.internal.da.sql.render.DeprecatedSimpleDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.DeprecatedSimpleInsertSql;
import com.jiuqi.dna.core.internal.da.sql.render.DeprecatedSimpleUpdateSql;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.da.sql.render.RowSimpleDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.RowSimpleInsertSql;
import com.jiuqi.dna.core.internal.da.sql.render.RowSimpleUpdateSql;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.sync.DbSync;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.DataType;

/**
 * 数据库实例元数据
 * 
 * @author houchunlei
 * 
 */
public abstract class DbMetadata {

	public DbMetadata(Connection conn, String postfix) throws SQLException {
		this.initKeywords(postfix);
		DatabaseMetaData dbmd = conn.getMetaData();
		this.dbProductName = dbmd.getDatabaseProductName();
		this.dbMajorVer = dbmd.getDatabaseMajorVersion();
		this.dbMinorVer = dbmd.getDatabaseMinorVersion();
		this.user = dbmd.getUserName();
	}

	public final String user;

	@Override
	public final String toString() {
		return this.product().toString() + "_" + this.dbMajorVer + "." + this.dbMinorVer;
	}

	private final void initKeywords(String postfix) {
		try {
			InputStream is = this.getClass().getResourceAsStream(KEYWORDS_FILE + '.' + postfix);
			if (is != null) {
				try {
					Strings.readLines(is, Charsets.GBK, this.keywords);
				} finally {
					is.close();
				}
			}
		} catch (IOException e) {
			System.err.println("读取" + postfix + "关键字列表文件错误");
			e.printStackTrace();
		}
	}

	static final String KEYWORDS_FILE = "keywords";

	public final String dbProductName;

	public final int dbMajorVer;

	public final int dbMinorVer;

	private final HashSet<String> keywords = new HashSet<String>();

	public final boolean filterKeyword(String name) {
		return this.keywords.contains(name.toUpperCase());
	}

	public Charset databaseCharset() {
		return Charsets.GBK;
	}

	public void initConnectionProps(Properties props) {
	}

	public abstract String getCheckConnSql();

	public abstract String[] getModifiers();

	public abstract DbProduct product();

	public abstract int getMaxTableNameLength();

	public abstract int getMaxColumnNameLength();

	public abstract int getMaxIndexNameLength();

	public abstract int getMaxTablePartCount();

	public abstract int getDefaultPartSuggestion();

	public abstract int getMaxColumnsInSelect();

	public abstract void quoteId(Appendable str, String name);

	public abstract void format(Appendable str, DataType type);

	public abstract DbSync newDbRefactor(PooledConnection conn,
			ExceptionCatcher catcher);

	public abstract ISqlCommandFactory sqlbuffers();

	public ModifySql insertSqlFor(InsertStatementImpl insert) {
		return new DeprecatedSimpleInsertSql(this, insert);
	}

	public ModifySql deleteSqlFor(DeleteStatementImpl delete) {
		return new DeprecatedSimpleDeleteSql(this, delete);
	}

	public ModifySql updateSqlFor(UpdateStatementImpl update) {
		return new DeprecatedSimpleUpdateSql(this, update);
	}

	public ModifySql getRowInsertSql(QueryStatementBase queryStatment) {
		return new RowSimpleInsertSql(this, queryStatment);
	}

	public ModifySql getRowDeleteSql(QueryStatementBase queryStatment) {
		return new RowSimpleDeleteSql(this, queryStatment);
	}

	public ModifySql getRowUpdateSql(QueryStatementBase queryStatment) {
		return new RowSimpleUpdateSql(this, queryStatment);
	}

	public abstract SpCallSql spCallSqlFor(StoredProcedureDefineImpl procedure);

	public abstract boolean supportsRenameColumn();

	public abstract boolean supportsAddNotNullColumnWithoutDefaultOnEmptyTable();

	public SQLExecutionException raise(SQLException e, String sql) {
		return new SQLExecutionException(e.getMessage(), e, sql);
	}

	public boolean supportesBitmapIndex() {
		return false;
	}

	/**
	 * @throws SQLException
	 */
	public void init(Connection conn) throws SQLException {
	}
}