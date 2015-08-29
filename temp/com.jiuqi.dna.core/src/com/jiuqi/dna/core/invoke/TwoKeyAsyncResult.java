package com.jiuqi.dna.core.invoke;

/**
 * 双键异步查询的句柄
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@SuppressWarnings("deprecation")
public interface TwoKeyAsyncResult<TResult, TKey1, TKey2> extends
		OneKeyAsyncResult<TResult, TKey1>,
		TwoKeyOverlappedResult<TResult, TKey1, TKey2> {
	/**
	 * 获得该查询的第二个键
	 * 
	 * @return 返回第二个键
	 */
	public TKey2 getKey2();
}
