package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.DefineBase;
import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * �������
 * 
 * @author gaojingxin
 * 
 */
public interface OrderByItemDefine extends DefineBase {

	/**
	 * �����������Ƿ�������
	 * 
	 * @return
	 */
	public boolean isDesc();

	/**
	 * �����������������ʽ
	 * 
	 * @return
	 */
	public ValueExpression getExpression();
}
