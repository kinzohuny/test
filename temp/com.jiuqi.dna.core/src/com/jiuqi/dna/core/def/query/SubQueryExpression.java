package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * �Ӳ�ѯ���ʽ
 * 
 * @author gaojingxin
 */
public interface SubQueryExpression extends ValueExpression {

	/**
	 * ����Ӳ�ѯ����
	 * 
	 * @return �����Ӳ�ѯ����
	 */
	public SubQueryDefine getSubQuery();
}
