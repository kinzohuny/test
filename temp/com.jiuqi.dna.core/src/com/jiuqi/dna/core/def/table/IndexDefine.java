package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.ModifiableContainer;
import com.jiuqi.dna.core.def.NamedDefine;

/**
 * 物理表索引定义
 * 
 * @author gaojingxin
 * 
 */
public interface IndexDefine extends NamedDefine {

	/**
	 * 表定义
	 * 
	 * @return
	 */
	public TableDefine getOwner();

	/**
	 * 是否是唯一索引
	 * 
	 * @return
	 */
	public boolean isUnique();

	/**
	 * 索引类型
	 * 
	 * @return
	 */
	public IndexType getType();

	/**
	 * 返回索引组合字段的枚举器
	 * 
	 * @return 返回列的迭代器
	 */
	public ModifiableContainer<? extends IndexItemDefine> getItems();
}