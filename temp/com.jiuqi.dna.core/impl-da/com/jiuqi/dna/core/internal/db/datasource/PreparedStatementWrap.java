package com.jiuqi.dna.core.internal.db.datasource;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import com.jiuqi.dna.core.impl.ContextVariableIntl;
import com.jiuqi.dna.core.type.Convert;

/**
 * JDBC的PreparedStatement接口的包装
 * 
 * @author houchunlei
 * 
 */
public class PreparedStatementWrap extends StatementWrap {

	public final String sql;
	public final SqlSource source;

	PreparedStatementWrap(PooledConnection conn, String sql, SqlSource source,
			boolean eager) throws SQLException {
		super(conn, false);
		this.sql = sql;
		this.source = source;
		if (eager) {
			this.ensure();
		}
		this.logSetValue = SqlConsolePrinter.printSql(source) && ContextVariableIntl.isDebugSqlParam();
	}

	PreparedStatement pstmt;

	@Override
	void ensure() throws SQLException {
		if (this.pstmt == null) {
			this.stmt = this.pstmt = this.conn.jdbcPrepareStatement(this.cursorId, this.sql);
		}
	}

	private final boolean logSetValue;

	private StringBuilder setValueLogger;

	private final void logSetValue(int jdbcIndex, String type, String value) {
		if (this.setValueLogger == null) {
			this.setValueLogger = new StringBuilder();
		}
		this.setValueLogger.append("index[");
		this.setValueLogger.append(jdbcIndex);
		this.setValueLogger.append("],type[");
		this.setValueLogger.append(type);
		this.setValueLogger.append("],value:");
		this.setValueLogger.append(value == null ? "(null)" : value);
		this.setValueLogger.append("\r\n");
	}

	@Override
	public void close() throws SQLException {
		super.close();
		this.pstmt = null;
	}

	@Override
	public final boolean execute(String sql, SqlSource source)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final int executeUpdate(String sql, SqlSource source)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final ResultSet executeQuery(String sql, SqlSource source)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	public final boolean execute() throws SQLException {
		this.ensure();
		SQLException th = null;
		this.conn.enter();
		this.listenStartExecute(this.sql, this.source);
		final long start = System.currentTimeMillis();
		try {
			return this.pstmt.execute();
		} catch (SQLException e) {
			th = e;
			throw this.raise(e, this.sql);
		} finally {
			this.listenFinishExecute(this.sql, this.source, start, th);
			this.printParamValue();
			this.conn.leave(th);
		}
	}

	public final int executeUpdate() throws SQLException {
		this.ensure();
		SQLException th = null;
		this.conn.enter();
		this.listenStartExecute(this.sql, this.source);
		final long start = System.currentTimeMillis();
		try {
			return this.pstmt.executeUpdate();
		} catch (SQLException e) {
			th = e;
			throw this.raise(e, this.sql);
		} finally {
			this.listenFinishExecute(this.sql, this.source, start, th);
			this.printParamValue();
			this.conn.leave(th);
		}
	}

	public final ResultSet executeQuery() throws SQLException {
		this.ensure();
		SQLException th = null;
		this.conn.enter();
		this.listenStartExecute(this.sql, this.source);
		final long start = System.currentTimeMillis();
		try {
			return this.pstmt.executeQuery();
		} catch (SQLException e) {
			th = e;
			throw this.raise(e, this.sql);
		} finally {
			this.listenFinishExecute(this.sql, this.source, start, th);
			this.printParamValue();
			this.conn.leave(th);
		}
	}

	// TODO remove
	private final void printParamValue() {
		if (this.setValueLogger != null) {
			System.out.println(this.setValueLogger.toString());
			this.setValueLogger = null;
		}
	}

	public final boolean exist() throws SQLException {
		final ResultSet rs = this.executeQuery();
		try {
			if (rs.next()) {
				return true;
			}
			return false;
		} finally {
			rs.close();
		}
	}

	static final HashMap<Integer, String> jdbcTypeName = new HashMap<Integer, String>();
	static {
		Field[] fields = Types.class.getDeclaredFields();
		for (Field f : fields) {
			try {
				jdbcTypeName.put(f.getInt(null), f.getName());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static final String jdbcTypeName(int sqlType) {
		return jdbcTypeName.get(sqlType);
	}

	public final void setNull(int parameterIndex, int sqlType)
			throws SQLException {
		this.ensure();
		this.pstmt.setNull(parameterIndex, sqlType);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, jdbcTypeName(sqlType), null);
		}
	}

	public final void setBoolean(int parameterIndex, boolean x)
			throws SQLException {
		this.ensure();
		this.pstmt.setBoolean(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "boolean", Boolean.toString(x));
		}
	}

	public final void setByte(int parameterIndex, byte x) throws SQLException {
		this.ensure();
		this.pstmt.setByte(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "byte", Byte.toString(x));
		}
	}

	public final void setShort(int parameterIndex, short x) throws SQLException {
		this.ensure();
		this.pstmt.setShort(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "short", Short.toString(x));
		}
	}

	public final void setInt(int parameterIndex, int x) throws SQLException {
		this.ensure();
		this.pstmt.setInt(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "int", Integer.toString(x));
		}
	}

	public final void setLong(int parameterIndex, long x) throws SQLException {
		this.ensure();
		this.pstmt.setLong(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "long", Long.toString(x));
		}
	}

	public final void setFloat(int parameterIndex, float x) throws SQLException {
		this.ensure();
		this.pstmt.setFloat(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "float", Float.toString(x));
		}
	}

	public final void setDouble(int parameterIndex, double x)
			throws SQLException {
		this.ensure();
		this.pstmt.setDouble(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "double", Double.toString(x));
		}
	}

	public final void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		this.ensure();
		this.pstmt.setBigDecimal(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "bigdecimal", x.toString());
		}
	}

	public final void setString(int parameterIndex, String x)
			throws SQLException {
		this.ensure();
		this.pstmt.setString(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "string", x);
		}
	}

	public final void setBytes(int parameterIndex, byte x[])
			throws SQLException {
		this.ensure();
		this.pstmt.setBytes(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "bytes", Convert.bytesToHex(x, true, true));
		}
	}

	public final void setDate(int parameterIndex, java.sql.Date x)
			throws SQLException {
		this.ensure();
		this.pstmt.setDate(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "date", x.toString());
		}
	}

	public final void setTimestamp(int parameterIndex, java.sql.Timestamp x)
			throws SQLException {
		this.ensure();
		this.pstmt.setTimestamp(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "timestamp", x.toString());
		}
	}

	public final void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		this.ensure();
		this.pstmt.setObject(parameterIndex, x, targetSqlType);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "object of " + jdbcTypeName(targetSqlType), x.toString());
		}
	}

	public final void setObject(int parameterIndex, Object x)
			throws SQLException {
		this.ensure();
		this.pstmt.setObject(parameterIndex, x);
		if (this.logSetValue) {
			this.logSetValue(parameterIndex, "object", x.toString());
		}
	}

	public final void clearParameters() throws SQLException {
		this.ensure();
		this.pstmt.clearParameters();
	}

	public final void addBatch() throws SQLException {
		this.ensure();
		this.pstmt.addBatch();
	}

	public final int[] executeBatch() throws SQLException {
		this.ensure();
		return this.pstmt.executeBatch();
	}

	public final ResultSet getResultSet() throws SQLException {
		return this.pstmt.getResultSet();
	}

	public final boolean getMoreResults() throws SQLException {
		return this.pstmt.getMoreResults();
	}
}