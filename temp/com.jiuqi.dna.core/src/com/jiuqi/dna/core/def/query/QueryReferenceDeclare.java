package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.SelectColumnRefExpr;

/**
 * ��ѯ���ö���
 * 
 * @author houchunlei
 * 
 */
public interface QueryReferenceDeclare extends QueryReferenceDefine,
		RelationRefDeclare {

	public SelectDeclare getTarget();

	public SelectColumnRefExpr expOf(RelationColumnDefine column);

	public SelectColumnRefExpr expOf(String columnName);

}
