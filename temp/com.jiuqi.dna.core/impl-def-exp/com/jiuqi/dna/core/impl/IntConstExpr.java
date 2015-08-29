package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;

/**
 * ���γ������ʽ
 * 
 * @author houchunlei
 * 
 */
public final class IntConstExpr extends ConstExpr {

	public final static IntConstExpr valueOf(int value) {
		if (value == 0) {
			return IntConstExpr.ZERO_INT;
		} else if (value == 1) {
			return IntConstExpr.ONE;
		} else if (value == -1) {
			return IntConstExpr.NEGATIVE_ONE;
		}
		return new IntConstExpr(value);
	}

	public static final IntConstExpr valueOf(String value) {
		return IntConstExpr.valueOf(Integer.parseInt(value));
	}

	@Override
	public final DataTypeBase getType() {
		return IntType.TYPE;
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
		return this.value;
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
		} else if (obj instanceof IntConstExpr && ((IntConstExpr) obj).value == this.value) {
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return this.value;
	}

	@Override
	public final String toString() {
		return Integer.toString(this.value);
	}

	public final static IntConstExpr ZERO_INT = new IntConstExpr(0);
	public final static IntConstExpr ONE = new IntConstExpr(1);
	public final static IntConstExpr TWO = new IntConstExpr(2);
	public final static IntConstExpr THREE = new IntConstExpr(3);
	public final static IntConstExpr FOUR = new IntConstExpr(4);
	public final static IntConstExpr NEGATIVE_ONE = new IntConstExpr(-1);

	final int value;

	IntConstExpr(int value) {
		this.value = value;
	}

	IntConstExpr(String value) {
		this.value = Convert.toInt(value);
	}

	IntConstExpr(SXElement element) {
		this.value = Convert.toInt(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitIntExpr(this, context);
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value);
	}
}