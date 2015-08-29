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
		return "�����洢����[" + procedure.getName() + "]ʱ�쳣��";
	}

	public static final String message(UserFunctionDefine function) {
		return "�����û����庯������[" + function.getName() + "]ʱ�쳣��";
	}
}
