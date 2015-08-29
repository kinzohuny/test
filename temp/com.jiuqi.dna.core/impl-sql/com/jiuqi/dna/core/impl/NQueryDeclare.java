package com.jiuqi.dna.core.impl;

/**
 * 查询定义节点
 * 
 * @author niuhaifeng
 * 
 */
class NQueryDeclare extends NDmlDeclare {
	public final NQueryStmt body;

	public NQueryDeclare(Token start, Token end, TString name,
			NParamDeclare[] params, NQueryStmt body) {
		super(start, end, name, params);
		this.body = body;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitQueryDeclare(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
