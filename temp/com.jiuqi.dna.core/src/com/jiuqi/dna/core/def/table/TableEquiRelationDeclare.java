package com.jiuqi.dna.core.def.table;

/**
 * ��ֵ���ϵ
 * 
 * @see com.jiuqi.dna.core.def.table.TableEquiRelationDefine
 * 
 * @author houchunlei
 */
@Deprecated
public interface TableEquiRelationDeclare extends TableEquiRelationDefine,
		TableRelationDeclare {

	public TableFieldDeclare getSelfField();

	public TableFieldDeclare getTargetField();

	/**
	 * ���ñ���ĵ�ֵ��ϵ�ֶ�
	 */
	public void setSelfField(TableFieldDefine selfField);

	/**
	 * ����Ŀ���ĵ�ֵ��ϵ�ֶ�
	 */
	public void setTargetField(TableFieldDefine targetField);
}