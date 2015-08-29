package com.jiuqi.dna.core.def.arg;

import com.jiuqi.dna.core.def.exp.AssignableExpression;

/**
 * 参数引用表达式
 * 
 * @author gaojingxin
 * 
 */
@SuppressWarnings("deprecation")
public interface ArgumentRefExpression extends AssignableExpression {

	/**
	 * 获得引用的参数或结构子段定义
	 * 
	 * @return 返回参数或结构子段定义
	 */
	public ArgumentDefine getArgument();
}