package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.query.QueryReferenceDefine;
import com.jiuqi.dna.core.def.query.SelectColumnDefine;

/**
 * ��ѯ�����ñ��ʽ
 * 
 * @author houchunlei
 * 
 */
public interface SelectColumnRefExpr extends RelationColumnRefExpr {

	/**
	 * ��ȡ��ѯ�ж���
	 */
	public SelectColumnDefine getColumn();

	/**
	 * ��ȡ���ڵĲ�ѯ���ö���
	 */
	public QueryReferenceDefine getReference();
}