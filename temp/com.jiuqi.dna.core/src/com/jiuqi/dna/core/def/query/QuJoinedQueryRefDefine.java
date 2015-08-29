package com.jiuqi.dna.core.def.query;

/**
 * 查询中使用的连接查询引用
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
