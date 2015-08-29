package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * 删除语句定义
 * 
 * @author houchunlei
 * 
 */
public interface DeleteStatementDefine extends ModifyStatementDefine {

	/**
	 * 获取删除语句的条件
	 * 
	 * @return
	 */
	public ConditionalExpression getCondition();
}
