package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.MoJoinedRelationRefDeclare;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;

/**
 * �������Ĺ�ϵ����
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings({ "deprecation", "unused" })
public interface MoJoinedRelationRef extends MoRelationRef, JoinedRelationRef,
		Iterable<MoJoinedRelationRef>, MoJoinedRelationRefDeclare {

	MoJoinedRelationRef next();

	MoJoinedRelationRef last();

	void render(ISqlRelationRefBuffer buffer, TableUsages usages);
}