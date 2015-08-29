package com.jiuqi.dna.core.impl;

/**
 * 作为树节点的关系引用
 * 
 * @author houchunlei
 * 
 */
public interface NodableRelationRef extends RelationRef, RelationJoinableIntrl {

	NodableRelationRef next();

	NodableRelationRef last();

	void setNext(NodableRelationRef next);

	JoinedRelationRef getJoins();
}