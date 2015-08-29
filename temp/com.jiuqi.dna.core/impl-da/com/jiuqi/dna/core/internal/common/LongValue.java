package com.jiuqi.dna.core.internal.common;

import com.jiuqi.dna.core.impl.DataTypeBase;
import com.jiuqi.dna.core.impl.LongConstExpr;
import com.jiuqi.dna.core.impl.LongType;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

public final class LongValue implements ReadableValue {

	public static final LongValue valueOf(long value) {
		if (value == 0) {
			return LongValue.ZERO;
		} else if (value == 1) {
			return LongValue.POSITIVE_ONE;
		} else if (value == -1) {
			return LongValue.NEGATIVE_ONE;
		}
		return new LongValue(value);
	}

	public static final LongConstExpr valueOf(String value) {
		return LongConstExpr.valueOf(Long.parseLong(value));
	}

	public final DataTypeBase getType() {
		return LongType.TYPE;
	}

	public final boolean getBoolean() {
		return Convert.toBoolean(this.value);
	}

	public final char getChar() {
		return Convert.toChar(this.value);
	}

	public final byte getByte() {
		return Convert.toByte(this.value);
	}

	public final byte[] getBytes() {
		return Convert.toBytes(this.value);
	}

	public final long getDate() {
		return Convert.toDate(this.value);
	}

	public final double getDouble() {
		return Convert.toDouble(this.value);
	}

	public final float getFloat() {
		return Convert.toFloat(this.value);
	}

	public final GUID getGUID() {
		return Convert.toGUID(this.value);
	}

	public final int getInt() {
		return Convert.toInt(this.value);
	}

	public final long getLong() {
		return Convert.toLong(this.value);
	}

	public final short getShort() {
		return Convert.toShort(this.value);
	}

	public final String getString() {
		return Convert.toString(this.value);
	}

	public final Object getObject() {
		return Convert.toObject(this.value);
	}

	public final boolean isNull() {
		return false;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof LongValue) {
			LongValue o = (LongValue) obj;
			return this.value == o.value;
		} else {
			return false;
		}
	}

	@Override
	public final int hashCode() {
		return (int) (this.value ^ this.value >>> 32);
	}

	@Override
	public final String toString() {
		return Long.toString(this.value);
	}

	public final static LongValue ZERO = new LongValue(0);

	public final static LongValue POSITIVE_ONE = new LongValue(1);

	public final static LongValue NEGATIVE_ONE = new LongValue(-1);

	public final long value;

	private LongValue(long value) {
		this.value = value;
	}
}
