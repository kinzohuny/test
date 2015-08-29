package com.jiuqi.dna.core.type;
/**
 * 属性值跌代器
 * 
 * @author gaojingxin
 * 
 */
public interface ValueTreeIterator extends ValueIterator {
	/**
	 * 获取当前的级次，-1为空位置，0为第一级
	 * 
	 * @return 返回当前级次
	 */
	public int getLevel();
}
