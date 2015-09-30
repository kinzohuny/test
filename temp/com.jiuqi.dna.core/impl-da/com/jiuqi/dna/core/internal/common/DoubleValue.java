package com.jiuqi.dna.core.internal.common;

import com.jiuqi.dna.core.impl.DoubleType;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

public final class DoubleValue implements ReadableValue {

	public static final DoubleValue valueOf(double value) {
		return new DoubleValue(value);
	}

	public final DoubleType getType() {
		return DoubleType.TYPE;
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
		} else if (obj == this) {
			return true;
		} else if (obj instanceof DoubleValue) {
			return ((DoubleValue) obj).value == this.value;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		long bits = Double.doubleToLongBits(this.value);
		return (int) (bits ^ bits >>> 32);
	}

	@Override
	public final String toString() {
		return Double.toString(this.value);
	}

	public final static DoubleValue ZERO = new DoubleValue(0d);

	public final static DoubleValue POSITIVE_ONE = new DoubleValue(1d);

	public final static DoubleValue NEGATIVE_ONE = new DoubleValue(-1d);

	public final double value;

	DoubleValue(double value) {
		this.value = value;
	}
}