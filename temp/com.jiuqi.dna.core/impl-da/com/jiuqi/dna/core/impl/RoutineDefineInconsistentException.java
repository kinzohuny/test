package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.StoredProcedureDefine;

public final class RoutineDefineInconsistentException extends RuntimeException {

	private static final long serialVersionUID = 2368310261842706644L;

	public RoutineDefineInconsistentException(StoredProcedureDefine procedure,
			String message) {
		super(message(procedure, message));
	}

	public static final String message(StoredProcedureDefine procedure,
			String message) {
		return "�洢����[" + procedure + "]��Java������������SQL�ű�ʵ�ֲ�һ��:" + message;
	}
}