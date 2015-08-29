package com.jiuqi.dna.core.def.exp;

/**
 * 联合表达式
 * 
 * @author gaojingxin
 * 
 */
public interface CombinedExpression extends ConditionalExpression,
		Iterable<ConditionalExpression> {

	/**
	 * 返回是否是与联合
	 * 
	 * @return 返回是否是与联合
	 */
	public boolean isAnd();

	/**
	 * 获得条件表达式的个数
	 * 
	 * @return
	 */
	public int getCount();

	/**
	 * 获得条件表达式的个数
	 * 
	 * @return
	 */
	public int size();

	/**
	 * 获取指定的条件表达式
	 * 
	 * @param index
	 * @return
	 */
	public ConditionalExpression get(int index);
}