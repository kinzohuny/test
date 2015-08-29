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
		return "�ڴ洢����[" + procedure + "]��, ����[" + prev.name + "]���������Ϊ[" + prev.output + "]. ��������֮�����������Ϊ[" + arg.output + "]�Ĳ���[" + arg.name + "].";
	}
}