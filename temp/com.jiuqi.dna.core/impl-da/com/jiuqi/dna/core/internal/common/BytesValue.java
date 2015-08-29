package com.jiuqi.dna.core.internal.common;

import java.util.Arrays;

import com.jiuqi.dna.core.impl.BytesType;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

public final class BytesValue implements ReadableValue {

	public final BytesType getType() {
		return BytesType.TYPE;
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
		return this.value;
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
		} else if (obj instanceof BytesValue) {
			BytesValue bv = (BytesValue) obj;
			return Arrays.equals(bv.value, this.value);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public final String toString() {
		return "bytes'" + Convert.bytesToHex(this.value, false, false) + "'";
	}

	private final byte[] value;

	public BytesValue(byte[] value) {
		this.value = value;
	}
}
