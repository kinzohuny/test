package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.impl.DataTypeInternal;

public final class NotSameCategoryTypeException extends RuntimeException {

	private static final long serialVersionUID = 2150851365001218380L;

	public NotSameCategoryTypeException(Object op, DataTypeInternal left,
			DataTypeInternal right) {
		super("在运算[" + op + "]中,运算体类型分别为[" + left.toString() + "]与[" + right.toString() + "].");
	}
}