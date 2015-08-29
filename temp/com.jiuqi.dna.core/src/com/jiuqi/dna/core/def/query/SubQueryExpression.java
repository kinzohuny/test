package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * 子查询表达式
 * 
 * @author gaojingxin
 */
public interface SubQueryExpression extends ValueExpression {

	/**
	 * 获得子查询定义
	 * 
	 * @return 返回子查询定义
	 */
	public SubQueryDefine getSubQuery();
}
