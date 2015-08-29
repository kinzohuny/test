package com.jiuqi.dna.core;

/**
 * 外部过滤器，提供带上下文环境的过滤
 * 
 * @author gaojingxin
 * 
 * @param <TItem>
 */
public interface HeavyFilter<TItem> extends Filter<TItem> {
	/**
	 * 判断过滤器是否接受某项
	 * 
	 * @return 返回过滤器是否接受某项
	 */
	public boolean accept(Context context, TItem item);
}
