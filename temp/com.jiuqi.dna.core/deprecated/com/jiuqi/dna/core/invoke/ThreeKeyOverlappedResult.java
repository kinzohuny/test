package com.jiuqi.dna.core.invoke;

/**
 * 双键异步查询的句柄，已经废弃，请使用ThreeKeyAsyncResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@Deprecated
public interface ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyOverlappedResult<TResult, TKey1, TKey2> {
	/**
	 * 获得该查询的第三个键
	 * 
	 * @return 返回第三个键
	 */
	public TKey3 getKey3();
}
