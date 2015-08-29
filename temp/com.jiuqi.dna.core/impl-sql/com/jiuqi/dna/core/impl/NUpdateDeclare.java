package com.jiuqi.dna.core.impl;

/**
 * UPDATEÉùÃ÷½Úµã
 * 
 * @author niuhaifeng
 * 
 */
class NUpdateDeclare extends NDmlDeclare {
	public final NUpdateStmt body;

	public NUpdateDeclare(Token start, Token end, TString name,
			NParamDeclare[] params, NUpdateStmt body) {
		super(start, end, name, params);
		this.body = body;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitUpdateDeclare(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
