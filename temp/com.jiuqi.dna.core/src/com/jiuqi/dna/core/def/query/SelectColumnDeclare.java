package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * 查询语句选择列定义
 * 
 * @author houchunlei
 * 
 */
public interface SelectColumnDeclare extends SelectColumnDefine,
		RelationColumnDeclare {

	public SelectDeclare getOwner();

	/**
	 * 设置列定义的表达式
	 */
	public void setExpression(ValueExpression value);
}
