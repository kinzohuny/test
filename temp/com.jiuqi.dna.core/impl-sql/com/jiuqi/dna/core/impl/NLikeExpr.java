package com.jiuqi.dna.core.impl;


/**
 * LIKE表达式节点
 * 
 * @author niuhaifeng
 * 
 */
class NLikeExpr extends NStrCompareExpr {
	public final NValueExpr escape;

	public NLikeExpr(NValueExpr str, NValueExpr pattern, NValueExpr escape,
			boolean not) {
		super(Keywords.LIKE, str, pattern, not);
		this.escape = escape;
	}

	@Override
	public int endLine() {
		if (this.escape != null)
			return this.escape.endLine();
		return super.endLine();
	}

	@Override
	public int endCol() {
		if (this.escape != null)
			return this.escape.endCol();
		return super.endCol();
	}
}
