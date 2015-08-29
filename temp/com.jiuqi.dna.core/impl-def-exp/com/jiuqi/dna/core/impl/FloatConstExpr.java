package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;

/**
 * 单精度浮点型常量表达式
 * 
 * @author houchunlei
 * 
 */
public final class FloatConstExpr extends ConstExpr {

	public final static FloatConstExpr valueOf(float value) {
		if (value == 0) {
			return FloatConstExpr.ZERO_FLOAT;
		} else if (value == 1) {
			return FloatConstExpr.POSITIVE_ONE;
		} else if (value == -1) {
			return FloatConstExpr.NEGATIVE_ONE;
		}
		return new FloatConstExpr(value);
	}

	public final static FloatConstExpr valueOf(String value) {
		return FloatConstExpr.valueOf(Float.parseFloat(value));
	}

	@Override
	public final DataTypeBase getType() {
		return FloatType.TYPE;
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
		return this.value;
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
		} else if (obj instanceof FloatConstExpr && ((FloatConstExpr) obj).value == this.value) {
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Float.floatToIntBits(this.value);
	}

	@Override
	public final String toString() {
		return Float.toString(this.value);
	}

	public final static FloatConstExpr ZERO_FLOAT = new FloatConstExpr(0f);

	public final static FloatConstExpr POSITIVE_ONE = new FloatConstExpr(1f);

	public final static FloatConstExpr NEGATIVE_ONE = new FloatConstExpr(-1f);

	final float value;

	FloatConstExpr(float value) {
		this.value = value;
	}

	FloatConstExpr(String value) {
		this.value = Convert.toFloat(value);
	}

	FloatConstExpr(SXElement element) {
		this.value = Convert.toFloat(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitFloatExpr(this, context);
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value);
	}
}