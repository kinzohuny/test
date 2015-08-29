package com.jiuqi.dna.core.def.query;

/**
 * 查询定义中使用的查询引用定义
 * 
 * @author houchunlei
 * 
 */
public interface QuQueryRefDefine extends QuRelationRefDefine,
		QueryReferenceDefine {

	public DerivedQueryDefine getTarget();
}
