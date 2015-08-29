package com.jiuqi.dna.core.invoke;

/**
 * 单键异步查询列表的句柄，已经废弃，请使用OneKeyAsyncListResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@Deprecated
public interface OneKeyOverlappedResultList<TResult, TKey> extends
		AsyncResultList<TResult> {
	/**
	 * 得到第一个键
	 * 
	 * @return 返回第一个键
	 */
	public TKey getKey1();
}
