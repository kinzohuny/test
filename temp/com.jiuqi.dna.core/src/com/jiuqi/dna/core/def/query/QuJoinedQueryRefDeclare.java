package com.jiuqi.dna.core.def.query;

/**
 * ��ѯ��ʹ�õ����Ӳ�ѯ����
 * 
 * @see com.jiuqi.dna.core.def.query.QuJoinedQueryRefDefine
 * 
 * @author houchunlei
 * 
 */
public interface QuJoinedQueryRefDeclare extends QuJoinedQueryRefDefine,
		QuJoinedRelationRefDeclare, QuQueryRefDeclare,
		JoinedQueryReferenceDeclare {

	public DerivedQueryDeclare getTarget();

}
