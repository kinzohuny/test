package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.Operator;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;

interface OperatorIntrl extends Operator {

	/**
	 * 检查运算体的是否合法，并返回运算结果类型。
	 * 
	 * @param values
	 *            运算体
	 * @return
	 */
	DataTypeInternal checkValues(ValueExpr[] values);

	/**
	 * 获取运算的确定性
	 * 
	 * @return
	 */
	boolean isNonDeterministic();

	void render(ISqlExprBuffer buffer, TableUsages usages, OperateExpr expr);
}
