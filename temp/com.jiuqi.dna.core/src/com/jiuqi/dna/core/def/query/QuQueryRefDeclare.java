package com.jiuqi.dna.core.def.query;

/**
 * ��ѯ������ʹ�õĲ�ѯ���ö���
 * 
 * @see com.jiuqi.dna.core.def.query.QuQueryRefDefine
 * 
 * @author houchunlei
 * 
 */
public interface QuQueryRefDeclare extends QuQueryRefDefine,
		QuRelationRefDeclare, QueryReferenceDeclare {

	public DerivedQueryDeclare getTarget();
}
