package com.jiuqi.dna.core.def.query;

/**
 * ≤Â»Î”Ôæ‰∂®“Â
 * 
 * @see com.jiuqi.dna.core.def.query.InsertStatementDefine
 * 
 * @author houchunlei
 * 
 */
public interface InsertStatementDeclare extends InsertStatementDefine,
		FieldValueAssignable, ModifyStatementDeclare {

	public DerivedQueryDeclare getInsertValues();

}
