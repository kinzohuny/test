package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.query.QuRelationRefDefine;

/**
 * ���κ������ʽ
 * 
 * @author houchunlei
 * 
 */
@Deprecated
public interface HierarchyOperateExpression extends ValueExpression {

	/**
	 * ��ȡ���������
	 */
	@Deprecated
	public HierarchyOperator getOperator();

	/**
	 * ������
	 */
	@Deprecated
	public QuRelationRefDefine getSource();

	/**
	 * ����ֵ����
	 */
	@Deprecated
	public ValueExpression getLevel();
}