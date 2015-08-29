package com.jiuqi.dna.core.internal.da.statement;

import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;

public final class IllegalArgumentOutputOrderException extends RuntimeException {

	private static final long serialVersionUID = -6326560314417741323L;

	public IllegalArgumentOutputOrderException(
			StoredProcedureDefineImpl procedure, StructFieldDefineImpl prev,
			StructFieldDefineImpl arg) {
		super(message(procedure, prev, arg));
	}

	public static final String message(StoredProcedureDefineImpl procedure,
			StructFieldDefineImpl prev, StructFieldDefineImpl arg) {
		return "在存储过程[" + procedure + "]中, 参数[" + prev.name + "]的输出类型为[" + prev.output + "]. 不能在其之后定义输出类型为[" + arg.output + "]的参数[" + arg.name + "].";
	}
}