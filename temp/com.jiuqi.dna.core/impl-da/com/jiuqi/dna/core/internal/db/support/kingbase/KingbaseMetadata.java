package com.jiuqi.dna.core.internal.db.support.kingbase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.DeprecatedSimpleDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.DeprecatedSimpleInsertSql;
import com.jiuqi.dna.core.internal.da.sql.render.DeprecatedSimpleUpdateSql;
import com.jiuqi.dna.core.internal.da.sql.render.KingbaseMultiDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.KingbaseMultiInsertSql;
import com.jiuqi.dna.core.internal.da.sql.render.KingbaseMultiUpdateSql;
import com.jiuqi.dna.core.internal.da.sql.render.KingbaseRowMultiDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.KingbaseRowMultiInsertSql;
import com.jiuqi.dna.core.internal.da.sql.render.KingbaseRowMultiUpdateSql;
import com.jiuqi.dna.core.internal.da.sql.render.KingbaseSpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.da.sql.render.RowSimpleDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.RowSimpleInsertSql;
import com.jiuqi.dna.core.internal.da.sql.render.RowSimpleUpdateSql;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.db.common.Connections;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer.KingbaseCommandFactory;
import com.jiuqi.dna.core.internal.db.support.kingbase.sync.KingbaseDbRefactor;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public class KingbaseMetadata extends DbMetadata {

	final String databaseCharset;

	// private static final String SELECT_DATABASE_CHARSET =
	// "SELECT SYS_ENCODING_TO_CHAR(ENCODING) FROM SYS_DATABASE WHERE DATNAME='TDB'";

	final String nationalCharset;

	// private static final String SELECT_NATIONAL_CHARSET =
	// "SELECT SYS_ENCODING_TO_CHAR(ENCODING) FROM SYS_DATABASE WHERE DATNAME='TDB'";

	public KingbaseMetadata(Connection conn) throws SQLException {
		super(conn, "kingbase");
		// String namespace = conn.getCatalog();
		final String nameSpace = new StringBuffer().append("SELECT SYS_ENCODING_TO_CHAR(ENCODING) FROM SYS_DATABASE WHERE DATNAME='").append(conn.getCatalog().toUpperCase()).append("'").toString();
		this.user = Connections.stringOf(conn, SELECT_USER);
		this.databaseCharset = Connections.stringOf(conn, nameSpace);
		this.nationalCharset = Connections.stringOf(conn, nameSpace);
	}

	@Override
	public final DbProduct product() {
		return DbProduct.Unknown;
	}

	@Override
	public String[] getModifiers() {
		return modifiers;
	}

	private static final String[] modifiers = new String[] { "kingbase" };

	static final String CHECK_CONN = "select 1 from dual";

	@Override
	public final String getCheckConnSql() {
		return CHECK_CONN;
	}

	final String user;

	private static final String SELECT_USER = "select user from dual";

	@Override
	public final int getMaxColumnNameLength() {
		return 30;
	}

	@Override
	public final int getMaxIndexNameLength() {
		return 30;
	}

	@Override
	public final int getMaxTableNameLength() {
		return 30;
	}

	@Override
	public final int getMaxTablePartCount() {
		return 1048575;
	}

	@Override
	public final int getDefaultPartSuggestion() {
		return 65536;
	}

	@Override
	public final int getMaxColumnsInSelect() {
		return 1000;
	}

	@Override
	public final void format(Appendable str, DataType type) {
		type.detect(formatter, str);
	}

	// HCL
	public static final TypeDetectorBase<Object, Appendable> formatter = new TypeDetectorBase<Object, Appendable>() {

		@Override
		public Object inBoolean(Appendable sql) throws Throwable {
			sql.append("int");
			return null;
		}

		@Override
		public Object inShort(Appendable sql) throws Throwable {
			sql.append("int");
			return null;
		}

		@Override
		public Object inInt(Appendable sql) throws Throwable {
			sql.append("int");
			return null;
		}

		@Override
		public Object inLong(Appendable sql) throws Throwable {
			sql.append("bigint");
			return null;
		}

		@Override
		public Object inFloat(Appendable sql) throws Throwable {
			sql.append("float(24)");
			return null;
		}

		@Override
		public Object inDouble(Appendable sql) throws Throwable {
			sql.append("float(53)");
			return null;
		}

		@Override
		public Object inNumeric(Appendable sql, int precision, int scale)
				throws Throwable {
			sql.append("number(");
			sql.append(Convert.toString(precision));
			sql.append(',');
			sql.append(Convert.toString(scale));
			sql.append(')');
			return null;
		}

		@Override
		public Object inChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("char(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(" char)");
			return null;
		}

		@Override
		public Object inVarChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("varchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(" char)");
			return null;
		}

		@Override
		public Object inText(Appendable sql) throws Throwable {
			sql.append("text");
			return null;
		}

		@Override
		public Object inNChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("char(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(" char)");
			return null;
		}

		@Override
		public Object inNVarChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("varchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(" char)");
			return null;
		}

		@Override
		public Object inNText(Appendable sql) throws Throwable {
			sql.append("clob");
			return null;
		}

		@Override
		public Object inBinary(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("bytea");
			return null;
		}

		@Override
		public Object inVarBinary(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("bytea");
			return null;
		}

		@Override
		public Object inBlob(Appendable sql) throws Throwable {
			sql.append("blob");
			return null;
		}

		@Override
		public Object inDate(Appendable sql) throws Throwable {
			sql.append("timestamp(3)");
			return null;
		}

		@Override
		public Object inGUID(Appendable sql) throws Throwable {
			sql.append("bytea");
			return null;
		}
	};

	@Override
	public final KingbaseDbRefactor newDbRefactor(PooledConnection conn,
			ExceptionCatcher catcher) {
		return new KingbaseDbRefactor(conn, this, catcher);
	}

	public static final void quote(Appendable str, String name) {
		try {
			str.append('"').append(name).append('"');
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
		return KingbaseCommandFactory.INSTANCE;
	}

	@Override
	public SpCallSql spCallSqlFor(StoredProcedureDefineImpl procedure) {
		return new KingbaseSpCallSql(this, procedure);
	}

	@Override
	public ModifySql insertSqlFor(InsertStatementImpl insert) {
		final int tbCount = insert.moTableRef.getTarget().getDBTables().size();
		if (tbCount == 1) {
			return new DeprecatedSimpleInsertSql(this, insert);
		}
		return new KingbaseMultiInsertSql(this, insert);
	}

	@Override
	public ModifySql deleteSqlFor(DeleteStatementImpl delete) {
		final int tbCount = delete.moTableRef.getTarget().getDBTables().size();
		if (tbCount == 1) {
			return new DeprecatedSimpleDeleteSql(this, delete);
		}
		return new KingbaseMultiDeleteSql(this, delete);
	}

	@Override
	public ModifySql updateSqlFor(UpdateStatementImpl update) {
		final int tbCount = update.moTableRef.getTarget().getDBTables().size();
		if (tbCount == 1) {
			return new DeprecatedSimpleUpdateSql(this, update);
		}
		return new KingbaseMultiUpdateSql(this, update);
	}

	@Override
	public ModifySql getRowInsertSql(QueryStatementBase queryStatment) {
		QuTableRef qr = (QuTableRef) queryStatment.rootRelationRef();
		if (qr.getTarget().dbTables.size() > 1) {
			return new KingbaseRowMultiInsertSql(this, queryStatment);
		} else {
			return new RowSimpleInsertSql(this, queryStatment);
		}
	}

	@Override
	public ModifySql getRowDeleteSql(QueryStatementBase queryStatment) {
		QuTableRef qr = (QuTableRef) queryStatment.rootRelationRef();
		if (qr.getTarget().dbTables.size() > 1) {
			return new KingbaseRowMultiDeleteSql(this, queryStatment);
		} else {
			return new RowSimpleDeleteSql(this, queryStatment);
		}
	}

	@Override
	public ModifySql getRowUpdateSql(QueryStatementBase queryStatment) {
		QuTableRef qr = (QuTableRef) queryStatment.rootRelationRef();
		if (qr.getTarget().dbTables.size() > 1) {
			return new KingbaseRowMultiUpdateSql(this, queryStatment);
		} else {
			return new RowSimpleUpdateSql(this, queryStatment);
		}
	}

	@Override
	public final boolean supportsRenameColumn() {
		return true;
	}

	@Override
	public final boolean supportsAddNotNullColumnWithoutDefaultOnEmptyTable() {
		return true;
	}

}
