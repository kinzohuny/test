package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.NamedDefine;

/**
 * 物理表定义
 * 
 * @author gaojingxin
 * 
 */
public interface DBTableDefine extends NamedDefine {

	/**
	 * 所属表定义
	 */
	public TableDefine getOwner();

	/**
	 * 获得属于该物理表的字段个数
	 */
	public int getFieldCount();

	/**
	 * 获取物理表定义在数据库中的名称
	 * 
	 * @return
	 */
	public String getNameInDB();
	
	/**
	 * 获取物理表类型
	 * 
	 * @return TableType
	 * 			NORMAL（普通表），GLOBAL_TEMPORARY（全局临时表）
	 */
	public TableType getTableType();
}