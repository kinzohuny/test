package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.impl.SetOperatorImpl;

/**
 * 集合运算
 * 
 * @author houchunlei
 * 
 */
public interface SetOperator {

	/**
	 * 集合与
	 */
	public static final SetOperator UNION = SetOperatorImpl.UNION;

	/**
	 * 集合与
	 */
	public static final SetOperator UNION_ALL = SetOperatorImpl.UNION_ALL;

	/**
	 * 集合交
	 */
	@Deprecated
	public static final SetOperator INTERSECT = SetOperatorImpl.INTERSECT;

	/**
	 * 集合差
	 */
	@Deprecated
	public static final SetOperator DIFFERENCE = SetOperatorImpl.DIFFERENCE;
}
