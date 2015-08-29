package com.jiuqi.dna.core.invoke;

/**
 * 双键异步查询列表的句柄
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@SuppressWarnings("deprecation")
public interface TwoKeyAsyncListResult<TResult, TKey1, TKey2> extends
		OneKeyAsyncListResult<TResult, TKey1>,
		TwoKeyOverlappedResultList<TResult, TKey1, TKey2> {
	/**
	 * 得到第二个键
	 * 
	 * @return 返回第二个键
	 */
	public TKey2 getKey2();
}
