package com.jiuqi.dna.core.invoke;

/**
 * 双键异步查询的句柄，已经废弃，请使用TwoKeyAsyncResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@Deprecated
public interface TwoKeyOverlappedResult<TResult, TKey1, TKey2> extends
		OneKeyOverlappedResult<TResult, TKey1> {
	/**
	 * 获得该查询的第二个键
	 * 
	 * @return 返回第二个键
	 */
	public TKey2 getKey2();
}
