package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * 查询分组规则定义
 * 
 * @see com.jiuqi.dna.core.def.query.GroupByItemDefine
 * 
 * @author houchunlei
 * 
 */
public interface GroupByItemDeclare extends GroupByItemDefine {

	/**
	 * 设置分组规则的表达式
	 * 
	 * @param expression
	 */
	public void setExpression(ValueExpression expression);
}
