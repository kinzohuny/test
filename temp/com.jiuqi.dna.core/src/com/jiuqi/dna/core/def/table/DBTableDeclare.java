package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.type.DataType;

/**
 * 物理表定义
 * 
 * @author gaojingxin
 * 
 */
public interface DBTableDeclare extends DBTableDefine, NamedDeclare {

	/**
	 * 所属表定义
	 */
	public TableDeclare getOwner();

	/**
	 * 创建存储在该物理表中的字段
	 */
	public TableFieldDeclare newField(String name, DataType type);
	
	/**
	 * 设置物理表类型，NORMAL（普通表），GLOBAL_TEMPORARY（全局临时表）
	 * 
	 * @param type void
	 */
	public void setTableType(TableType type);
	
	/**
	 * 获取物理表类型
	 * 
	 * @return TableType
	 * 			NORMAL（普通表），GLOBAL_TEMPORARY（全局临时表）
	 */
	public TableType getTableType();
}