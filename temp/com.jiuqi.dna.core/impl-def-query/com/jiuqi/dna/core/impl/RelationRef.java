package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.def.query.RelationRefDeclare;

/**
 * ��ϵ���õ��ڲ����ӿ�
 * 
 * @see com.jiuqi.dna.core.def.query.RelationRefDefine
 * 
 * @author houchunlei
 * 
 */
public interface RelationRef extends RelationRefDeclare, OMVisitable {

	Relation getTarget();

	RelationColumnRefImpl expOf(RelationColumnDefine column);

	RelationColumnRefImpl expOf(String columnName);

	int modCount();

	void increaseModCount();
}