package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.StoredProcedureDefine;

/**
 * 存储过程执行成功后，未返回期望个数的结果集。
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
	 *            存储过程定义
	 * @param returned
	 *            实际运行返回的结果集个数
	 */
	public ProcedureExpectedResultSetsDissatifiedException(
			StoredProcedureDefine procedure, int returned) {
		super(message(procedure, returned));
		this.procedure = procedure;
	}

	public static final String message(StoredProcedureDefine procedure,
			int returned) {
		return "存储过程[" + procedure.getName() + "]期望返回[" + procedure.getResultSets() + "]个结果集，实际执行" + (returned == 0 ? "未返回任何结果集。" : "只返回[" + returned + "]个结果集。");
	}
}