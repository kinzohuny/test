package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.Operator;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;

interface OperatorIntrl extends Operator {

	/**
	 * �����������Ƿ�Ϸ������������������͡�
	 * 
	 * @param values
	 *            ������
	 * @return
	 */
	DataTypeInternal checkValues(ValueExpr[] values);

	/**
	 * ��ȡ�����ȷ����
	 * 
	 * @return
	 */
	boolean isNonDeterministic();

	void render(ISqlExprBuffer buffer, TableUsages usages, OperateExpr expr);
}
