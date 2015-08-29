package com.jiuqi.dna.core.internal.common;

import com.jiuqi.dna.core.impl.BooleanType;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;

public final class BooleanValue implements DataValue {

	public static final BooleanValue valueOf(boolean value) {
		if (value) {
			return TRUE;
		}
		return FALSE;
	}

	public final BooleanType getType() {
		return BooleanType.TYPE;
	}

	public final boolean getBoolean() {
		return this.value;
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
		}
		return this == obj;
	}

	@Override
	public final int hashCode() {
		return (this.value ? Boolean.TRUE : Boolean.FALSE).hashCode();
	}

	@Override
	public final String toString() {
		return java.lang.Boolean.toString(this.value);
	}

	public final boolean value;

	public static final BooleanValue TRUE = new BooleanValue(true);
	public static final BooleanValue FALSE = new BooleanValue(false);

	private BooleanValue(boolean value) {
		this.value = value;
	}
}