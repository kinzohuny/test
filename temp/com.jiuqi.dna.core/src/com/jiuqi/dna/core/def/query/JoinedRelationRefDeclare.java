package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.table.TableJoinType;

/**
 * 连接的关系引用
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
	 * 设置连接条件
	 * 
	 * @param condition
	 */
	public void setJoinCondition(ConditionalExpression condition);

	/**
	 * 设置连接类型
	 * 
	 * @param type
	 */
	public void setJoinType(TableJoinType type);
}
