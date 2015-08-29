package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;

/**
 * ������䶨��
 * 
 * @see com.jiuqi.dna.core.def.query.ModifyStatementDefine
 * 
 * @author houchunlei
 */
public interface ModifyStatementDeclare extends ModifyStatementDefine,
		StatementDeclare, RelationRefDomainDeclare {

	/**
	 * �����Ӳ�ѯ����
	 * 
	 * @return
	 */
	public SubQueryDeclare newSubQuery();

	/**
	 * ���쵼����ѯ����,����from�Ӿ�ʹ��
	 * 
	 * @return
	 */
	public DerivedQueryDeclare newDerivedQuery();

	/**
	 * �����ֶ����ñ��ʽ
	 * 
	 * @param field
	 * @return
	 */
	public TableFieldRefExpr expOf(RelationColumnDefine column);

	/**
	 * �����ֶ����ñ��ʽ
	 * 
	 * @param columnName
	 * @return
	 */
	public TableFieldRefExpr expOf(String columnName);
}
