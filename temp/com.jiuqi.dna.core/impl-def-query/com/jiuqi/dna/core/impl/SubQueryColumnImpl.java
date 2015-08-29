package com.jiuqi.dna.core.impl;

/**
 * 子查询对应的查询输出列
 * 
 * @author houchunlei
 * 
 */
final class SubQueryColumnImpl extends
		SelectColumnImpl<SubQueryImpl, SubQueryColumnImpl> {

	SubQueryColumnImpl(SubQueryImpl owner, String name, String alias,
			ValueExpr expr) {
		super(owner, name, alias, expr);
	}
}