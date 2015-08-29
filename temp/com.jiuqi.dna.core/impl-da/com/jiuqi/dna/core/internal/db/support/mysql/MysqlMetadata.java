package com.jiuqi.dna.core.internal.db.support.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.da.SQLExecutionException;
import com.jiuqi.dna.core.da.SQLUniqueConstraintViolationException;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.common.Charsets;
import com.jiuqi.dna.core.internal.common.Strings;
import com.jiuqi.dna.core.internal.da.sql.render.MysqlSpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadataInitilizationException;
import com.jiuqi.dna.core.internal.db.metadata.UnsupportedDatabaseConfigException;
import com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer.MysqlCommandFactory;
import com.jiuqi.dna.core.internal.db.support.mysql.sync.MysqlDbSync;
import com.jiuqi.dna.core.internal.db.sync.DbSync;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public class MysqlMetadata extends DbMetadata {

	public MysqlMetadata(Connection conn) throws SQLException {
		super(conn, "mysql");
		this.database = conn.getCatalog();
		this.dbcs = dbcs(conn);
	}

	public final Charset dbcs;

	private static final String SUPPORTED_CHARSET = "mysql_dna-supported-charset";

	private static final Set<String> supportedCharsets() {
		final HashSet<String> set = new HashSet<String>();
		final InputStream is = MysqlMetadata.class.getResourceAsStream(SUPPORTED_CHARSET);
		if (is == null) {
			throw new DbMetadataInitilizationException("无法读取'DNA支持的MySQL配置参数'文件.");
		}
		try {
			try {
				Strings.readLines(is, Charsets.GBK, set);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new DbMetadataInitilizationException(e);
		}
		return set;
	}

	private static final String SHOW_CHARSET_DATABASE = "show variables where variable_name = 'character_set_database'";

	private static final String dbcsName(Connection conn) {
		try {
			Statement s = conn.createStatement();
			try {
				ResultSet rs = s.executeQuery(SHOW_CHARSET_DATABASE);
				try {
					if (rs.next()) {
						return rs.getString(2);
					}
					return null;
				} finally {
					rs.close();
				}
			} finally {
				s.close();
			}
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private static final Charset dbcs(Connection conn) {
		final String dbcs = dbcsName(conn);
		if (Charset.isSupported(dbcs) && supportedCharsets().contains(dbcs)) {
			return Charset.forName(dbcs);
		}
		throw new UnsupportedDatabaseConfigException("MySQL配置参数“数据库默认字符集(character_set_database)”为[" + dbcs + "]，DNA框架不支持该配置。");
	}

	@Override
	public final Charset databaseCharset() {
		return this.dbcs;
	}

	@Override
	public final DbProduct product() {
		return DbProduct.MySQL;
	}

	@Override
	public String[] getModifiers() {
		return modifiers;
	}

	private static final String[] modifiers = new String[] { "mysql5", "mysql" };

	private static final String CHECK_CONN = "select 1 from dual";

	@Override
	public final String getCheckConnSql() {
		return CHECK_CONN;
	}

	public final String database;

	@Override
	public final int getMaxTablePartCount() {
		return 1024;
	}

	@Override
	public final int getDefaultPartSuggestion() {
		return 30;
	}

	@Override
	public final int getMaxTableNameLength() {
		return 64;
	}

	@Override
	public final int getMaxColumnNameLength() {
		return 64;
	}

	@Override
	public final int getMaxIndexNameLength() {
		return 64;
	}

	@Override
	public final int getMaxColumnsInSelect() {
		return 256;
	}

	@Override
	public final void format(Appendable str, DataType type) {
		type.detect(formatter, str);
	}

	static final TypeDetectorBase<Object, Appendable> formatter = new TypeDetectorBase<Object, Appendable>() {

		@Override
		public final Object inBoolean(Appendable sql) throws Throwable {
			sql.append("bit");
			return null;
		}

		@Override
		public final Object inShort(Appendable sql) throws Throwable {
			sql.append("smallint");
			return null;
		}

		@Override
		public final Object inInt(Appendable sql) throws Throwable {
			sql.append("int");
			return null;
		}

		@Override
		public final Object inLong(Appendable sql) throws Throwable {
			sql.append("bigint");
			return null;
		}

		@Override
		public final Object inNumeric(Appendable sql, int precision, int scale)
				throws Throwable {
			sql.append("decimal(");
			sql.append(Convert.toString(precision));
			sql.append(',');
			sql.append(Convert.toString(scale));
			sql.append(')');
			return null;
		}

		@Override
		public final Object inFloat(Appendable sql) throws Throwable {
			sql.append("float");
			return null;
		}

		@Override
		public final Object inDouble(Appendable sql) throws Throwable {
			sql.append("double");
			return null;
		}

		@Override
		public final Object inChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("char(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		@Override
		public final Object inVarChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("varchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		@Override
		public final Object inText(Appendable sql) throws Throwable {
			sql.append("longtext");
			return null;
		}

		@Override
		public final Object inNChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("nchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(")");
			return null;
		}

		@Override
		public final Object inNVarChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("nvarchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		@Override
		public final Object inNText(Appendable sql) throws Throwable {
			sql.append("longtext charset utf8");
			return null;
		}

		@Override
		public final Boolean inBinary(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("binary(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(")");
			return null;
		}

		@Override
		public final Object inVarBinary(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("varbinary(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(")");
			return null;
		}

		@Override
		public final Object inBlob(Appendable sql) throws Throwable {
			sql.append("longblob");
			return null;
		}

		@Override
		public final Object inDate(Appendable sql) throws Throwable {
			sql.append("datetime");
			return null;
		}

		@Override
		public final Object inGUID(Appendable sql) throws Throwable {
			sql.append("binary(16)");
			return null;
		}
	};

	@Override
	public final DbSync newDbRefactor(PooledConnection conn,
			ExceptionCatcher catcher) {
		return new MysqlDbSync(conn, this, catcher);
	}

	public static final void quote(Appendable str, String name) {
		try {
			str.append('`').append(name).append('`');
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public final void quoteId(Appendable str, String name) {
		quote(str, name);
	}

	@Override
	public final MysqlCommandFactory sqlbuffers() {
		return MysqlCommandFactory.INSTANCE;
	}

	@Override
	public final SpCallSql spCallSqlFor(StoredProcedureDefineImpl procedure) {
		return new MysqlSpCallSql(this, procedure);
	}

	@Override
	public void initConnectionProps(Properties props) {
		super.initConnectionProps(props);
		props.put("allowMultiQueries", "true");
	}

	@Override
	public final boolean supportsRenameColumn() {
		return true;
	}

	@Override
	public final boolean supportsAddNotNullColumnWithoutDefaultOnEmptyTable() {
		return true;
	}

	@Override
	public SQLExecutionException raise(SQLException e, String sql) {
		if (e.getErrorCode() == 1062) {
			return new SQLUniqueConstraintViolationException(e.getMessage(), e, sql);
		}
		return super.raise(e, sql);
	}
}