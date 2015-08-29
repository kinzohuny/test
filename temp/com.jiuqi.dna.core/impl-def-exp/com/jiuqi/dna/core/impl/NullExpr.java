package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.type.GUID;

/**
 * ø’±Ì¥Ô Ω
 * 
 * @author houchunlei
 * 
 */
public final class NullExpr extends ConstExpr {

	public final boolean isNull() {
		return true;
	}

	public final Object getObject() {
		return null;
	}

	public final boolean getBoolean() {
		return false;
	}

	public final char getChar() {
		return 0;
	}

	public final byte getByte() {
		return 0;
	}

	public final short getShort() {
		return 0;
	}

	public final int getInt() {
		return 0;
	}

	public final long getLong() {
		return 0;
	}

	public final long getDate() {
		return 0;
	}

	public final float getFloat() {
		return 0;
	}

	public final double getDouble() {
		return 0;
	}

	public final byte[] getBytes() {
		return null;
	}

	public final String getString() {
		return null;
	}

	public final GUID getGUID() {
		return null;
	}

	@Override
	public final DataTypeBase getType() {
		return NullType.TYPE;
	}

	@Override
	public final String toString() {
		return "NULL";
	}

	public final static NullExpr NULL = new NullExpr();

	static final String xml_element_null = "null-exp";

	NullExpr() {
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitNullExpr(this, context);
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadNull(null);
	}

	final void render(ISqlExprBuffer buffer, TableUsages usages,
			DataTypeInternal refer) {
		buffer.loadNull(refer);
	}
}