package com.jiuqi.dna.core.impl;

import java.util.Arrays;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;

/**
 * 字节数组常量表达式
 * 
 * @author houchunlei
 * 
 */
public final class BytesConstExpr extends ConstExpr {

	public final static BytesConstExpr valueOf(byte[] value) {
		return new BytesConstExpr(value);
	}

	@Override
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
		} else if (obj instanceof BytesConstExpr && Arrays.equals(((BytesConstExpr) obj).value, this.value)) {
			return true;
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

	public static final BytesConstExpr EMPTY = valueOf(new byte[] {});

	private final byte[] value;

	BytesConstExpr(byte[] value) {
		this.value = value;
	}

	BytesConstExpr(String value) {
		this.value = Convert.toBytes(value);
	}

	BytesConstExpr(SXElement element) {
		this.value = Convert.toBytes(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitBytesExpr(this, context);
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value);
	}
}