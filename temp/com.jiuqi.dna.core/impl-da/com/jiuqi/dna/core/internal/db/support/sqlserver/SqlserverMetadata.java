package com.jiuqi.dna.core.internal.db.support.sqlserver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.da.SQLExecutionException;
import com.jiuqi.dna.core.da.SQLUniqueConstraintViolationException;
import com.jiuqi.dna.core.def.model.ModelDefine;
import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.exception.NotDBTypeException;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.SqlserverSpCallSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.db.common.Connections;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.metadata.UnsupportedDatabaseConfigException;
import com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer.SQLServerCommandFactory;
import com.jiuqi.dna.core.internal.db.support.sqlserver.sync.SqlserverDbSync;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.EnumType;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;

/**
 * Sqlserverʵ��Ԫ���ݡ���ӦSqlserver���ﶨ������ݿ⡣
 * 
 * @author houchunlei
 * 
 */
public class SqlserverMetadata extends DbMetadata {

	public SqlserverMetadata(Connection conn) throws SQLException {
		super(conn, "sqlserver");
		this.database = conn.getCatalog();
		this.schema = this.getDefaultSchema(conn);
		validate(conn);
	}

	public final boolean beforeYukon() {
		return this.dbMajorVer < 9;
	}

	private static final void validate(Connection conn) throws SQLException {
		final int datefirst = getDatefirst(conn);
		if (datefirst != 7) {
			throw new UnsupportedDatabaseConfigException("��������ԴΪSQL Server�����ݿ����@@datefirstΪ[" + datefirst + "]�����Ҫ��Ϊ[7]��������ص����ں��������ش���ֵ��");
		}
		// HCL ����������
	}

	@Override
	public final DbProduct product() {
		return DbProduct.SQLServer;
	}

	@Override
	public final String[] getModifiers() {
		switch (this.dbMajorVer) {
		case 9:
			return MODIFIERS_9;
		case 10:
			return MODIFIERS_10;
		}
		return MODIFIERS_DEFAULT;
	}

	private static final String[] MODIFIERS_DEFAULT = new String[] { "sqlserver" };
	private static final String[] MODIFIERS_9 = new String[] { "sqlserver9", "sqlserver2005", "sqlserver" };
	private static final String[] MODIFIERS_10 = new String[] { "sqlserver10", "sqlserver2008", "sqlserver" };

	private static final String CHECK_CONN = "select 1";

	@Override
	public final String getCheckConnSql() {
		return CHECK_CONN;
	}

	/**
	 * ��ȡ���ݿ���û�(��½)
	 */
	@SuppressWarnings("unused")
	private static final String SELECT_USER = "select system_user";

	/**
	 * Ĭ�����ݿ������,����ǰ�����Ӧ���ݿ�.
	 */
	public final String database;

	/**
	 * �û�Ĭ�ϵļܹ�
	 */
	public final String schema;

	private static final String SELECT_SCHEMA = "select default_schema_name from sys.database_principals where name = ?";

	public static final String LOGIN_SA = "sa";
	public static final String SCHEMA_DBO = "dbo";

	/**
	 * ��ȡ��½�ڵ�ǰ���ݿ��Ĭ�ϼܹ�(ģʽ)
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private final String getDefaultSchema(Connection conn) throws SQLException {
		if (this.beforeYukon()) {
			return this.user.equalsIgnoreCase(LOGIN_SA) ? SCHEMA_DBO : this.user;
		}
		final PreparedStatement pstmt = conn.prepareStatement(SELECT_SCHEMA);
		try {
			pstmt.setString(1, this.user);
			final String schema = Connections.stringOf(pstmt);
			if (schema != null && schema.length() > 0) {
				return schema;
			}
			return SCHEMA_DBO;
		} finally {
			pstmt.close();
		}
	}

	// public final String collation;

	private static final String SELECT_COLLATION = "select collation_name from sys.databases where name = ?";

	/**
	 * ��ȡ���ݿ���������.
	 * 
	 * @param conn
	 * @param database
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private static final String getDatabaseCollation(Connection conn,
			String database) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement(SELECT_COLLATION);
		try {
			pstmt.setString(1, database);
			final String collation = Connections.stringOf(pstmt);
			return collation;
		} finally {
			pstmt.close();
		}
	}

	private static final String SELECT_DATE_FIRST = "select @@datefirst";

	private static final int getDatefirst(Connection conn) throws SQLException {
		return Connections.intOf(conn, SELECT_DATE_FIRST);
	}

	@Override
	public final int getMaxTablePartCount() {
		return 1000;
	}

	@Override
	public final int getDefaultPartSuggestion() {
		return 65536;
	}

	@Override
	public final int getMaxColumnNameLength() {
		return 120;
	}

	@Override
	public final int getMaxIndexNameLength() {
		return 120;
	}

	@Override
	public final int getMaxTableNameLength() {
		return 120;
	}

	@Override
	public final int getMaxColumnsInSelect() {
		return 4096;
	}

	@Override
	public final void format(Appendable str, DataType type) {
		if (this.dbMajorVer < 9) {
			type.detect(formatter8, str);
		} else {
			type.detect(formatter, str);
		}
	}

	public static abstract class TypeBase implements
			TypeDetector<Object, Appendable> {

		public Object inBinary(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("binary(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(")");
			return null;
		}

		public Object inBoolean(Appendable sql) throws Throwable {
			sql.append("bit");
			return null;
		}

		public Object inByte(Appendable sql) throws Throwable {
			throw new NotDBTypeException("byte");
		}

		public Object inBytes(Appendable sql, SequenceDataType type)
				throws Throwable {
			throw new NotDBTypeException("bytes");
		}

		public Object inChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("char(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		public Object inDate(Appendable sql) throws Throwable {
			sql.append("datetime");
			return null;
		}

		public Object inDouble(Appendable sql) throws Throwable {
			sql.append("float");
			return null;
		}

		public Object inEnum(Appendable sql, EnumType<?> type) throws Throwable {
			throw new NotDBTypeException("enum");
		}

		public Object inFloat(Appendable sql) throws Throwable {
			sql.append("real");
			return null;
		}

		public Object inGUID(Appendable sql) throws Throwable {
			sql.append("binary(16)");
			return null;
		}

		public Object inInt(Appendable sql) throws Throwable {
			sql.append("int");
			return null;
		}

		public Object inLong(Appendable sql) throws Throwable {
			sql.append("bigint");
			return null;
		}

		public Object inModel(Appendable sql, ModelDefine type)
				throws Throwable {
			throw new NotDBTypeException("ģ�Ͷ���");
		}

		public Object inNChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("nchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		public Object inNVarChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("nvarchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		public Object inNumeric(Appendable sql, int precision, int scale)
				throws Throwable {
			sql.append("numeric(");
			sql.append(Convert.toString(precision));
			sql.append(',');
			sql.append(Convert.toString(scale));
			sql.append(')');
			return null;
		}

		public Object inObject(Appendable sql, ObjectDataType type)
				throws Throwable {
			throw new NotDBTypeException("��������");
		}

		public Object inQuery(Appendable sql, QueryStatementDefine type)
				throws Throwable {
			throw new NotDBTypeException("��ѯ����");
		}

		public Object inRecordSet(Appendable sql) throws Throwable {
			throw new NotDBTypeException("�����");
		}

		public Object inResource(Appendable sql, Class<?> facadeClass,
				Object category) throws Throwable {
			throw new NotDBTypeException("��Դ");
		}

		public Object inShort(Appendable sql) throws Throwable {
			sql.append("smallint");
			return null;
		}

		public Object inString(Appendable sql, SequenceDataType type)
				throws Throwable {
			throw new NotDBTypeException("string");
		}

		public Object inStruct(Appendable sql, StructDefine type)
				throws Throwable {
			throw new NotDBTypeException("�ṹ����");
		}

		public Object inTable(Appendable sql) throws Throwable {
			throw new NotDBTypeException("����");
		}

		public Object inUnknown(Appendable sql) throws Throwable {
			throw new NotDBTypeException("δ֪");
		}

		public Object inVarBinary(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("varbinary(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(")");
			return null;
		}

		public Object inVarChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("varchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		public Object inNull(Appendable userData) throws Throwable {
			throw new NotDBTypeException("������");
		}

		public Object inCharacter(Appendable userData) throws Throwable {
			throw new NotDBTypeException("�ַ�����");
		}
	}

	public static final TypeDetector<Object, Appendable> formatter = new TypeBase() {

		public Object inBlob(Appendable sql) throws Throwable {
			sql.append("varbinary(max)");
			return null;
		}

		public Object inNText(Appendable sql) throws Throwable {
			sql.append("nvarchar(max)");
			return null;
		}

		public Object inText(Appendable sql) throws Throwable {
			sql.append("varchar(max)");
			return null;
		}
	};

	public static final TypeDetector<Object, Appendable> formatter8 = new TypeBase() {

		public Object inBlob(Appendable sql) throws Throwable {
			sql.append("image");
			return null;
		}

		public Object inNText(Appendable sql) throws Throwable {
			sql.append("ntext");
			return null;
		}

		public Object inText(Appendable sql) throws Throwable {
			sql.append("text");
			return null;
		}
	};

	@Override
	public final SqlserverDbSync newDbRefactor(PooledConnection conn,
			ExceptionCatcher catcher) {
		return new SqlserverDbSync(conn, this, catcher);
	}

	public static final void quote(Appendable str, String name) {
		try {
			str.append('[').append(name).append(']');
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public final void quoteId(Appendable str, String name) {
		quote(str, name);
	}

	@Override
	public final ISqlCommandFactory sqlbuffers() {
		return this.sqlbufferFactory;
	}

	private final SQLServerCommandFactory sqlbufferFactory = new SQLServerCommandFactory(this);

	@Override
	public final SpCallSql spCallSqlFor(StoredProcedureDefineImpl procedure) {
		return new SqlserverSpCallSql(this, procedure);
	}

	@Override
	public final boolean supportsRenameColumn() {
		return true;
	}

	@Override
	public final boolean supportsAddNotNullColumnWithoutDefaultOnEmptyTable() {
		return true;
	}

	public static final boolean beforeYukon(DbMetadata metadata) {
		return metadata instanceof SqlserverMetadata && ((SqlserverMetadata) metadata).beforeYukon();
	}

	@Override
	public final SQLExecutionException raise(SQLException e, String sql) {
		// 2601 �����ھ���Ψһ���� 'XX' �Ķ��� 'XX' �в����ظ������С�
		// 2627 Υ���� PRIMARY KEY Լ�� 'PK_XX'�������ڶ��� 'dbo.XX' �в����ظ�����
		final int ec = e.getErrorCode();
		if (ec == 2601 || ec == 2627) {
			return new SQLUniqueConstraintViolationException(e.getMessage(), e, sql);
		}
		return super.raise(e, sql);
	}
}