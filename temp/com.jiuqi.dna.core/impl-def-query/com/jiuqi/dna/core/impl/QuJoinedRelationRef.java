package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.query.QuJoinedRelationRefDeclare;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;

/**
 * 查询定义中使用的连接引用的基类
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
	 * 目标关系引用增加当前为样例的连接引用,包括递归的join及next
	 * 
	 * @param from
	 *            目标关系定义
	 * @param args
	 *            参数容器
	 */
	void cloneTo(QuRelationRef from, ArgumentableDefine args);

	void render(ISqlRelationRefBuffer buffer, TableUsages usages);
}