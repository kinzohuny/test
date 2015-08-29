package com.jiuqi.dna.core.invoke;

import java.util.List;

/**
 * 异步查询列表的句柄
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */

@SuppressWarnings("deprecation")
public interface AsyncListResult<TResult> extends AsyncHandle,
		AsyncResultList<TResult> {
	/**
	 * 获得执行完后的结果列表
	 * 
	 * @return 返回结果列表
	 * @throws IllegalStateException
	 *             如果结果还未返回，则抛出该异常
	 */
	public List<TResult> getResultList() throws IllegalStateException;

	/**
	 * 获得请求结果的类
	 * 
	 * @return 返回请求结果的类
	 */
	public Class<TResult> getResultClass();
}
