package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.query.QuRelationRefDefine;

/**
 * ����ν�ʱ��ʽ
 * 
 * @author houchunlei
 * 
 */
@Deprecated
public interface HierarchyPredicateExpression extends ConditionalExpression {

	/**
	 * ����ν��
	 */
	@Deprecated
	public HierarchyPredicate getPredicate();

	/**
	 * ����Դ������
	 */
	@Deprecated
	public QuRelationRefDefine getSource();

	/**
	 * ����Ŀ�������
	 */
	@Deprecated
	public QuRelationRefDefine getTarget();

	/**
	 * ����ν�ʵļ���ֵ
	 */
	@Deprecated
	public ValueExpression getLevel();
}