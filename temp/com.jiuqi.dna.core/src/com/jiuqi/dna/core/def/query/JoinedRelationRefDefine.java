package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.table.TableJoinType;

/**
 * 连接的关系引用
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings("deprecation")
public interface JoinedRelationRefDefine extends RelationRefDefine,
		MoJoinedRelationRefDefine {

	/**
	 * 获取连接条件
	 * 
	 * @return
	 */
	public ConditionalExpression getJoinCondition();

	/**
	 * 获取连接类型
	 * 
	 * @return
	 */
	public TableJoinType getJoinType();
}
