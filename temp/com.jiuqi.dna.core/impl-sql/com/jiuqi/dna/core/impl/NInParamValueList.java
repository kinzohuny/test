package com.jiuqi.dna.core.impl;


/**
 * IN���ʽֵ�б�
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
