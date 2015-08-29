package com.jiuqi.dna.core.def.model;

/**
 * 列表属性
 * 
 * @author gaojingxin
 * 
 */
public interface ListPropertyValue extends Iterable<Object> {
	/**
	 * 列表大小
	 */
	public int size();

	/**
	 * 获取某元素
	 * 
	 * @param index
	 *            位置
	 */
	public Object get(int index);
}
