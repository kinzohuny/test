package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.StoredProcedureDefine;

public final class RoutineInvalidException extends RuntimeException {

	private static final long serialVersionUID = 9005974659917520955L;

	public RoutineInvalidException(StoredProcedureDefine procedure) {
		super("�洢����[" + procedure.getName() + "]���ڲ�����״̬.");
	}
}