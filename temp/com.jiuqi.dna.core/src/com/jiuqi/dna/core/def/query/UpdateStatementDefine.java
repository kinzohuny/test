package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * ������䶨��
 * 
 * @author houchunlei
 */
public interface UpdateStatementDefine extends ModifyStatementDefine {

	/**
	 * ��ȡ������������
	 * 
	 * @return
	 */
	ConditionalExpression getCondition();
}
