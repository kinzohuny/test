package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.StoredProcedureDefine;

/**
 * �洢����ִ�гɹ���δ�������������Ľ������
 * 
 * @author houchunlei
 * 
 */
public final class ProcedureExpectedResultSetsDissatifiedException extends
		RuntimeException {

	private static final long serialVersionUID = 8752269223026693138L;

	public final StoredProcedureDefine procedure;

	/**
	 * @param procedure
	 *            �洢���̶���
	 * @param returned
	 *            ʵ�����з��صĽ��������
	 */
	public ProcedureExpectedResultSetsDissatifiedException(
			StoredProcedureDefine procedure, int returned) {
		super(message(procedure, returned));
		this.procedure = procedure;
	}

	public static final String message(StoredProcedureDefine procedure,
			int returned) {
		return "�洢����[" + procedure.getName() + "]��������[" + procedure.getResultSets() + "]���������ʵ��ִ��" + (returned == 0 ? "δ�����κν������" : "ֻ����[" + returned + "]���������");
	}
}