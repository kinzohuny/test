package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.JoinedRelationRefDeclare;
import com.jiuqi.dna.core.def.table.TableJoinType;

/**
 * ���ӵĹ�ϵ����
 * 
 * @author Jedicc
 * 
 */
public interface JoinedRelationRef extends NodableRelationRef,
		JoinedRelationRefDeclare {

	ConditionalExpr getJoinCondition();

	TableJoinType getJoinType();

	JoinedRelationRef next();

	JoinedRelationRef last();
}