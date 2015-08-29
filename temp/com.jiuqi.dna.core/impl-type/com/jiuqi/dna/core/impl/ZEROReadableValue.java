/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

public final class ZEROReadableValue implements ReadableValue {

	public byte[] getBytes() {
		return null;
	}

	public boolean getBoolean() {
		return false;
	}

	public char getChar() {
		return 0;
	}

	public long getDate() {
		return 0l;
	}

	public double getDouble() {
		return 0d;
	}

	public byte getByte() {
		return 0;
	}

	public short getShort() {
		return 0;
	}

	public float getFloat() {
		return 0f;
	}

	public int getInt() {
		return 0;
	}

	public long getLong() {
		return 0l;
	}

	public String getString() {
		return "";
	}

	public GUID getGUID() {
		return GUID.emptyID;
	}

	public Object getObject() {
		return None.NONE;
	}

	public boolean isNull() {
		return false;
	}

	public DataType getType() {
		return UnknownType.TYPE;
	}

	private ZEROReadableValue() {

	}

	public static final ZEROReadableValue INSTANCE = new ZEROReadableValue();
}