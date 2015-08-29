package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.query.QuRelationRefDefine;

/**
 * 级次函数表达式
 * 
 * @author houchunlei
 * 
 */
@Deprecated
public interface HierarchyOperateExpression extends ValueExpression {

	/**
	 * 获取级次运算符
	 */
	@Deprecated
	public HierarchyOperator getOperator();

	/**
	 * 表引用
	 */
	@Deprecated
	public QuRelationRefDefine getSource();

	/**
	 * 级次值参数
	 */
	@Deprecated
	public ValueExpression getLevel();
}