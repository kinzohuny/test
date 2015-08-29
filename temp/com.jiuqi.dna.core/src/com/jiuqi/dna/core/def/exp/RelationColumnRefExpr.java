package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.def.query.RelationRefDefine;

/**
 * 关系列引用表达式
 * 
 * @author houchunlei
 * 
 */
public interface RelationColumnRefExpr extends ValueExpression {

	/**
	 * 获取指向的关系列定义
	 * 
	 * @return
	 */
	public RelationColumnDefine getColumn();

	/**
	 * 获取所在的关系引用定义
	 * 
	 * @return
	 */
	public RelationRefDefine getReference();
}