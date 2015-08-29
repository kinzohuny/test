package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.impl.DataTypeInternal;

public class NotNumberTypeException extends RuntimeException {

	private static final long serialVersionUID = 2312601577480006977L;

	public NotNumberTypeException(Object op, DataTypeInternal type) {
		super("在运算[" + op + "]中, 运算体类型为[" + type.toString() + "],不是数值类型的表达式.");
	}
}