package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * �����õı��ϵ����
 * 
 * @author gaojingxin
 * 
 */
public interface TableRelationDeclare extends TableRelationDefine,
		TableReferenceDeclare {

	/**
	 * ��ù�ϵ��������
	 * 
	 * @return ���ر���
	 */
	public TableDeclare getOwner();

	/**
	 * ������������
	 */
	public void setJoinCondition(ConditionalExpression condition);

	/**
	 * ���ù�ϵ����
	 */
	public void setRelationType(TableRelationType type);

	public TableFieldDeclare getEquiRelationSelfField();

	public TableFieldDeclare getEquiRelationTargetField();

	@Deprecated
	public TableEquiRelationDeclare castAsEquiRelation();
}