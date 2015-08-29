package com.jiuqi.dna.core.internal.db.support.dm;

import java.sql.Connection;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.IllegalStatementDefineException;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.internal.da.sql.render.DMSpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.DeleteMultipleSql;
import com.jiuqi.dna.core.internal.da.sql.render.DeleteSingleSql;
import com.jiuqi.dna.core.internal.da.sql.render.DeleteStatementStatusVisitor;
import com.jiuqi.dna.core.internal.da.sql.render.InsertMultipleSql;
import com.jiuqi.dna.core.internal.da.sql.render.InsertSingleSql;
import com.jiuqi.dna.core.internal.da.sql.render.InsertStatementStatusVisitor;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateMultipleResolver;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateMultipleSql;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateSingleSql;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateStatementStatusVisitor;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer.DmCommandFactory;
import com.jiuqi.dna.core.internal.db.support.dm.sync.DmDbSync;
import com.jiuqi.dna.core.internal.db.sync.DbSync;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public class DmMetadata extends DbMetadata {

	public DmMetadata(Connection conn) throws SQLException {
		super(conn, "dm");
	}

	@Override
	public String getCheckConnSql() {
		return "select 1 from dual";
	}

	@Override
	public String[] getModifiers() {
		return modifier;
	}

	private static final String[] modifier = new String[] { "dm" };

	@Override
	public final DbProduct product() {
		return DbProduct.Dameng;
	}

	@Override
	public int getMaxTableNameLength() {
		return 128;
	}

	@Override
	public int getMaxColumnNameLength() {
		return 128;
	}

	@Override
	public int getMaxIndexNameLength() {
		return 128;
	}

	@Override
	public int getMaxTablePartCount() {
		return 0;
	}

	@Override
	public int getDefaultPartSuggestion() {
		return 0;
	}

	@Override
	public int getMaxColumnsInSelect() {
		return 1024;
	}

	@Override
	public void quoteId(Appendable str, String name) {
		DbProduct.Dameng.quote(str, name);
	}

	@Override
	public void format(Appendable str, DataType type) {
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
			sql.append("numeric(");
			sql.append(Convert.toString(precision));
			sql.append(',');
			sql.append(Convert.toString(scale));
			sql.append(')');
			return null;
		}

		@Override
		public final Object inFloat(Appendable sql) throws Throwable {
			sql.append("real");
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
			sql.append("nvarchar(");
			sql.append(Convert.toString(type.getMaxLength()));
			sql.append(')');
			return null;
		}

		@Override
		public final Object inText(Appendable sql) throws Throwable {
			sql.append("clob");
			return null;
		}

		@Override
		public final Object inNChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("nvarchar(");
			sql.append(Convert.toString(type.getMaxLength() * 2));
			sql.append(')');
			return null;
		}

		@Override
		public final Object inNVarChar(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("nvarchar(");
			sql.append(Convert.toString(type.getMaxLength() * 2));
			sql.append(')');
			return null;
		}

		@Override
		public final Object inNText(Appendable sql) throws Throwable {
			sql.append("clob");
			return null;
		}

		@Override
		public final Boolean inBinary(Appendable sql, SequenceDataType type)
				throws Throwable {
			sql.append("varbinary(");
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
			sql.append("blob");
			return null;
		}

		@Override
		public final Object inDate(Appendable sql) throws Throwable {
			sql.append("timestamp(0)");
			return null;
		}

		@Override
		public final Object inGUID(Appendable sql) throws Throwable {
			sql.append("binary(16)");
			return null;
		}
	};

	@Override
	public DbSync newDbRefactor(PooledConnection conn, ExceptionCatcher catcher) {
		return new DmDbSync(conn, this, catcher);
	}

	@Override
	public ISqlCommandFactory sqlbuffers() {
		return DmCommandFactory.INSTANCE;
	}

	@Override
	public SpCallSql spCallSqlFor(StoredProcedureDefineImpl procedure) {
		return new DMSpCallSql(this, procedure);
	}

	@Override
	public boolean supportsRenameColumn() {
		return true;
	}

	@Override
	public boolean supportsAddNotNullColumnWithoutDefaultOnEmptyTable() {
		return true;
	}

	@Override
	public ModifySql insertSqlFor(InsertStatementImpl insert) {
		insert.checkValid();
		final InsertStatementStatusVisitor visitor = new InsertStatementStatusVisitor();
		insert.visit(visitor, null);
		final int tbCount = insert.moTableRef.target.dbTables.size();
		if (tbCount == 1) {
			return new InsertSingleSql(this, insert, visitor);
		} else if (visitor.isValuesNonDeterministic()) {
			throw new UnsupportedOperationException();
		} else {
			return new InsertMultipleSql(this, insert, visitor);
		}
	}

	@Override
	public ModifySql updateSqlFor(UpdateStatementImpl update) {
		if (update.assigns.size() == 0) {
			throw new IllegalStatementDefineException(update, "更新语句定义["
					+ update.name + "]未定义任何更新列。");
		}
		final UpdateStatementStatusVisitor visitor = new UpdateStatementStatusVisitor(
				update);
		if (update.moTableRef.target.dbTables.size() == 1) {
			return new UpdateSingleSql(this, update, visitor);
		} else {
			UpdateMultipleResolver resolver = new UpdateMultipleResolver(update);
			if (resolver.dbTables.size() == 1) {
				return new UpdateSingleSql(this, update, visitor,
						resolver.dbTables.get(0));
			} else {
				return new UpdateMultipleSql(this, update, visitor, resolver);
			}
		}
	}

	@Override
	public final ModifySql deleteSqlFor(DeleteStatementImpl delete) {
		final DeleteStatementStatusVisitor visitor = new DeleteStatementStatusVisitor(
				delete);
		delete.visit(visitor, null);
		final int tbCount = delete.moTableRef.target.dbTables.size();
		if (tbCount == 1) {
			return new DeleteSingleSql(this, delete, visitor);
		} else {
			if (visitor.conditionSource.tableCount() > 1) {
				throw new UnsupportedOperationException();
			} else {
				return new DeleteMultipleSql(this, delete, visitor);
			}
		}
	}

}