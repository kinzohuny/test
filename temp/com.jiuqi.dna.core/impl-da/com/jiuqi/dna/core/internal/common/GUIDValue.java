package com.jiuqi.dna.core.internal.common;

import com.jiuqi.dna.core.impl.DataTypeBase;
import com.jiuqi.dna.core.impl.GUIDType;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

public final class GUIDValue implements ReadableValue {

	public static final GUIDValue valueOf(GUID value) {
		if (value == GUID.emptyID || value.equals(GUID.emptyID)) {
			return EMPTY;
		}
		return new GUIDValue(value);
	}

	public final DataTypeBase getType() {
		return GUIDType.TYPE;
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
		return this.value;
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
		} else if (obj instanceof GUIDValue) {
			return ((GUIDValue) obj).value.equals(this.value);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public final String toString() {
		return "guid'" + this.value.toString(false, false) + "'";
	}

	public final GUID value;

	public static final GUIDValue EMPTY = new GUIDValue(GUID.emptyID);

	public GUIDValue(GUID value) {
		if (value == null) {
			throw new RuntimeException();
		}
		this.value = value;
	}
}