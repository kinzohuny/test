package com.jiuqi.dna.core.def.table;

/**
 * 等值表关系
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
	 * 设置本表的等值关系字段
	 */
	public void setSelfField(TableFieldDefine selfField);

	/**
	 * 设置目标表的等值关系字段
	 */
	public void setTargetField(TableFieldDefine targetField);
}