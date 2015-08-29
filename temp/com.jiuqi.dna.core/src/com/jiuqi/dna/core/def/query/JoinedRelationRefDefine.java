package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.table.TableJoinType;

/**
 * ���ӵĹ�ϵ����
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings("deprecation")
public interface JoinedRelationRefDefine extends RelationRefDefine,
		MoJoinedRelationRefDefine {

	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	public ConditionalExpression getJoinCondition();

	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	public TableJoinType getJoinType();
}
