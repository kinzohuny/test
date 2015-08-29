package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.QueryReferenceDeclare;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;

/**
 * 查询引用的内部接口
 * 
 * @author houchunlei
 * 
 */
public interface QueryRef extends RelationRef, QueryReferenceDeclare {

	public SelectImpl<?, ?> getTarget();

	public SelectColumnRefImpl expOf(RelationColumnDefine column);

	public SelectColumnRefImpl expOf(String columnName);
}