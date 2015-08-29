package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.def.exp.RelationColumnRefExpr;

/**
 * ��ϵ����
 * 
 * @see com.jiuqi.dna.core.def.query.RelationRefDefine
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings({ "deprecation", "unused" })
public interface RelationRefDeclare extends RelationRefDefine,
		RelationJoinable, HierarchyOperatable, NamedDeclare,
		MoRelationRefDeclare {

	public RelationDeclare getTarget();

	/**
	 * �����ϵ�����ñ��ʽ
	 * 
	 * @param column
	 *            ��ϵ�ж���
	 * @return
	 */
	public RelationColumnRefExpr expOf(RelationColumnDefine column);

	/**
	 * �����ϵ�����ñ��ʽ
	 * 
	 * @param columnName
	 *            ��ϵ������
	 * @return
	 */
	public RelationColumnRefExpr expOf(String columnName);

}
