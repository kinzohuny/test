package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeFactory;

/**
 * �������������ڵ�
 * 
 * @author niuhaifeng
 * 
 */
class NLiteralLong extends NLiteral {
	public long value;

	public NLiteralLong(TLong value) {
		super(value);
		this.value = value.value;
	}

	@Override
	public DataType getType() {
		return TypeFactory.LONG;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralLong(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralLong) {
			return ((NLiteralLong) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) this.value;
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
