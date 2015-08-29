package com.jiuqi.dna.core.invoke;

/**
 * 单键异步查询列表的句柄
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */

@SuppressWarnings("deprecation")
public interface OneKeyAsyncListResult<TResult, TKey> extends
		AsyncListResult<TResult>, OneKeyOverlappedResultList<TResult, TKey> {
	/**
	 * 得到第一个键
	 * 
	 * @return 返回第一个键
	 */
	public TKey getKey1();
}
