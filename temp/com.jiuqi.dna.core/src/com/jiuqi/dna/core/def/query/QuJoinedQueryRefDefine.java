package com.jiuqi.dna.core.def.query;

/**
 * ��ѯ��ʹ�õ����Ӳ�ѯ����
 * 
 * @see com.jiuqi.dna.core.def.query.QuJoinedRelationRefDefine
 * 
 * @author houchunlei
 * 
 */
public interface QuJoinedQueryRefDefine extends QuJoinedRelationRefDefine,
		QuQueryRefDefine, JoinedQueryReferenceDefine {

	public DerivedQueryDefine getTarget();

}
