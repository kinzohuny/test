package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.RelationColumnRefExpr;

/**
 * �������ʹ�õĹ�ϵ���ö���
 * 
 * @deprecated ʹ��RelationRefDeclare
 * 
 */
@Deprecated
public interface MoRelationRefDeclare extends MoRelationRefDefine,
		RelationJoinable {

	public RelationDeclare getTarget();

	public RelationColumnRefExpr expOf(RelationColumnDefine column);

	public RelationColumnRefExpr expOf(String columnName);

}
