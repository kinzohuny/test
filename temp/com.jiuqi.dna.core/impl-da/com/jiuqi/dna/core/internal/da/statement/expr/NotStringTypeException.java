package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.impl.DataTypeInternal;

public class NotStringTypeException extends RuntimeException {

	private static final long serialVersionUID = -6051649788565083712L;

	public NotStringTypeException(Object op, DataTypeInternal type) {
		super("������[" + op + "]��, ����������Ϊ[" + type.toString() + "],�����ַ������͵ı���ʽ.");
	}
}