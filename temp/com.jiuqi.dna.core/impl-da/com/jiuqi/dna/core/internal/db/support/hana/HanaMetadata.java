package com.jiuqi.dna.core.internal.db.support.hana;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.def.model.ModelDefine;
import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.render.HanaSpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.InsertMultipleSql;
import com.jiuqi.dna.core.internal.da.sql.render.InsertSingleSql;
import com.jiuqi.dna.core.internal.da.sql.render.InsertStatementStatusVisitor;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer.HanaCommandFactory;
import com.jiuqi.dna.core.internal.db.support.hana.sync.HanaDbSync;
import com.jiuqi.dna.core.internal.db.sync.DbSync;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.EnumType;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;

public class HanaMetadata extends DbMetadata {

	public HanaMetadata(Connection conn) throws SQLException {
		super(conn, "hana");
		this.schema = System.getProperty("com.jiuqi.dna.hana-set-schema");
		if (this.schema == null || this.schema.length() == 0) {
			throw new IllegalStateException("必须指定Hana的Schema。");
		} else {
			setSchema(conn, this.schema);
			System.out.println("设置Hana模式为：" + this.schema + "。");
		}
	}

	public static final void setSchema(Connection conn, String schema)
			throws SQLException {
		Statement stmt = conn.createStatement();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("set schema ");
			DbProduct.Hana.quote(sql, schema);
			stmt.execute(sql.toString());
		} finally {
			stmt.close();
		}
	}

	public final String schema;

	@Override
	public DbProduct product() {
		return DbProduct.Hana;
	}

	@Override
	public String getCheckConnSql() {
		return "select 1 from dummy";
	}

	private static final String[] MODIFIER = new String[] { "hana" };

	@Override
	public String[] getModifiers() {
		return MODIFIER;
	}

	@Override
	public int getMaxTableNameLength() {
		// HANA 实际是128字节，采用UTF8编码
		return 30;
	}

	@Override
	public int getMaxColumnNameLength() {
		// HANA 实际是128字节，采用UTF8编码
		return 60;
	}

	@Override
	public int getMaxIndexNameLength() {
		// HANA 实际是128字节，采用UTF8编码
		return 60;
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
		// HANA Auto-generated method stub
		return 1000;
	}

	@Override
	public void quoteId(Appendable str, String name) {
		DbProduct.Hana.quote(str, name);
	}

	@Override
	public void format(Appendable str, DataType type) {
		type.detect(formatter, str);
	}

	public static final TypeDetector<Object, Appendable> formatter = new TypeDetector<Object, Appendable>() {

		public Object inBoolean(Appendable str) throws Throwable {
			str.append("tinyint");
			return null;
		}

		public Object inByte(Appendable str) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inShort(Appendable str) throws Throwable {
			str.append("smallint");
			return null;
		}

		public Object inInt(Appendable str) throws Throwable {
			str.append("integer");
			return null;
		}

		public Object inLong(Appendable str) throws Throwable {
			str.append("bigint");
			return null;
		}

		public Object inDate(Appendable str) throws Throwable {
			str.append("seconddate");
			return null;
		}

		public Object inFloat(Appendable str) throws Throwable {
			str.append("double");
			return null;
		}

		public Object inDouble(Appendable str) throws Throwable {
			str.append("double");
			return null;
		}

		public Object inNumeric(Appendable str, int precision, int scale)
				throws Throwable {
			str.append("decimal(");
			str.append(Convert.toString(precision));
			str.append(',');
			str.append(Convert.toString(scale));
			str.append(')');
			return null;
		}

		public Object inString(Appendable str, SequenceDataType type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inObject(Appendable str, ObjectDataType type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inChar(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("nvarchar(");
			str.append(Convert.toString(type.getMaxLength()));
			str.append(')');
			return null;
		}

		public Object inVarChar(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("nvarchar(");
			str.append(Convert.toString(type.getMaxLength()));
			str.append(')');
			return null;
		}

		public Object inText(Appendable str) throws Throwable {
			str.append("nclob");
			return null;
		}

		public Object inNVarChar(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("nvarchar(");
			str.append(Convert.toString(type.getMaxLength() * 2));
			str.append(')');
			return null;
		}

		public Object inNChar(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("nvarchar(");
			str.append(Convert.toString(type.getMaxLength() * 2));
			str.append(')');
			return null;
		}

		public Object inNText(Appendable str) throws Throwable {
			str.append("nclob");
			return null;
		}

		public Object inBytes(Appendable str, SequenceDataType type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inBinary(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("varbinary(");
			str.append(Convert.toString(type.getMaxLength()));
			str.append(")");
			return null;
		}

		public Object inVarBinary(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("varbinary(");
			str.append(Convert.toString(type.getMaxLength()));
			str.append(")");
			return null;
		}

		public Object inBlob(Appendable str) throws Throwable {
			str.append("blob");
			return null;
		}

		public Object inGUID(Appendable str) throws Throwable {
			str.append("varbinary(16)");
			return null;
		}

		public Object inEnum(Appendable str, EnumType<?> type) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inResource(Appendable str, Class<?> facadeClass,
				Object category) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inUnknown(Appendable str) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inStruct(Appendable str, StructDefine type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inModel(Appendable str, ModelDefine type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inQuery(Appendable str, QueryStatementDefine type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inTable(Appendable str) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inRecordSet(Appendable str) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inNull(Appendable str) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inCharacter(Appendable userData) throws Throwable {
			throw new UnsupportedOperationException();
		}
	};

	@Override
	public DbSync newDbRefactor(PooledConnection conn, ExceptionCatcher catcher) {
		return new HanaDbSync(conn, this, catcher);
	}

	@Override
	public ISqlCommandFactory sqlbuffers() {
		return HanaCommandFactory.INSTANCE;
	}

	@Override
	public SpCallSql spCallSqlFor(StoredProcedureDefineImpl procedure) {
		return new HanaSpCallSql(this, procedure);
	}

	@Override
	public boolean supportsRenameColumn() {
		return true;
	}

	@Override
	public boolean supportsAddNotNullColumnWithoutDefaultOnEmptyTable() {
		// HANA Auto-generated method stub
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
	public void init(Connection conn) throws SQLException {
		setSchema(conn, this.schema);
	}
}