package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * ɾ����䶨��
 * 
 * @author houchunlei
 * 
 */
public interface DeleteStatementDefine extends ModifyStatementDefine {

	/**
	 * ��ȡɾ����������
	 * 
	 * @return
	 */
	public ConditionalExpression getCondition();
}
