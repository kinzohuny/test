package com.jiuqi.dna.core.def.query;

/**
 * 查询中使用的连接查询引用
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
