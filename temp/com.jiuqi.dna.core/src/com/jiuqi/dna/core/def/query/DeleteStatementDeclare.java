package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * ɾ����䶨��
 * 
 * @see com.jiuqi.dna.core.def.query.DeleteStatementDefine
 * 
 * @author houchunlei
 */
@SuppressWarnings("deprecation")
public interface DeleteStatementDeclare extends DeleteStatementDefine,
		ModifyStatementDeclare, RelationJoinable {

	/**
	 * ����ɾ���������
	 * 
	 * @param condition
	 */
	public void setCondition(ConditionalExpression condition);
}
