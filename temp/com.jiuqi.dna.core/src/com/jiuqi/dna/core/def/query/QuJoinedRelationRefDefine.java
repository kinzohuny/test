package com.jiuqi.dna.core.def.query;

/**
 * 查询定义中使用的连接的关系引用.
 * 
 * <p>
 * 和TableReferenceDefine组合为QuJoinedTableRefDefine;
 * 和QueryReferenceDefine组合为QuJoinedQueryRefDefine.
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
