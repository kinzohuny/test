package com.jiuqi.dna.core.invoke;

/**
 * 三键异步查询列表的句柄，已经废弃，请使用ThreeKeyAsyncListResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@Deprecated
public interface ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3>
		extends TwoKeyOverlappedResultList<TResult, TKey1, TKey2> {
	/**
	 * 得到第三个键
	 * 
	 * @return 返回第三个键
	 */
	public TKey3 getKey3();
}
