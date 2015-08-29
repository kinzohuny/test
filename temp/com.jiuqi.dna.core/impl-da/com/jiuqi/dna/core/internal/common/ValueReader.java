package com.jiuqi.dna.core.internal.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

public interface ValueReader {

	public ReadableValue read(ResultSet resultSet, int jdbcIndex)
			throws SQLException;

	public static final ValueReader BOOLEAN = new ValueReader() {

		public ReadableValue read(ResultSet resultSet, int jdbcIndex)
				throws SQLException {
			final boolean value = resultSet.getBoolean(jdbcIndex);
			if (resultSet.wasNull()) {
				return ReadableValue.NULL;
			}
			return BooleanValue.valueOf(value);
		}
	};

	public static final ValueReader INT = new ValueReader() {

		public ReadableValue read(ResultSet resultSet, int jdbcIndex)
				throws SQLException {
			final int value = resultSet.getInt(jdbcIndex);
			if (resultSet.wasNull()) {
				return ReadableValue.NULL;
			}
			return IntValue.valueOf(value);
		}
	};

	public static final ValueReader LONG = new ValueReader() {

		public ReadableValue read(ResultSet resultSet, int jdbcIndex)
				throws SQLException {
			final long value = resultSet.getLong(jdbcIndex);
			if (resultSet.wasNull()) {
				return ReadableValue.NULL;
			}
			return LongValue.valueOf(value);
		}
	};

	public static final ValueReader DOUBLE = new ValueReader() {

		public ReadableValue read(ResultSet resultSet, int jdbcIndex)
				throws SQLException {
			final double value = resultSet.getDouble(jdbcIndex);
			if (resultSet.wasNull()) {
				return ReadableValue.NULL;
			}
			return DoubleValue.valueOf(value);
		}
	};

	public static final ValueReader STRING = new ValueReader() {

		public ReadableValue read(ResultSet resultSet, int jdbcIndex)
				throws SQLException {
			final String value = resultSet.getString(jdbcIndex);
			if (value == null) {
				return ReadableValue.NULL;
			}
			return new StringValue(value);
		}
	};

	public static final ValueReader BYTES = new ValueReader() {

		public ReadableValue read(ResultSet resultSet, int jdbcIndex)
				throws SQLException {
			final byte[] value = resultSet.getBytes(jdbcIndex);
			if (value == null) {
				return ReadableValue.NULL;
			}
			return new BytesValue(value);
		}
	};

	public static final ValueReader GUID_ = new ValueReader() {

		public ReadableValue read(ResultSet resultSet, int jdbcIndex)
				throws SQLException {
			byte[] bytes = resultSet.getBytes(jdbcIndex);
			if (bytes == null) {
				return ReadableValue.NULL;
			}
			GUID guid = GUID.valueOf(bytes);
			return GUIDValue.valueOf(guid);
		}
	};

	public static final ValueReader DATE = new ValueReader() {

		public ReadableValue read(ResultSet resultSet, int jdbcIndex)
				throws SQLException {
			Timestamp ts = resultSet.getTimestamp(jdbcIndex);
			if (ts == null) {
				return ReadableValue.NULL;
			}
			return DateValue.valueOf(ts.getTime());
		}
	};
}