package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.impl.HierarchyOperatorImpl;

/**
 * ���������
 * 
 * @author houchunlei
 * 
 */
@Deprecated
public interface HierarchyOperator {

	/**
	 * ��ȡ������RECID
	 */
	@Deprecated
	public static final HierarchyOperator PARENT_RECID = HierarchyOperatorImpl.PARENT_RECID;

	/**
	 * ��ȡ���n�������Ƚ���RECID
	 */
	@Deprecated
	public static final HierarchyOperator RELATIVE_ANCESTOR_RECID = HierarchyOperatorImpl.RELATIVE_ANCESTOR_RECID;

	/**
	 * ��ȡ�������Ϊn�����Ƚ���RECID
	 */
	@Deprecated
	public static final HierarchyOperator ABUSOLUTE_ANCESTOR_RECID = HierarchyOperatorImpl.ABUSOLUTE_ANCESTOR_RECID;

	/**
	 * ��ȡ���ļ������
	 */
	@Deprecated
	public static final HierarchyOperator LEVEVL_OF = HierarchyOperatorImpl.LEVEVL_OF;
}