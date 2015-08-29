package com.jiuqi.dna.core.internal.da.report;

import com.jiuqi.dna.core.da.ext.RPTRecordSetField;
import com.jiuqi.dna.core.exception.NumericOverflowException;
import com.jiuqi.dna.core.impl.DataTypeInternal;
import com.jiuqi.dna.core.impl.RefDataType;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

final class RPTRecordSetFieldImpl extends RPTRecordSetColumnImpl implements
		RPTRecordSetField {

	@Override
	public final String toString() {
		return "field:".concat(this.structField.name);
	}

	final RPTRecordSetRestrictionImpl restriction;

	final TableFieldDefineImpl tableField;

	final boolean usingBigDecimal;

	public final TableFieldDefineImpl getTableField() {
		return this.tableField;
	}

	public final RPTRecordSetRestrictionImpl getRestriction() {
		return this.restriction;
	}

	RPTRecordSetFieldImpl(RPTRecordSetImpl owner,
			TableFieldDefineImpl tableField,
			RPTRecordSetRestrictionImpl restriction, boolean usingBigDecimal) {
		super(owner, owner.fields.size(), owner.recordStruct.newField(typeOf(tableField, usingBigDecimal)));
		this.tableField = tableField;
		this.restriction = restriction;
		this.usingBigDecimal = usingBigDecimal;
	}

	private static final DataTypeInternal typeOf(
			TableFieldDefineImpl tableField, boolean usingBigDecimal) {
		DataTypeInternal type = tableField.getType();
		if (type.isNumber() && usingBigDecimal) {
			return RefDataType.bigDecimalType;
		}
		return type;
	}

	private final void updateRecordMask() {
		this.restriction.recordSet.updateRecordMask(this.restriction.index);
	}

	@Override
	public final void setBoolean(boolean value) {
		this.updateRecordMask();
		super.setBoolean(value);
	}

	@Override
	public final void setByte(byte value) {
		this.updateRecordMask();
		super.setByte(value);
	}

	@Override
	public final void setBytes(byte[] value) {
		this.updateRecordMask();
		super.setBytes(value);
	}

	@Override
	public final void setChar(char value) {
		this.updateRecordMask();
		super.setChar(value);
	}

	@Override
	public final void setDate(long value) {
		this.updateRecordMask();
		super.setDate(value);
	}

	@Override
	public final void setDouble(double value) {
		this.updateRecordMask();
		try {
			super.setDouble(value);
		} catch (NumericOverflowException e) {
			throw new NumericOverflowException(e.precision, e.scale, e.overflow, this.tableField.name);
		}
	}

	@Override
	public final void setFloat(float value) {
		this.updateRecordMask();
		try {
			super.setFloat(value);
		} catch (NumericOverflowException e) {
			throw new NumericOverflowException(e.precision, e.scale, e.overflow, this.tableField.name);
		}
	}

	@Override
	public final void setGUID(GUID guid) {
		this.updateRecordMask();
		super.setGUID(guid);
	}

	@Override
	public final void setInt(int value) {
		this.updateRecordMask();
		super.setInt(value);
	}

	@Override
	public final void setLong(long value) {
		this.updateRecordMask();
		try {
			super.setLong(value);
		} catch (NumericOverflowException e) {
			throw new NumericOverflowException(e.precision, e.scale, e.overflow, this.tableField.name);
		}
	}

	@Override
	public final void setNull() {
		this.updateRecordMask();
		super.setNull();
	}

	@Override
	public final void setObject(Object value) {
		this.updateRecordMask();
		try {
			super.setObject(value);
		} catch (NumericOverflowException e) {
			throw new NumericOverflowException(e.precision, e.scale, e.overflow, this.tableField.name);
		}
	}

	@Override
	public final void setShort(short value) {
		this.updateRecordMask();
		super.setShort(value);
	}

	@Override
	public final void setString(String value) {
		this.updateRecordMask();
		try {
			super.setString(value);
		} catch (NumericOverflowException e) {
			throw new NumericOverflowException(e.precision, e.scale, e.overflow, this.tableField.name);
		}
	}

	@Override
	public final void setValue(ReadableValue value) {
		this.updateRecordMask();
		try {
			super.setValue(value);
		} catch (NumericOverflowException e) {
			throw new NumericOverflowException(e.precision, e.scale, e.overflow, this.tableField.name);
		}
	}

}
