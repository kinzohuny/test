package com.jiuqi.dna.core.impl;

/**
 * ��Ϊ���ڵ�Ĺ�ϵ����
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