package com.jiuqi.dna.core.impl;

/**
 * INSERT子查询节点
 * 
 * @author niuhaifeng
 * 
 */
class NInsertSubQuery extends NInsertSource {
	public final NQuerySpecific query;

	public NInsertSubQuery(Token start, Token end, NQuerySpecific query) {
		super(start, end);
		this.query = query;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitInsertSubQuery(visitorContext, this);
	}
}
