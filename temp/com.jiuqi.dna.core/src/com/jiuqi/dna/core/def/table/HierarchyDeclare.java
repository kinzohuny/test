package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * 级次定义
 * 
 * @author gaojingxin
 * 
 */
@Deprecated
public interface HierarchyDeclare extends HierarchyDefine, NamedDeclare {

	/**
	 * 所属表定义
	 */
	public TableDeclare getOwner();

	/**
	 * 设置最大支持的级次
	 */
	public void setMaxLevel(int maxLevel);
}