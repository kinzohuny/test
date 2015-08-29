package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.query.QuRelationRefDefine;

/**
 * 级次谓词表达式
 * 
 * @author houchunlei
 * 
 */
@Deprecated
public interface HierarchyPredicateExpression extends ConditionalExpression {

	/**
	 * 返回谓词
	 */
	@Deprecated
	public HierarchyPredicate getPredicate();

	/**
	 * 返回源表引用
	 */
	@Deprecated
	public QuRelationRefDefine getSource();

	/**
	 * 返回目标表引用
	 */
	@Deprecated
	public QuRelationRefDefine getTarget();

	/**
	 * 返回谓词的级次值
	 */
	@Deprecated
	public ValueExpression getLevel();
}