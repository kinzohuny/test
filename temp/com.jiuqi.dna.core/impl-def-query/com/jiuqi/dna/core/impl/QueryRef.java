package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.QueryReferenceDeclare;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;

/**
 * ��ѯ���õ��ڲ��ӿ�
 * 
 * @author houchunlei
 * 
 */
public interface QueryRef extends RelationRef, QueryReferenceDeclare {

	public SelectImpl<?, ?> getTarget();

	public SelectColumnRefImpl expOf(RelationColumnDefine column);

	public SelectColumnRefImpl expOf(String columnName);
}