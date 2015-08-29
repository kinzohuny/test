package com.jiuqi.dna.core.internal.db.support.db2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.da.SQLExecutionException;
import com.jiuqi.dna.core.da.SQLUniqueConstraintViolationException;
import com.jiuqi.dna.core.def.model.ModelDefine;
import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.impl.IllegalStatementDefineException;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.DB2UpdateMultipleSql;
import com.jiuqi.dna.core.internal.da.sql.render.Db2SpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateMultipleResolver;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateSingleSql;
import com.jiuqi.dna.core.internal.da.sql.render.UpdateStatementStatusVisitor;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.db.common.Connections;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer.DB2CommandFactory;
import com.jiuqi.dna.core.internal.db.support.db2.sync.Db2DbSync;
import com.jiuqi.dna.core.internal.db.sync.DbSync;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.EnumType;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;

public class Db2Metadata extends DbMetadata {

	public Db2Metadata(Connection conn) throws SQLException {
		super(conn, "db2");
		this.user = Connections.stringOf(conn, SELECT_USER);
		this.database = Connections.stringOf(conn, SELECT_DATABASE);
	}

	@Override
	public final DbProduct product() {
		return DbProduct.DB2;
	}

	@Override
	public String[] getModifiers() {
		return modifiers;
	}

	private static final String[] modifiers = new String[] { "db2" };

	static final String CHECK_CONN = "select 1 from sysibm.dual";

	@Override
	public final String getCheckConnSql() {
		return CHECK_CONN;
	}

	public final String user;

	private static final String SELECT_USER = "select current user from sysibm.dual";

	public final String database;

	private static final String SELECT_DATABASE = "select current server from sysibm.dual";

	@Override
	public final int getMaxTableNameLength() {
		return 128;
	}

	@Override
	public final int getMaxColumnNameLength() {
		return 128;
	}

	@Override
	public final int getMaxIndexNameLength() {
		return 128;
	}

	@Override
	public final int getMaxTablePartCount() {
		// HCL Auto-generated method stub
		return 0;
	}

	@Override
	public final int getDefaultPartSuggestion() {
		// HCL Auto-generated method stub
		return 0;
	}

	@Override
	public final int getMaxColumnsInSelect() {
		return 1012;
	}

	@Override
	public final DbSync newDbRefactor(PooledConnection conn,
			ExceptionCatcher catcher) {
		return new Db2DbSync(conn, this, catcher);
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
	public final void format(Appendable str, DataType type) {
		type.detect(formatter, str);
	}

	public static final TypeDetector<Object, Appendable> formatter = new TypeDetector<Object, Appendable>() {

		public Object inBoolean(Appendable str) throws Throwable {
			str.append("smallint");
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
			str.append("timestamp");
			return null;
		}

		public Object inFloat(Appendable str) throws Throwable {
			str.append("real");
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
			str.append("varchar(");
			str.append(Convert.toString(type.getMaxLength()));
			str.append(')');
			return null;
		}

		public Object inVarChar(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("varchar(");
			str.append(Convert.toString(type.getMaxLength()));
			str.append(')');
			return null;
		}

		public Object inText(Appendable str) throws Throwable {
			str.append("clob(2g) not logged");
			return null;
		}

		public Object inNVarChar(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("varchar(");
			str.append(Convert.toString(type.getMaxLength() * 2));
			str.append(')');
			return null;
		}

		public Object inNChar(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("varchar(");
			str.append(Convert.toString(type.getMaxLength() * 2));
			str.append(')');
			return null;
		}

		public Object inNText(Appendable str) throws Throwable {
			str.append("clob(2g) not logged");
			return null;
		}

		public Object inBytes(Appendable str, SequenceDataType type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inBinary(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("varchar(");
			str.append(Convert.toString(type.getMaxLength()));
			str.append(") for bit data");
			return null;
		}

		public Object inVarBinary(Appendable str, SequenceDataType type)
				throws Throwable {
			str.append("varchar(");
			str.append(Convert.toString(type.getMaxLength()));
			str.append(") for bit data");
			return null;
		}

		public Object inBlob(Appendable str) throws Throwable {
			str.append("blob(2g) not logged");
			return null;
		}

		public Object inGUID(Appendable str) throws Throwable {
			str.append("char(16) for bit data");
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

	static final void format(Appendable str, byte[] value) {
		try {
			str.append("x\'");
			str.append(Convert.bytesToHex(value, false, false));
			str.append("\')");
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public final ISqlCommandFactory sqlbuffers() {
		return DB2CommandFactory.INSTANCE;
	}

	@Override
	public final SpCallSql spCallSqlFor(StoredProcedureDefineImpl procedure) {
		return new Db2SpCallSql(this, procedure);
	}

	@Override
	public final boolean supportsRenameColumn() {
		return false;
	}

	@Override
	public final boolean supportsAddNotNullColumnWithoutDefaultOnEmptyTable() {
		return false;
	}

	private static final String errmc(String m) {
		final String t = "SQLERRMC=";
		int begin = m.indexOf(t);
		if (begin < 0) {
			return null;
		}
		int last = m.indexOf(",", begin);
		return m.substring(begin + t.length(), last);
	}

	@Override
	public SQLExecutionException raise(SQLException e, String sql) {
		try {
			switch (e.getErrorCode()) {
			case -911: {
				String m = e.getMessage();
				String errmc = errmc(m);
				String cause = "";
				if (errmc.equals("2")) {
					cause = "由于死锁而导致事务已回滚。";
				} else if (errmc.equals("68")) {
					cause = "由于锁定超时而导致事务已回滚。";
				} else if (errmc.equals("72")) {
					cause = "因为存在于事务中所涉及的DB2 Data Links Manager有关的错误，所以事务已回滚。";
				}
				return new SQLExecutionException("因为死锁或超时，所以当前事务已回滚。原因码为：" + cause, e, sql);
			}
			case -803: {
				String m = e.getMessage();
				int last = m.lastIndexOf(';') + 1;
				String table = last < m.length() ? m.substring(last) : "";
				return new SQLUniqueConstraintViolationException(String.format("INSERT语句、UPDATE语句或由DELETE语句导致的外键更新中的一个或多个值无效，主键、唯一约束或者唯一索引将表“%s”的索引键限制为不能具有重复值。", table), e, sql);
			}
			case -614: {
				String m = e.getMessage();
				int last = m.lastIndexOf(':') + 2;
				String index = last < m.length() ? m.substring(last) : "";
				return new SQLExecutionException(String.format("不能创建或改变索引或索引扩展名“%s”，因为指定列的组合长度太长。", index), e, sql);
			}
			case -286:
				return new SQLExecutionException("缺省表空间的页大小不足。", e, sql);
			case -138:
				return new SQLExecutionException("内置字符串函数（substr等）的数字自变量超出了范围。", e, sql);
			}
		} catch (Throwable th) {
		}
		return super.raise(e, sql);
	}
	
	@Override
	public ModifySql updateSqlFor(UpdateStatementImpl update) {
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
				return new DB2UpdateMultipleSql(this, update, visitor, resolver);
			}
		}
	}
}