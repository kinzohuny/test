package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * ������䶨��
 * 
 * @see com.jiuqi.dna.core.def.query.UpdateStatementDefine
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings("deprecation")
public interface UpdateStatementDeclare extends UpdateStatementDefine,
		ModifyStatementDeclare, FieldValueAssignable, RelationJoinable {

	/**
	 * ���ø�������
	 * 
	 * @param condition
	 */
	public void setCondition(ConditionalExpression condition);
}