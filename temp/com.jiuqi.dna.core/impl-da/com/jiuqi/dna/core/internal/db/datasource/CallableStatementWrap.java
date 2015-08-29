package com.jiuqi.dna.core.internal.db.datasource;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * JDBC的CallableStatement接口的包装
 * 
 * @author houchunlei
 * 
 */
public final class CallableStatementWrap extends PreparedStatementWrap {

	CallableStatementWrap(PooledConnection conn, String sql, SqlSource source,
			boolean eager) throws SQLException {
		super(conn, sql, source, false);
		if (eager) {
			this.ensure();
		}
	}

	CallableStatement cstmt;

	@Override
	final void ensure() throws SQLException {
		if (this.cstmt == null) {
			this.stmt = this.pstmt = this.cstmt = this.conn.jdbcPrepareCall(this.cursorId, this.sql);
		}
	}

	@Override
	public final void close() throws SQLException {
		super.close();
		this.cstmt = null;
	}

	public final boolean wasNull() throws SQLException {
		return this.cstmt.wasNull();
	}

	public final boolean getBoolean(int parameterIndex) throws SQLException {
		return this.cstmt.getBoolean(parameterIndex);
	}

	public final byte getByte(int parameterIndex) throws SQLException {
		return this.cstmt.getByte(parameterIndex);
	}

	public final short getShort(int parameterIndex) throws SQLException {
		return this.cstmt.getShort(parameterIndex);
	}

	public final int getInt(int parameterIndex) throws SQLException {
		return this.cstmt.getInt(parameterIndex);
	}

	public final long getLong(int parameterIndex) throws SQLException {
		return this.cstmt.getLong(parameterIndex);
	}

	public final float getFloat(int parameterIndex) throws SQLException {
		return this.cstmt.getFloat(parameterIndex);
	}

	public final double getDouble(int parameterIndex) throws SQLException {
		return this.cstmt.getDouble(parameterIndex);
	}

	public final String getString(int parameterIndex) throws SQLException {
		return this.cstmt.getString(parameterIndex);
	}

	public final byte[] getBytes(int parameterIndex) throws SQLException {
		return this.cstmt.getBytes(parameterIndex);
	}

	public final Date getDate(int parameterIndex) throws SQLException {
		return this.cstmt.getDate(parameterIndex);
	}

	public final Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return this.cstmt.getTimestamp(parameterIndex);
	}

	public final BigDecimal getBigDecimal(int parameterIndex)
			throws SQLException {
		return this.cstmt.getBigDecimal(parameterIndex);
	}

	public final Object getObject(int parameterIndex) throws SQLException {
		return this.cstmt.getObject(parameterIndex);
	}

	public final void registerOutParameter(int parameterIndex, int sqlType)
			throws SQLException {
		this.ensure();
		this.cstmt.registerOutParameter(parameterIndex, sqlType);
	}
}