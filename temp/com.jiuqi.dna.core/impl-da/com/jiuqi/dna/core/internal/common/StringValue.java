package com.jiuqi.dna.core.internal.common;

import com.jiuqi.dna.core.impl.DataTypeBase;
import com.jiuqi.dna.core.impl.StringType;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

public final class StringValue implements ReadableValue {

	public final DataTypeBase getType() {
		return StringType.TYPE;
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
		return this.value;
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
		} else if (obj instanceof StringValue) {
			final StringValue other = (StringValue) obj;
			if (other.value == null) {
				return this.value == null;
			} else {
				return other.value.equals(this.value);
			}
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public final String toString() {
		return "'" + this.value + "'";
	}

	public final String value;

	public StringValue(String value) {
		this.value = value;
	}
}