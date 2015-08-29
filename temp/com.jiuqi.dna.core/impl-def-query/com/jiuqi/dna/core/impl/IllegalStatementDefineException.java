package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.StatementDefine;

/**
 * 非法的语句定义
 * 
 * @author houchunlei
 * 
 */
public class IllegalStatementDefineException extends RuntimeException {

	private static final long serialVersionUID = 7346139372801976940L;

	public final StatementDefine statement;

	public IllegalStatementDefineException(StatementDefine statement,
			String message) {
		super(message);
		this.statement = statement;
	}

}
