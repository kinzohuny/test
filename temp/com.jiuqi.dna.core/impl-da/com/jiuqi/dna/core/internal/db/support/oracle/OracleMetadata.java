package com.jiuqi.dna.core.internal.db.support.oracle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.da.SQLExecutionException;
import com.jiuqi.dna.core.da.SQLUniqueConstraintViolationException;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.IllegalStatementDefineException;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.DeleteMultipleSql;
import com.jiuqi.dna.core.internal.da.sql.render.DeleteSingleSql;
import com.jiuqi.dna.core.internal.da.sql.render.DeleteStatementStatusVisitor;
import com.jiuqi.dna.core.internal.da.sql.render.InsertMultipleSql;
import com.jiuqi.dna.core.internal.da.sql.render.InsertSingleSql;
import com.jiuqi.dna.core.internal.da.sql.render.InsertStatementStatusVisitor;
import com.jiuqi.dna.core.internal.da.sql.render.InsertUsingCursorSql;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.da.sql.render.OracleSpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateMultipleResolver;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateMultipleSql;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateSingleSql;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateStatementStatusVisitor;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.db.common.Connections;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer.OracleCommandFactory;
import com.jiuqi.dna.core.internal.db.support.oracle.sync.OracleDbSync;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public class OracleMetadata extends DbMetadata {

	public OracleMetadata(Connection conn) throws SQLException {
		super(conn, "oracle");
		this.user = Connections.stringOf(conn, SELECT_USER);
		this.databaseCharsetName = Connections.stringOf(conn, SELECT_DATABASE_CHARSET);
		this.nationalCharsetName = Connections.stringOf(conn, SELECT_NATIONAL_CHARSET);
	}

	@Override
	public final DbProduct product() {
		return DbProduct.Oracle;
	}

	@Override
	public String[] getModifiers() {
		switch (this.dbMajorVer) {
		case 9:
			return MODIFIER_9;
		case 10:
			return MODIFIER_10;
		case 11:
			return MODIFIER_11;
		}
		return MODIFIER_DEFAULT;
	}

	private static final String[] MODIFIER_DEFAULT = new String[] { "oracle" };
	private static final String[] MODIFIER_9 = new String[] { "oracle9", "oracle" };
	private static final String[] MODIFIER_10 = new String[] { "oracle10", "oracle" };
	private static final String[] MODIFIER_11 = new String[] { "oracle11", "oracle" };

	static final String CHECK_CONN = "select 1 from dual";

	@Override
	public final String getCheckConnSql() {
		return CHECK_CONN;
	}

	public final String user;

	private static final String SELECT_USER = "select user from dual";

	public final String databaseCharsetName;

	private static final String SELECT_DATABASE_CHARSET = "select value from nls_database_parameters where parameter = 'NLS_CHARACTERSET'";

	public final String nationalCharsetName;

	private static final String SELECT_NATIONAL_CHARSET = "select value from nls_database_parameters where parameter = 'NLS_NCHAR_CHARACTERSET'";

	// select * from v$nls_valid_values

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

	public static final TypeDetectorBase<Object, Appendable> formatter = new TypeDetectorBase<Object, Appendable>() {

		@Override
		public Object inBoolean(Appendable sql) throws Throwable {
			sql.append("number(1)");
			return null;
		}

		@Override
		public Object inShort(Appendable sql) throws Throwable {
			sql.append("number(5)");
			return null;
		}

		@Override
		public Object inInt(Appendable sql) throws Throwable {
			sql.append("number(10)");
			return null;
		}

		@Override
		public Object inLong(Appendable sql) throws Throwable {
			sql.append("number(19)");
			return null;
		}

		@Override
		public Object inFloat(Appendable sql) throws Throwable {
			sql.append("real");
			return null;
		}

		@Override
		public Object inDouble(Appendable sql) throws Throwable {
			sql.append("float");
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
			sql.append(')');
			return null;
		}

		@Override
		public Object inVarChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("varchar2(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		@Override
		public Object inText(Appendable sql) throws Throwable {
			sql.append("clob");
			return null;
		}

		@Override
		public Object inNChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("nchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		@Override
		public Object inNVarChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("nvarchar2(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		@Override
		public Object inNText(Appendable sql) throws Throwable {
			sql.append("nclob");
			return null;
		}

		@Override
		public Object inBinary(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("raw(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(")");
			return null;
		}

		@Override
		public Object inVarBinary(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("raw(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(")");
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
			sql.append("raw(16)");
			return null;
		}
	};

	@Override
	public final OracleDbSync newDbRefactor(PooledConnection conn,
			ExceptionCatcher catcher) {
		return new OracleDbSync(conn, this, catcher);
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
		return OracleCommandFactory.INSTANCE;
	}

	@Override
	public SpCallSql spCallSqlFor(StoredProcedureDefineImpl procedure) {
		return new OracleSpCallSql(this, procedure);
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
	public final ModifySql insertSqlFor(InsertStatementImpl insert) {
		insert.checkValid();
		final InsertStatementStatusVisitor visitor = new InsertStatementStatusVisitor();
		insert.visit(visitor, null);
		final int tbCount = insert.moTableRef.target.dbTables.size();
		if (tbCount == 1) {
			return new InsertSingleSql(this, insert, visitor);
		} else if (visitor.isValuesNonDeterministic()) {
			return new InsertUsingCursorSql(this, insert, visitor);
		} else {
			return new InsertMultipleSql(this, insert, visitor);
		}
	}

	@Override
	public final ModifySql deleteSqlFor(DeleteStatementImpl delete) {
		final DeleteStatementStatusVisitor visitor = new DeleteStatementStatusVisitor(delete);
		delete.visit(visitor, null);
		final int tbCount = delete.moTableRef.target.dbTables.size();
		if (tbCount == 1) {
			return new DeleteSingleSql(this, delete, visitor);
		} else {
			if (visitor.conditionSource.tableCount() > 1) {
				throw new UnsupportedOperationException();
				// this.deleteUsingCursor(dbMetadata, delete, visitor);
			} else {
				return new DeleteMultipleSql(this, delete, visitor);
			}
		}
	}

	@Override
	public final ModifySql updateSqlFor(UpdateStatementImpl update) {
		if (update.assigns.size() == 0) {
			throw new IllegalStatementDefineException(update, "更新语句定义[" + update.name + "]未定义任何更新列。");
		}
		final UpdateStatementStatusVisitor visitor = new UpdateStatementStatusVisitor(update);
		if (update.moTableRef.target.dbTables.size() == 1) {
			return new UpdateSingleSql(this, update, visitor);
		} else {
			UpdateMultipleResolver resolver = new UpdateMultipleResolver(update);
			if (resolver.dbTables.size() == 1) {
				return new UpdateSingleSql(this, update, visitor, resolver.dbTables.get(0));
			} else {
				return new UpdateMultipleSql(this, update, visitor, resolver);
			}
		}
	}

	@Override
	public SQLExecutionException raise(SQLException e, String sql) {
		if (e.getErrorCode() == 1) {
			return new SQLUniqueConstraintViolationException(e.getMessage(), e, sql);
		}
		return super.raise(e, sql);
	}

	@Override
	public boolean supportesBitmapIndex() {
		return true;
	}
}