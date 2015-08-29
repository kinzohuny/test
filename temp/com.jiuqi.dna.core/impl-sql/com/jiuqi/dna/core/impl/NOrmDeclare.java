package com.jiuqi.dna.core.impl;

/**
 * ORM�����ڵ�
 * 
 * @author niuhaifeng
 * 
 */
class NOrmDeclare extends NQueryDeclare {
	public final String className;

	public NOrmDeclare(Token start, Token end, TString name,
			NParamDeclare[] params, String className, NQueryStmt body) {
		super(start, end, name, params, body);
		this.className = className;
	}

	@Override
	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitOrmDeclare(visitorContext, this);
	}
}
