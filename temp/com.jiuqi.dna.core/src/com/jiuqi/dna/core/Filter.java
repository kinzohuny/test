package com.jiuqi.dna.core;

/**
 * 过滤器
 * 
 * @author gaojingxin
 * 
 * @param <TItem>
 */
public interface Filter<TItem> {

	/**
	 * 判断过滤器是否接受某项
	 * 
	 * @param item
	 * @return 返回过滤器是否接受某项
	 */
	public boolean accept(TItem item);
}
