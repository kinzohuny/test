package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.impl.DataTypeInternal;

public class NotNumberTypeException extends RuntimeException {

	private static final long serialVersionUID = 2312601577480006977L;

	public NotNumberTypeException(Object op, DataTypeInternal type) {
		super("������[" + op + "]��, ����������Ϊ[" + type.toString() + "],������ֵ���͵ı��ʽ.");
	}
}