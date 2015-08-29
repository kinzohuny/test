package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.def.exp.RelationColumnRefExpr;

/**
 * 关系引用
 * 
 * @see com.jiuqi.dna.core.def.query.RelationRefDefine
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings({ "deprecation", "unused" })
public interface RelationRefDeclare extends RelationRefDefine,
		RelationJoinable, HierarchyOperatable, NamedDeclare,
		MoRelationRefDeclare {

	public RelationDeclare getTarget();

	/**
	 * 构造关系列引用表达式
	 * 
	 * @param column
	 *            关系列定义
	 * @return
	 */
	public RelationColumnRefExpr expOf(RelationColumnDefine column);

	/**
	 * 构造关系列引用表达式
	 * 
	 * @param columnName
	 *            关系列名称
	 * @return
	 */
	public RelationColumnRefExpr expOf(String columnName);

}
