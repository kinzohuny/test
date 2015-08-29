package com.jiuqi.dna.core.internal.da.report;

import com.jiuqi.dna.core.da.ext.RPTRecordSetColumn;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

abstract class RPTRecordSetColumnImpl implements RPTRecordSetColumn {

	final int index;
	StructFieldDefineImpl structField;
	final RPTRecordSetImpl recordSet;
	final int generation;

	RPTRecordSetColumnImpl(RPTRecordSetImpl recordSet, int index,
			StructFieldDefineImpl structField) {
		this.recordSet = recordSet;
		this.generation = recordSet.generation;
		this.index = index;
		this.structField = structField;
	}

	public final int getIndex() {
		return this.index;
	}

	public final boolean getBoolean() {
		return this.structField.getFieldValueAsBoolean(this.recordSet.getRecordRead());
	}

	public final byte getByte() {
		return this.structField.getFieldValueAsByte(this.recordSet.getRecordRead());
	}

	public final byte[] getBytes() {
		return this.structField.getFieldValueAsBytes(this.recordSet.getRecordRead());
	}

	public final char getChar() {
		return this.structField.getFieldValueAsChar(this.recordSet.getRecordRead());
	}

	public final long getDate() {
		return this.structField.getFieldValueAsDate(this.recordSet.getRecordRead());
	}

	public final double getDouble() {
		return this.structField.getFieldValueAsDouble(this.recordSet.getRecordRead());
	}

	public final float getFloat() {
		return this.structField.getFieldValueAsFloat(this.recordSet.getRecordRead());
	}

	public final GUID getGUID() {
		return this.structField.getFieldValueAsGUID(this.recordSet.getRecordRead());
	}

	public final int getInt() {
		return this.structField.getFieldValueAsInt(this.recordSet.getRecordRead());
	}

	public final long getLong() {
		return this.structField.getFieldValueAsLong(this.recordSet.getRecordRead());
	}

	public final Object getObject() {
		return this.structField.getFieldValueAsObject(this.recordSet.getRecordRead());
	}

	public final short getShort() {
		return this.structField.getFieldValueAsShort(this.recordSet.getRecordRead());
	}

	public final String getString() {
		return this.structField.getFieldValueAsString(this.recordSet.getRecordRead());
	}

	public final boolean isNull() {
		return this.structField.isFieldValueNull(this.recordSet.getRecordRead());
	}

	public final DataType getType() {
		return this.structField.getType();
	}

	public void setBoolean(boolean value) {
		this.structField.setFieldValueAsBoolean(this.recordSet.getRecordWrite(), value);
	}

	public void setByte(byte value) {
		this.structField.setFieldValueAsByte(this.recordSet.getRecordWrite(), value);
	}

	public void setBytes(byte[] value) {
		this.structField.setFieldValueAsBytes(this.recordSet.getRecordWrite(), value);
	}

	public void setChar(char value) {
		this.structField.setFieldValueAsChar(this.recordSet.getRecordWrite(), value);
	}

	public void setDate(long value) {
		this.structField.setFieldValueAsDate(this.recordSet.getRecordWrite(), value);
	}

	public void setDouble(double value) {
		this.structField.setFieldValueAsDouble(this.recordSet.getRecordWrite(), value);
	}

	public void setFloat(float value) {
		this.structField.setFieldValueAsFloat(this.recordSet.getRecordWrite(), value);
	}

	public void setGUID(GUID guid) {
		this.structField.setFieldValueAsGUID(this.recordSet.getRecordWrite(), guid);
	}

	public void setInt(int value) {
		this.structField.setFieldValueAsInt(this.recordSet.getRecordWrite(), value);
	}

	public void setLong(long value) {
		this.structField.setFieldValueAsLong(this.recordSet.getRecordWrite(), value);
	}

	public void setNull() {
		this.structField.setFieldValueNull(this.recordSet.getRecordWrite());
	}

	public void setObject(Object value) {
		this.structField.setFieldValueAsObject(this.recordSet.getRecordWrite(), value);
	}

	public void setShort(short value) {
		this.structField.setFieldValueAsShort(this.recordSet.getRecordWrite(), value);
	}

	public void setString(String value) {
		this.structField.setFieldValueAsString(this.recordSet.getRecordWrite(), value);
	}

	public void setValue(ReadableValue value) {
		this.structField.setFieldValue(this.recordSet.getRecordWrite(), value);
	}
}