/**
 * Copyright (C) 2007-2008 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File CategoryResourceMaster.java
 * Date 2008-11-19
 */
package com.jiuqi.dna.core.resource;

import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.DeadLockException;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
public interface ResourceModifier<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ResourceQuerier, ResourcePutter<TFacade, TImpl, TKeysHolder> {
	
	/**
	 * 设置资源对象
	 * 
	 * @param resource
	 *            资源外观
	 * @param keys
	 *            资源键源
	 * @param policy
	 *            资源的添加策略
	 */
	public ResourceToken<TFacade> putResource(TImpl resource, TKeysHolder keys,
			ResourceService.WhenExists policy);

	/**
	 * 设置资源对象
	 * 
	 * @param resource
	 *            资源外观
	 * @param keys
	 *            资源键源
	 * @param policy
	 *            资源的添加策略
	 */
	public ResourceToken<TFacade> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource,
			TKeysHolder keys, ResourceService.WhenExists policy);

	/**
	 * 克隆资源
	 */
	public TImpl cloneResource(ResourceToken<TFacade> token);

	/**
	 * 克隆资源
	 * 
	 * @param tryReuse
	 *            尝试被重用的实例（减少对象创建成本），该值可为空
	 */
	public TImpl cloneResource(ResourceToken<TFacade> token, TImpl tryReuse);

	/**
	 * 修改某资源，（该方法仅用于单例资源）
	 */
	public TImpl modifyResource() throws DeadLockException;

	/**
	 * 修改某资源
	 */
	public <TKey> TImpl modifyResource(TKey key) throws DeadLockException;

	/**
	 * 修改某资源
	 */
	public <TKey1, TKey2> TImpl modifyResource(TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * 修改某资源
	 */
	public <TKey1, TKey2, TKey3> TImpl modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * 修改某资源
	 */
	public <TKey1, TKey2, TKey3> TImpl modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * 使资源的修改生效
	 * 
	 * <p>
	 * 如果当前事务对象持有过多的资源，该方法的性能将下降。
	 * 
	 * @param modifiedResource
	 *            通过<code>modifyResource</code>方法获取到的资源副本。
	 */
	void postModifiedResource(TImpl modifiedResource);

	/**
	 * 删除单例的资源
	 * 
	 * <p>
	 * 该方法仅用于单例资源。
	 */
	public TImpl removeResource() throws DeadLockException;

	/**
	 * 删除以key为键的资源
	 */
	public <TKey> TImpl removeResource(TKey key) throws DeadLockException;

	/**
	 * 删除以key1和key2为键的资源
	 */
	public <TKey1, TKey2> TImpl removeResource(TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * 删除三键资源
	 */
	public <TKey1, TKey2, TKey3> TImpl removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * 删除多键资源
	 */
	public <TKey1, TKey2, TKey3> TImpl removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * 将单例资源置为无效，（该方法仅用于单例资源）
	 * 
	 */
	@Deprecated
	public void invalidResource() throws DeadLockException;

	/**
	 * 将以key为键的资源置为无效
	 * 
	 */
	@Deprecated
	public <TKey> void invalidResource(TKey key) throws DeadLockException;

	/**
	 * 将以key1和key2为键的资源置为无效
	 * 
	 */
	@Deprecated
	public <TKey1, TKey2> void invalidResource(TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * 将三键资源置为无效
	 * 
	 */
	@Deprecated
	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * 将多键资源置为无效
	 * 
	 */
	@Deprecated
	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * 刷新单例资源，如果重新加载资源失败，则删除该资源，（该方法仅用于单例资源）
	 */
	public void reloadResource() throws DeadLockException;

	/**
	 * 刷新资源，如果重新加载资源失败，则删除该资源
	 */
	public <TKey> void reloadResource(TKey key) throws DeadLockException;

	/**
	 * 刷新资源，如果重新加载资源失败，则删除该资源
	 */
	public <TKey1, TKey2> void reloadResource(TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * 刷新资源，如果重新加载资源失败，则删除该资源
	 */
	public <TKey1, TKey2, TKey3> void reloadResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * 刷新资源，如果重新加载资源失败，则删除该资源
	 */
	public <TKey1, TKey2, TKey3> void reloadResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * 克隆资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param token
	 *            资源标识
	 * @return 返回克隆的资源
	 */
	public TImpl cloneResource(Operation<? super TFacade> operation,
			ResourceToken<TFacade> token);

	/**
	 * 克隆资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param token
	 *            资源标识
	 * @param tryReuse
	 *            尝试被重用的实例（减少对象创建成本），该值可为空
	 * @return 返回克隆的资源
	 */
	public TImpl cloneResource(Operation<? super TFacade> operation,
			ResourceToken<TFacade> token, TImpl tryReuse);

	/**
	 * 修改单例的资源，（该方法仅用于单例资源）
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public TImpl modifyResource(Operation<? super TFacade> operation)
			throws DeadLockException;

	/**
	 * 修改某资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public <TKey> TImpl modifyResource(Operation<? super TFacade> operation,
			TKey key) throws DeadLockException;

	/**
	 * 修改某资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public <TKey1, TKey2> TImpl modifyResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * 修改某资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public <TKey1, TKey2, TKey3> TImpl modifyResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * 修改某资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param keys
	 *            其它键
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public <TKey1, TKey2, TKey3> TImpl modifyResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * 删除单例的资源，（该方法仅用于单例资源）
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public TImpl removeResource(Operation<? super TFacade> operation)
			throws DeadLockException;

	/**
	 * 删除某资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public <TKey> TImpl removeResource(Operation<? super TFacade> operation,
			TKey key) throws DeadLockException;

	/**
	 * 删除某资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public <TKey1, TKey2> TImpl removeResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * 删除某资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public <TKey1, TKey2, TKey3> TImpl removeResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * 删除某资源
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param keys
	 *            其它键
	 * @throws DeadLockException
	 *             死锁异常
	 */
	public <TKey1, TKey2, TKey3> TImpl removeResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * 将资源置为无效，（该方法仅用于单例资源）
	 * 
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @throws DeadLockException
	 *             死锁异常
	 */
	@Deprecated
	public void invalidResource(Operation<? super TFacade> operation)
			throws DeadLockException;

	/**
	 * 将资源置为无效<br>
	 * 
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @throws DeadLockException
	 *             死锁异常
	 */
	@Deprecated
	public <TKey> void invalidResource(Operation<? super TFacade> operation,
			TKey key) throws DeadLockException;

	/**
	 * 将资源置为无效<br>
	 * 
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @throws DeadLockException
	 *             死锁异常
	 */
	@Deprecated
	public <TKey1, TKey2> void invalidResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * 将资源置为无效<br>
	 * 
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @throws DeadLockException
	 *             死锁异常
	 */
	@Deprecated
	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * 将资源置为无效<br>
	 * 
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param keys
	 *            其它键
	 * @throws DeadLockException
	 *             死锁异常
	 */
	@Deprecated
	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * 刷新资源，如果重新加载资源失败，则删除该资源，（该方法仅用于单例资源）
	 */
	public void reloadResource(Operation<? super TFacade> operation)
			throws DeadLockException;

	/**
	 * 刷新资源，如果重新加载资源失败，则删除该资源
	 */
	public <TKey> void reloadResource(Operation<? super TFacade> operation,
			TKey key) throws DeadLockException;

	/**
	 * 刷新资源，如果重新加载资源失败，则删除该资源
	 */
	public <TKey1, TKey2> void reloadResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * 刷新资源，如果重新加载资源失败，则删除该资源
	 */
	public <TKey1, TKey2, TKey3> void reloadResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * 刷新资源，如果重新加载资源失败，则删除该资源
	 */
	public <TKey1, TKey2, TKey3> void reloadResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	// -----------------------------------------以下为权限相关---------------------------------------------------

}
