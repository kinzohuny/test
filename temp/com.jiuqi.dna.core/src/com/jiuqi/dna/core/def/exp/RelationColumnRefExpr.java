package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.def.query.RelationRefDefine;

/**
 * ��ϵ�����ñ��ʽ
 * 
 * @author houchunlei
 * 
 */
public interface RelationColumnRefExpr extends ValueExpression {

	/**
	 * ��ȡָ��Ĺ�ϵ�ж���
	 * 
	 * @return
	 */
	public RelationColumnDefine getColumn();

	/**
	 * ��ȡ���ڵĹ�ϵ���ö���
	 * 
	 * @return
	 */
	public RelationRefDefine getReference();
}