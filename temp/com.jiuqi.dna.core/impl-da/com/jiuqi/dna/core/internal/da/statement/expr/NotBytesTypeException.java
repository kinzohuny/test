package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.impl.DataTypeInternal;

public class NotBytesTypeException extends RuntimeException {

	private static final long serialVersionUID = 4989619255835461319L;

	public NotBytesTypeException(Object op, DataTypeInternal type) {
		super("������[" + op + "]��, ����������Ϊ[" + type.toString() + "],���Ƕ����ƴ����͵ı��ʽ.");
	}
}