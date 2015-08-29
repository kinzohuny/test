package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.impl.DataTypeInternal;

public class NotStringTypeException extends RuntimeException {

	private static final long serialVersionUID = -6051649788565083712L;

	public NotStringTypeException(Object op, DataTypeInternal type) {
		super("在运算[" + op + "]中, 运算体类型为[" + type.toString() + "],不是字符串类型的表达式.");
	}
}