package com.jiuqi.dna.core.def.query;

/**
 * ������䶨��
 * 
 * @author houchunlei
 * 
 */
public interface InsertStatementDefine extends ModifyStatementDefine {

	/**
	 * ���ض������ֵ�Ĳ�ѯ���
	 * 
	 * @return
	 */
	public DerivedQueryDefine getInsertValues();
}
