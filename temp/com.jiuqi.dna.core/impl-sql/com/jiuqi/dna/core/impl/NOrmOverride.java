package com.jiuqi.dna.core.impl;

/**
 * ORM(OVERRIDE)½Úµã
 * 
 * @author niuhaifeng
 * 
 */
class NOrmOverride extends NOrmDeclare {
	public final TString superName;

	public NOrmOverride(Token start, Token end, TString name,
			NParamDeclare[] params, TString superName, String className,
			NQueryStmt body) {
		super(start, end, name, params, className, body);
		this.superName = superName;
	}

	@Override
	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitOrmOverride(visitorContext, this);
	}
}
