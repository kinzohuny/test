package com.jiuqi.dna.core.def.exp;

/**
 * 运算表达式
 * 
 * <p>
 * 表示运算结果为值表达式的运算
 * 
 * @author gaojingxin
 * 
 */
public interface OperateExpression extends ValueExpression,
		Iterable<ValueExpression> {

	/**
	 * 获得操作符
	 * 
	 * @return
	 */
	public Operator getOperator();

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
	 * @return
	 */
	public ValueExpression get(int index);
}