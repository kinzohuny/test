package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.StoredProcedureDefine;
import com.jiuqi.dna.core.def.query.UserFunctionDefine;

public final class CreateRoutineException extends RuntimeException {

	private static final long serialVersionUID = -8251998336182385480L;

	public CreateRoutineException(StoredProcedureDefine procedure,
			Throwable cause) {
		super(message(procedure), cause);
	}

	public CreateRoutineException(UserFunctionDefine function, Throwable cause) {
		super(message(function), cause);
	}

	public static final String message(StoredProcedureDefine procedure) {
		return "创建存储过程[" + procedure.getName() + "]时异常。";
	}

	public static final String message(UserFunctionDefine function) {
		return "创建用户定义函数过程[" + function.getName() + "]时异常。";
	}
}
