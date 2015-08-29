package com.jiuqi.dna.core.service;

import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.invoke.AsyncHandle;
import com.jiuqi.dna.core.invoke.AsyncListResult;
import com.jiuqi.dna.core.invoke.AsyncResult;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.invoke.AsyncTreeNodeResult;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.invoke.OneKeyAsyncListResult;
import com.jiuqi.dna.core.invoke.OneKeyAsyncResult;
import com.jiuqi.dna.core.invoke.OneKeyAsyncTreeNodeResult;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.invoke.ThreeKeyAsyncListResult;
import com.jiuqi.dna.core.invoke.ThreeKeyAsyncResult;
import com.jiuqi.dna.core.invoke.ThreeKeyAsyncTreeNodeResult;
import com.jiuqi.dna.core.invoke.TwoKeyAsyncListResult;
import com.jiuqi.dna.core.invoke.TwoKeyAsyncResult;
import com.jiuqi.dna.core.invoke.TwoKeyAsyncTreeNodeResult;

/**
 * 模块调用器接口
 * 
 * @author gaojingxin
 * 
 */
public interface ServiceInvoker extends SyncServiceInvoker {

	public <TResult> AsyncResult<TResult> asyncGet(Class<TResult> resultClass);

	public <TResult, TKey> OneKeyAsyncResult<TResult, TKey> asyncGet(
			Class<TResult> resultClass, TKey key);

	public <TResult, TKey1, TKey2> TwoKeyAsyncResult<TResult, TKey1, TKey2> asyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2);

	public <TResult, TKey1, TKey2, TKey3> ThreeKeyAsyncResult<TResult, TKey1, TKey2, TKey3> asyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2, TKey3 key3);

	public <TResult> AsyncListResult<TResult> asyncGetList(
			Class<TResult> resultClass);

	public <TResult, TKey1> OneKeyAsyncListResult<TResult, TKey1> asyncGetList(
			Class<TResult> resultClass, TKey1 key1);

	public <TResult, TKey1, TKey2> TwoKeyAsyncListResult<TResult, TKey1, TKey2> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2);

	public <TResult, TKey1, TKey2, TKey3> ThreeKeyAsyncListResult<TResult, TKey1, TKey2, TKey3> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3);

	public <TFacade> AsyncTreeNodeResult<TFacade> asyncGetTreeNode(
			Class<TFacade> resultClass);

	public <TFacade, TKey> OneKeyAsyncTreeNodeResult<TFacade, TKey> asyncGetTreeNode(
			Class<TFacade> facadeClass, TKey key);

	public <TFacade, TKey1, TKey2> TwoKeyAsyncTreeNodeResult<TFacade, TKey1, TKey2> asyncGetTreeNode(
			Class<TFacade> resultClass, TKey1 key1, TKey2 key2);

	public <TFacade, TKey1, TKey2, TKey3> ThreeKeyAsyncTreeNodeResult<TFacade, TKey1, TKey2, TKey3> asyncGetTreeNode(
			Class<TFacade> resultClass, TKey1 key1, TKey2 key2, TKey3 key3);

	public <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method);

	public <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task);

	public <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method, AsyncInfo info);

	public <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task, AsyncInfo info);

	/**
	 * 异步的触发事件
	 * 
	 * <p>
	 * 该方法一经调用马上返回，每个事件处理都拥有独立的事务。
	 * 
	 * @param event
	 *            事件对象
	 */
	public AsyncHandle occur(Event event);

	/**
	 * 同步的触发事件
	 * 
	 * @param event
	 * @param key1
	 * @return
	 */
	public boolean dispatch(Event event, Object key1);

	/**
	 * 同步的触发事件
	 * 
	 * <p>
	 * 该方法等待该事件的全部处理器执行完毕后返回，事件处理与调用者在同一事务中工作。
	 * 
	 * @param event事件对象
	 * @return 返回false表示没有事件响应器
	 */
	public boolean dispatch(Event event);

	/**
	 * 等待异步处理的全部结束
	 */
	public void waitFor(AsyncHandle one, AsyncHandle... others)
			throws InterruptedException;

	/**
	 * 等待异步处理的全部结束
	 * 
	 * @param timeout
	 *            超时毫秒数，0代表永远不超时
	 */
	public void waitFor(long timeout, AsyncHandle one, AsyncHandle... others)
			throws InterruptedException;
}