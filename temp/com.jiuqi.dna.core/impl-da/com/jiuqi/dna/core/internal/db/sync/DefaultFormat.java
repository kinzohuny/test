package com.jiuqi.dna.core.internal.db.sync;

import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public abstract class DefaultFormat extends TypeDetectorBase<String, ConstExpr> {

	@Override
	public String inBoolean(ConstExpr c) throws Throwable {
		return c.getBoolean() ? "1" : "0";
	}

	@Override
	public String inByte(ConstExpr c) throws Throwable {
		return Byte.toString(c.getByte());
	}

	@Override
	public String inShort(ConstExpr c) throws Throwable {
		return Short.toString(c.getShort());
	}

	@Override
	public String inInt(ConstExpr c) throws Throwable {
		return Integer.toString(c.getInt());
	}

	@Override
	public String inLong(ConstExpr c) throws Throwable {
		return Long.toString(c.getLong());
	}

	@Override
	public String inFloat(ConstExpr c) throws Throwable {
		return Float.toString(c.getFloat());
	}

	@Override
	public String inDouble(ConstExpr c) throws Throwable {
		return Double.toString(c.getDouble());
	}

	@Override
	public String inString(ConstExpr c, SequenceDataType type) throws Throwable {
		return "\'" + escape(c.getString()) + "\'";
	}

	@Override
	public abstract String inDate(ConstExpr c) throws Throwable;

	@Override
	public String inBytes(ConstExpr c, SequenceDataType type) throws Throwable {
		return this.formatSql(c.getBytes());
	}

	@Override
	public String inGUID(ConstExpr c) throws Throwable {
		return this.formatSql(c.getBytes());
	}

	protected abstract String formatSql(byte[] value);

	protected static final String escape(String s) {
		if (s == null) {
			return "null";
		}
		final int l = s.length();
		if (l == 0) {
			return "";
		}
		StringBuilder sb = null;
		int start = 0;
		for (int i = s.indexOf('\'', 0); i > 0; i = s.indexOf('\'', i + 1)) {
			if (sb == null) {
				sb = new StringBuilder(l * 5 / 4);
			}
			sb.append(s, start, i).append('\'');
			start = i + 1;
		}
		if (sb == null) {
			return s;
		} else {
			return sb.toString();
		}
	}
}