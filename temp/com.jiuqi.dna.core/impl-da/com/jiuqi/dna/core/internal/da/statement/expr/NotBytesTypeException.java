package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.impl.DataTypeInternal;

public class NotBytesTypeException extends RuntimeException {

	private static final long serialVersionUID = 4989619255835461319L;

	public NotBytesTypeException(Object op, DataTypeInternal type) {
		super("在运算[" + op + "]中, 运算体类型为[" + type.toString() + "],不是二进制串类型的表达式.");
	}
}