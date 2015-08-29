package com.jiuqi.dna.core.def.query;

/**
 * ��ѯ������ʹ�õ����ӵĹ�ϵ����.
 * 
 * <p>
 * ��TableReferenceDefine���ΪQuJoinedTableRefDefine;
 * ��QueryReferenceDefine���ΪQuJoinedQueryRefDefine.
 * 
 * @see com.jiuqi.dna.core.def.query.TableJoinType
 * 
 * @author houchunlei
 * 
 */
public interface QuJoinedRelationRefDefine extends QuRelationRefDefine,
		JoinedRelationRefDefine {

	@Deprecated
	public QuJoinedTableRefDefine castAsTableRef();

	@Deprecated
	public QuJoinedQueryRefDefine castAsQueryRef();
}
