package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.impl.SetOperatorImpl;

/**
 * ��������
 * 
 * @author houchunlei
 * 
 */
public interface SetOperator {

	/**
	 * ������
	 */
	public static final SetOperator UNION = SetOperatorImpl.UNION;

	/**
	 * ������
	 */
	public static final SetOperator UNION_ALL = SetOperatorImpl.UNION_ALL;

	/**
	 * ���Ͻ�
	 */
	@Deprecated
	public static final SetOperator INTERSECT = SetOperatorImpl.INTERSECT;

	/**
	 * ���ϲ�
	 */
	@Deprecated
	public static final SetOperator DIFFERENCE = SetOperatorImpl.DIFFERENCE;
}
