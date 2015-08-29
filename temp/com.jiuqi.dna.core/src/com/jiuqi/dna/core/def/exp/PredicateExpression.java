package com.jiuqi.dna.core.def.exp;

/**
 * 谓词表达式
 * 
 * <p>
 * 表示运算结果为逻辑值的运算
 * 
 * @author gaojingxin
 * 
 */
public interface PredicateExpression extends ConditionalExpression,
		Iterable<ValueExpression> {

	/**
	 * 获得谓词
	 * 
	 * @return
	 */
	public Predicate getPredicate();

	/**
	 * 获得值表达式的个数
	 * 
	 * @return
	 */
	public int getCount();

	/**
	 * 获得值表达式的个数
	 * 
	 * @return
	 */
	public int size();

	/**
	 * 获取指定的值表达式
	 * 
	 * @param index
	 * 
	 * @return
	 */
	public ValueExpression get(int index);
}