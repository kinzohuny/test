package com.jiuqi.dna.core.def.query;

/**
 * ��ѯ������ʹ�õ����ӵĹ�ϵ����
 * 
 * @see com.jiuqi.dna.core.def.query.QuJoinedRelationRefDefine
 * 
 * @author houchunlei
 * 
 */
public interface QuJoinedRelationRefDeclare extends QuJoinedRelationRefDefine,
		QuRelationRefDeclare, JoinedRelationRefDeclare {

	@Deprecated
	public QuJoinedTableRefDeclare castAsTableRef();

	@Deprecated
	public QuJoinedQueryRefDeclare castAsQueryRef();
}
