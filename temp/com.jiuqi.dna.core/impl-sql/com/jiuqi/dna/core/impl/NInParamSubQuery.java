package com.jiuqi.dna.core.impl;

/**
 * IN���ʽ�ֲ�ѯ
 * 
 * @author niuhaifeng
 * 
 */
class NInParamSubQuery extends NInExprParam {
	public final NQuerySpecific query;

	public NInParamSubQuery(Token start, Token end, NQuerySpecific query) {
		super(start, end);
		this.query = query;
	}
}
