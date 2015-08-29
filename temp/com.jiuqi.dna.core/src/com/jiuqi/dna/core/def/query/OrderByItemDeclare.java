package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.DeclareBase;

/**
 * 排序项定义
 * 
 * @see com.jiuqi.dna.core.def.query.OrderByItemDefine
 * 
 * @author gaojingxin
 * 
 */
public interface OrderByItemDeclare extends OrderByItemDefine, DeclareBase {

	/**
	 * 设置是否是倒序排列
	 * 
	 * @param value
	 */
	public void setDesc(boolean value);
}
