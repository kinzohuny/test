package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * 更新语句定义
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
	 * 设置更新条件
	 * 
	 * @param condition
	 */
	public void setCondition(ConditionalExpression condition);
}