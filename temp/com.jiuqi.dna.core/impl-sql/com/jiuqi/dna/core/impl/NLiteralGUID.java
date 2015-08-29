package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.spi.sql.SQLValueFormatException;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.TypeFactory;
import com.jiuqi.dna.core.type.ValueConvertException;

/**
 * GUID型字面量节点
 * 
 * @author niuhaifeng
 * 
 */
class NLiteralGUID extends NLiteral {
	public static final NLiteralGUID EMPTY = new NLiteralGUID(new TString(
			GUID.emptyID.toString(), 0, 0, 0));
	public final GUID value;

	public NLiteralGUID(TString value) {
		super(value);
		this.value = parseGUID(value);
	}

	@Override
	public DataType getType() {
		return TypeFactory.GUID;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralGUID(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralGUID) {
			return ((NLiteralGUID) obj).value.equals(this.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}

	private static GUID parseGUID(TString guid) {
		try {
			return GUID.valueOf(guid.value);
		} catch (ValueConvertException vce) {
			throw new SQLValueFormatException(guid.line, guid.col,
					"GUID格式不正确 '" + guid.value + "'");
		}
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
