package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.impl.DataTypeInternal;

public class NotDateTypeException extends RuntimeException {

	private static final long serialVersionUID = 7250426101774104800L;

	public NotDateTypeException(Object op, DataTypeInternal type) {
		super("������[" + op + "]��, ����������Ϊ[" + type.toString() + "],�����������͵ı��ʽ.");
	}
}