package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.DeclareBase;

/**
 * 可设置的索引字段定义
 * 
 * @author gaojingxin
 * 
 */
public interface IndexItemDeclare extends IndexItemDefine, DeclareBase {

	/**
	 * 设置是否降序
	 * 
	 * @param desc
	 */
	public void setDesc(boolean desc);
}