package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.DefineBase;
import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * 排序项定义
 * 
 * @author gaojingxin
 * 
 */
public interface OrderByItemDefine extends DefineBase {

	/**
	 * 返回排序项是否倒序排列
	 * 
	 * @return
	 */
	public boolean isDesc();

	/**
	 * 返回排序项的排序表达式
	 * 
	 * @return
	 */
	public ValueExpression getExpression();
}
