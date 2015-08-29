package com.jiuqi.dna.core.def.query;

/**
 * 查询定义中使用的连接的关系引用
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
