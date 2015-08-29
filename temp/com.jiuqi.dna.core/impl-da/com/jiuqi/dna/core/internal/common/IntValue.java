package com.jiuqi.dna.core.internal.common;

import com.jiuqi.dna.core.impl.DataTypeBase;
import com.jiuqi.dna.core.impl.IntType;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

public final class IntValue implements ReadableValue {

	public static final IntValue valueOf(int value) {
		if (value == 0) {
			return ZERO;
		} else if (value == 1) {
			return POSITIVE_ONE;
		} else if (value == -1) {
			return NEGATIVE_ONE;
		}
		return new IntValue(value);
	}

	public final DataTypeBase getType() {
		return IntType.TYPE;
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
		return this.value;
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
		} else if (obj == this) {
			return true;
		} else if (obj instanceof IntValue) {
			return ((IntValue) obj).value == this.value;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return this.value;
	}

	@Override
	public final String toString() {
		return Integer.toString(this.value);
	}

	public final int value;

	public static final IntValue ZERO = new IntValue(0);
	public static final IntValue POSITIVE_ONE = new IntValue(1);
	public static final IntValue NEGATIVE_ONE = new IntValue(-1);

	private IntValue(int value) {
		this.value = value;
	}
}