package com.jiuqi.dna.core.invoke;

/**
 * 单键异步查询的句柄，已经废弃，请使用OneKeyAsyncResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@Deprecated
public interface OneKeyOverlappedResult<TResult, TKey> extends
		AsyncResult<TResult> {
	/**
	 * 获得该查询的第一个键
	 * 
	 * @return 返回第一个键
	 */
	public TKey getKey1();
}
