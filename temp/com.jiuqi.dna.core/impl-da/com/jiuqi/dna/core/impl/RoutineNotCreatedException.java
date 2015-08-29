package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.StoredProcedureDefine;
import com.jiuqi.dna.core.def.query.UserFunctionDefine;

public final class RoutineNotCreatedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RoutineNotCreatedException(StoredProcedureDefine procedure) {
		super(message(procedure));
	}

	public RoutineNotCreatedException(UserFunctionDefine function) {
		super(message(function));
	}

	public static final String message(StoredProcedureDefine procedure) {
		return "在执行存储过程[" + procedure.getName() + "]的构建脚本后，指定名称的存储过程未成功创建到数据库。";
	}

	public static final String message(UserFunctionDefine function) {
		return "在执行用户定义函数[" + function.getName() + "]的构建脚本后，指定名称的函数未成功创建到数据库。";
	}

}
