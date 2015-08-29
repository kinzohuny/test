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
		return "��ִ�д洢����[" + procedure.getName() + "]�Ĺ����ű���ָ�����ƵĴ洢����δ�ɹ����������ݿ⡣";
	}

	public static final String message(UserFunctionDefine function) {
		return "��ִ���û����庯��[" + function.getName() + "]�Ĺ����ű���ָ�����Ƶĺ���δ�ɹ����������ݿ⡣";
	}

}
