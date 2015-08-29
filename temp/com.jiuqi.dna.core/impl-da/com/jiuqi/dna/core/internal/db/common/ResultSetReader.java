package com.jiuqi.dna.core.internal.db.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public class ResultSetReader<TUserData> extends
		TypeDetectorBase<Object, TUserData> {

	private ResultSet resultSet;

	public final void close() {
		this.resultSet = null;
	}

	public final void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	private int jdbcIndex;

	public final void setJdbcIndex(int jdbcIndex) {
		this.jdbcIndex = jdbcIndex;
	}

	protected void readNull(TUserData userData) {
	}

	@Override
	public Object inBoolean(TUserData userData) throws Throwable {
		final boolean value = this.resultSet.getBoolean(this.jdbcIndex);
		if (this.resultSet.wasNull()) {
			this.readNull(userData);
			return null;
		} else {
			this.readBoolean(userData, value);
			return value;
		}
	}

	protected void readBoolean(TUserData userData, boolean value) {
	}

	@Override
	public final Object inByte(TUserData userData) throws SQLException {
		final byte value = this.resultSet.getByte(this.jdbcIndex);
		if (this.resultSet.wasNull()) {
			this.readNull(userData);
			return null;
		} else {
			this.readByte(userData, value);
			return value;
		}
	}

	protected void readByte(TUserData userData, byte value) {
	}

	@Override
	public final Object inShort(TUserData userData) throws SQLException {
		final short value = this.resultSet.getShort(this.jdbcIndex);
		if (this.resultSet.wasNull()) {
			this.readNull(userData);
			return null;
		} else {
			this.readShort(userData, value);
			return value;
		}
	}

	protected void readShort(TUserData userData, short value) {
	}

	@Override
	public final Object inInt(TUserData userData) throws SQLException {
		final int value = this.resultSet.getInt(this.jdbcIndex);
		if (this.resultSet.wasNull()) {
			this.readNull(userData);
			return null;
		} else {
			this.readInt(userData, value);
			return value;
		}
	}

	protected void readInt(TUserData userData, int value) {
	}

	@Override
	public final Object inLong(TUserData userData) throws SQLException {
		final long value = this.resultSet.getLong(this.jdbcIndex);
		if (this.resultSet.wasNull()) {
			this.readNull(userData);
			return null;
		} else {
			this.readLong(userData, value);
			return value;
		}
	}

	protected void readLong(TUserData userData, long value) {
	}

	@Override
	public final Object inFloat(TUserData userData) throws SQLException {
		final float value = this.resultSet.getFloat(this.jdbcIndex);
		if (this.resultSet.wasNull()) {
			this.readNull(userData);
			return null;
		} else {
			this.readFloat(userData, value);
			return value;
		}
	}

	protected void readFloat(TUserData userData, float value) {
	}

	@Override
	public final Object inDouble(TUserData userData) throws SQLException {
		final double value = this.resultSet.getDouble(this.jdbcIndex);
		if (this.resultSet.wasNull()) {
			this.readNull(userData);
			return null;
		} else {
			this.readDouble(userData, value);
			return value;
		}
	}

	protected void readDouble(TUserData userData, double value) {
	}

	@Override
	public final Object inString(TUserData userData, SequenceDataType type)
			throws SQLException {
		final String value = this.resultSet.getString(this.jdbcIndex);
		if (value == null) {
			this.readNull(userData);
			return null;
		} else {
			this.readString(userData, value);
			return value;
		}
	}

	protected void readString(TUserData userData, String value) {
	}

	@Override
	public final Object inBytes(TUserData userData, SequenceDataType type)
			throws SQLException {
		final byte[] value = this.resultSet.getBytes(this.jdbcIndex);
		if (value == null) {
			this.readNull(userData);
			return null;
		} else {
			this.readBytes(userData, value);
			return value;
		}
	}

	protected void readBytes(TUserData userData, byte[] value) {
	}

	@Override
	public final Object inGUID(TUserData userData) throws SQLException {
		GUID value = GUID.valueOf(this.resultSet.getBytes(this.jdbcIndex));
		if (value == null) {
			this.readNull(userData);
			return null;
		} else {
			this.readGUID(userData, value);
			return value;
		}
	}

	protected void readGUID(TUserData userData, GUID value) {
	}

	@Override
	public final Object inDate(TUserData userData) throws SQLException {
		final Timestamp value = this.resultSet.getTimestamp(this.jdbcIndex);
		if (value == null) {
			this.readNull(userData);
			return null;
		} else {
			this.readDate(userData, value);
			return value;
		}
	}

	protected void readDate(TUserData userData, Timestamp value) {
	}

	@Override
	public Object inObject(TUserData userData, ObjectDataType type)
			throws SQLException {
		final Object value = this.resultSet.getObject(this.jdbcIndex);
		if (value == null) {
			this.readNull(userData);
			return null;
		} else {
			this.readObject(userData, value);
			return value;
		}
	}

	protected void readObject(TUserData userData, Object value) {
	}
}