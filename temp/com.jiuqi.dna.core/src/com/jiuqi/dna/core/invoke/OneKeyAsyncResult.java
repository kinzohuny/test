package com.jiuqi.dna.core.invoke;

/**
 * 单键异步查询的句柄
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@SuppressWarnings("deprecation")
public interface OneKeyAsyncResult<TResult, TKey> extends AsyncResult<TResult>,
		OneKeyOverlappedResult<TResult, TKey> {
	/**
	 * 获得该查询的第一个键
	 * 
	 * @return 返回第一个键
	 */
	public TKey getKey1();
}
