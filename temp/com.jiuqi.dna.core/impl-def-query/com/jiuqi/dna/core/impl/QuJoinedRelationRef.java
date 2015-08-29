package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.query.QuJoinedRelationRefDeclare;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;

/**
 * ��ѯ������ʹ�õ��������õĻ���
 * 
 * @author houchunlei
 * 
 */
public interface QuJoinedRelationRef extends QuJoinedRelationRefDeclare,
		QuRelationRef, JoinedRelationRef, Iterable<QuJoinedRelationRef> {

	QuJoinedQueryRef castAsQueryRef();

	QuJoinedTableRef castAsTableRef();

	QuRelationRef parent();

	QuJoinedRelationRef next();

	QuJoinedRelationRef last();

	/**
	 * Ŀ���ϵ�������ӵ�ǰΪ��������������,�����ݹ��join��next
	 * 
	 * @param from
	 *            Ŀ���ϵ����
	 * @param args
	 *            ��������
	 */
	void cloneTo(QuRelationRef from, ArgumentableDefine args);

	void render(ISqlRelationRefBuffer buffer, TableUsages usages);
}