package com.jiuqi.dna.core.invoke;

/**
 * 双键异步查询列表的句柄，已经废弃，请使用TwoKeyAsyncListResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@Deprecated
public interface TwoKeyOverlappedResultList<TResult, TKey1, TKey2> extends
		OneKeyOverlappedResultList<TResult, TKey1> {
	/**
	 * 得到第二个键
	 * 
	 * @return 返回第二个键
	 */
	public TKey2 getKey2();
}
