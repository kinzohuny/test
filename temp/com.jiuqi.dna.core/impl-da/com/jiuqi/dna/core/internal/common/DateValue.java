package com.jiuqi.dna.core.internal.common;

import com.jiuqi.dna.core.impl.DateType;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DateParser;
import com.jiuqi.dna.core.type.GUID;

public final class DateValue implements DataValue {

	public static final DateValue valueOf(long value) {
		return new DateValue(value);
	}

	public static final DateValue valueOf(String value) {
		return valueOf(Convert.toDate(value));
	}

	public final DateType getType() {
		return DateType.TYPE;
	}

	public final boolean getBoolean() {
		return Convert.dateToBoolean(this.value);
	}

	public final char getChar() {
		return Convert.toChar(this.value);
	}

	public final byte getByte() {
		return Convert.dateToByte(this.value);
	}

	public final byte[] getBytes() {
		return Convert.dateToBytes(this.value);
	}

	public final long getDate() {
		return this.value;
	}

	public final double getDouble() {
		return Convert.dateToDouble(this.value);
	}

	public final float getFloat() {
		return Convert.dateToFloat(this.value);
	}

	public final GUID getGUID() {
		return Convert.dateToGUID(this.value);
	}

	public final int getInt() {
		return Convert.dateToInt(this.value);
	}

	public final long getLong() {
		return Convert.dateToLong(this.value);
	}

	public final short getShort() {
		return Convert.dateToShort(this.value);
	}

	public final String getString() {
		return Convert.dateToString(this.value);
	}

	public final Object getObject() {
		return Convert.dateToObject(this.value);
	}

	public final boolean isNull() {
		return false;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof DateValue) {
			DateValue o = (DateValue) obj;
			return this.value == o.value;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return (int) (this.value ^ this.value >>> 32);
	}

	@Override
	public final String toString() {
		return "date'" + DateParser.format(this.value, DateParser.FORMAT_DATE_TIME_AUTOMS) + "'";
	}

	private final long value;

	DateValue(long value) {
		this.value = value;
	}

	DateValue(String value) {
		this.value = Convert.toDate(value);
	}
}
