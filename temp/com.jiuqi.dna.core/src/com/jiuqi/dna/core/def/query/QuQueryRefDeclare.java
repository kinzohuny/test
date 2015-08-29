package com.jiuqi.dna.core.def.query;

/**
 * 查询定义中使用的查询引用定义
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
