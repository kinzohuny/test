package com.jiuqi.dna.core.def.query;

/**
 * 查询引用定义
 * 
 * <p>
 * 继承至关系引用定义,表示目标类型为查询定义的关系引用.
 * 
 * @see com.jiuqi.dna.core.def.query.RelationRefDefine
 * 
 * @author houchunlei
 */
public interface QueryReferenceDefine extends RelationRefDefine {

	public SelectDefine getTarget();
}
