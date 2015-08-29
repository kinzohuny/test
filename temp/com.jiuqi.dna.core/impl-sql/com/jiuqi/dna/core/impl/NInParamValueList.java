package com.jiuqi.dna.core.impl;


/**
 * IN表达式值列表
 * 
 * @author niuhaifeng
 * 
 */
class NInParamValueList extends NInExprParam {
	public final NValueExpr[] values;

	public NInParamValueList(Token start, Token end, NValueExpr[] values) {
		super(start, end);
		this.values = values;
	}
}
