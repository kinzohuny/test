package com.jiuqi.dna.core.internal.da.common;

import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.VariableValue;

public abstract class AbstractVariableValue implements VariableValue {

	protected abstract StructFieldDefineImpl getStructField();

	protected abstract DynObj readObj();

	protected abstract DynObj writeObj();

	public final boolean isNull() {
		return this.getStructField().isFieldValueNull(this.readObj());
	}

	public final Object getObject() {
		return this.getStructField().getFieldValueAsObject(this.readObj());
	}

	public final boolean getBoolean() {
		return this.getStructField().getFieldValueAsBoolean(this.readObj());
	}

	public final char getChar() {
		return this.getStructField().getFieldValueAsChar(this.readObj());
	}

	public final byte getByte() {
		return this.getStructField().getFieldValueAsByte(this.readObj());
	}

	public final short getShort() {
		return this.getStructField().getFieldValueAsShort(this.readObj());
	}

	public final int getInt() {
		return this.getStructField().getFieldValueAsInt(this.readObj());
	}

	public final long getLong() {
		return this.getStructField().getFieldValueAsLong(this.readObj());
	}

	public final long getDate() {
		return this.getStructField().getFieldValueAsDate(this.readObj());
	}

	public final float getFloat() {
		return this.getStructField().getFieldValueAsFloat(this.readObj());
	}

	public final double getDouble() {
		return this.getStructField().getFieldValueAsDouble(this.readObj());
	}

	public final byte[] getBytes() {
		return this.getStructField().getFieldValueAsBytes(this.readObj());
	}

	public final String getString() {
		return this.getStructField().getFieldValueAsString(this.readObj());
	}

	public final GUID getGUID() {
		return this.getStructField().getFieldValueAsGUID(this.readObj());
	}

	public final void setNull() {
		this.getStructField().setFieldValueNull(this.writeObj());
	}

	public final void setObject(Object value) {
		this.getStructField().setFieldValueAsObject(this.writeObj(), value);
	}

	public final void setValue(ReadableValue value) {
		this.getStructField().setFieldValue(this.writeObj(), value);
	}

	public final void setBoolean(boolean value) {
		this.getStructField().setFieldValueAsBoolean(this.writeObj(), value);
	}

	public final void setChar(char value) {
		this.getStructField().setFieldValueAsChar(this.writeObj(), value);
	}

	public final void setShort(short value) {
		this.getStructField().setFieldValueAsShort(this.writeObj(), value);
	}

	public final void setInt(int value) {
		this.getStructField().setFieldValueAsInt(this.writeObj(), value);
	}

	public final void setLong(long value) {
		this.getStructField().setFieldValueAsLong(this.writeObj(), value);
	}

	public final void setDate(long value) {
		this.getStructField().setFieldValueAsDate(this.writeObj(), value);
	}

	public final void setFloat(float value) {
		this.getStructField().setFieldValueAsFloat(this.writeObj(), value);
	}

	public final void setDouble(double value) {
		this.getStructField().setFieldValueAsDouble(this.writeObj(), value);
	}

	public final void setString(String value) {
		this.getStructField().setFieldValueAsString(this.writeObj(), value);
	}

	public final void setByte(byte value) {
		this.getStructField().setFieldValueAsByte(this.writeObj(), value);
	}

	public final void setBytes(byte[] value) {
		this.getStructField().setFieldValueAsBytes(this.writeObj(), value);
	}

	public final void setGUID(GUID value) {
		this.getStructField().setFieldValueAsGUID(this.writeObj(), value);
	}
}