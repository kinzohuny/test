package com.jiuqi.dna.core.def.query;

/**
 * ������䶨��
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
