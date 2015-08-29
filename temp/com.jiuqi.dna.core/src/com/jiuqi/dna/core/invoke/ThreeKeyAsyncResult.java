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
public interface ThreeKeyAsyncResult<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyAsyncResult<TResult, TKey1, TKey2>,
		ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> {
	/**
	 * 获得该查询的第三个键
	 * 
	 * @return 返回第三个键
	 */
	public TKey3 getKey3();
}
