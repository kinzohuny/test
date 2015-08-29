package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * 删除语句定义
 * 
 * @see com.jiuqi.dna.core.def.query.DeleteStatementDefine
 * 
 * @author houchunlei
 */
@SuppressWarnings("deprecation")
public interface DeleteStatementDeclare extends DeleteStatementDefine,
		ModifyStatementDeclare, RelationJoinable {

	/**
	 * 设置删除语句条件
	 * 
	 * @param condition
	 */
	public void setCondition(ConditionalExpression condition);
}
