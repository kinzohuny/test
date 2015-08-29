package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.table.TableJoinType;

/**
 * ���ӵĹ�ϵ����
 * 
 * @see com.jiuqi.dna.core.def.query.JoinedRelationRefDefine
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings("deprecation")
public interface JoinedRelationRefDeclare extends JoinedRelationRefDefine,
		RelationRefDeclare, MoJoinedRelationRefDeclare {

	/**
	 * ������������
	 * 
	 * @param condition
	 */
	public void setJoinCondition(ConditionalExpression condition);

	/**
	 * ������������
	 * 
	 * @param type
	 */
	public void setJoinType(TableJoinType type);
}
