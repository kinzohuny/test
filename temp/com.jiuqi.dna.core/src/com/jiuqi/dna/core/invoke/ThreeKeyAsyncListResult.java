package com.jiuqi.dna.core.invoke;

/**
 * 三键异步查询列表的句柄
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@SuppressWarnings("deprecation")
public interface ThreeKeyAsyncListResult<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyAsyncListResult<TResult, TKey1, TKey2>,
		ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> {
	/**
	 * 得到第三个键
	 * 
	 * @return 返回第三个键
	 */
	public TKey3 getKey3();
}
