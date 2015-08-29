package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * 更新语句定义
 * 
 * @author houchunlei
 */
public interface UpdateStatementDefine extends ModifyStatementDefine {

	/**
	 * 获取更新语句的条件
	 * 
	 * @return
	 */
	ConditionalExpression getCondition();
}
