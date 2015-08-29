/**
 * Copyright (C) 2007-2008 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File CategoryContextAdapter.java
 * Date 2008-11-19
 */
package com.jiuqi.dna.core.impl;

import java.util.Comparator;
import java.util.List;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.TreeNode;
import com.jiuqi.dna.core.TreeNodeFilter;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.DeadLockException;
import com.jiuqi.dna.core.exception.DisposedException;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.Cache.CustomGroupSpace;
import com.jiuqi.dna.core.impl.SessionImpl.SessionCacheGroupContainer;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.resource.CategorialResourceModifier;
import com.jiuqi.dna.core.resource.ResourceHandle;
import com.jiuqi.dna.core.resource.ResourceService.WhenExists;
import com.jiuqi.dna.core.resource.ResourceToken;

final class CategorialResContextAdapter<TFacadeM, TImplM extends TFacadeM, TKeysHolderM>
		implements CategorialResourceModifier<TFacadeM, TImplM, TKeysHolderM> {

	public final boolean isValid() {
		return this.context.isValid();
	}

	public final void checkValid() {
		this.context.checkValid();
	}

	final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context;

	private CustomGroupSpace cacheGroupSpace;

	private Object category;

	private <TFacade> CacheGroup<TFacade, ?, ?> getCacheGroup(
			final Class<TFacade> facadeClass) {
		final CacheGroup<TFacade, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group == null) {
			throw new NotFoundGroupException(facadeClass, this.category);
		} else {
			return group;
		}
	}

	@SuppressWarnings("unchecked")
	<TFacade> CacheGroup<TFacade, ?, ?> findCacheGroup(
			final Class<TFacade> facadeClass) {
		final Cache cache = this.context.occorAt.site.cache;
		final CacheDefine<?, ?, ?> define = cache.getDefine(facadeClass);
		final CacheGroup<?, ?, ?> group;
		final Transaction transaction = this.context.transaction;
		if (define.kind.inSession) {
			final SessionCacheGroupContainer sessionGroupContainer = this.context.session.tryGetCacheGroupContainer();
			if (sessionGroupContainer != null) {
				group = sessionGroupContainer.findGroup(facadeClass, this.category, transaction);
			} else {
				return null;
			}
		} else {
			if (this.cacheGroupSpace == null || this.cacheGroupSpace.notBelong(cache)) {
				this.cacheGroupSpace = cache.findSpace(this.category);
				if (this.cacheGroupSpace == null) {
					return null;
				}
			}
			group = this.cacheGroupSpace.findGroup(facadeClass, transaction);
		}
		return group == null ? null : (CacheGroup<TFacade, ?, ?>) group;
	}

	CategorialResContextAdapter(
			ContextImpl<TFacadeM, TImplM, TKeysHolderM> context, Object category) {
		this.context = context;
		this.category = category;
	}

	public final Object getCategory() {
		return this.category;
	}

	public void setCategory(Object category) {
		if (category == null) {
			throw new NullArgumentException("category");
		}
		this.category = category;
	}

	public final <TFacade> void ensureResourceInited(Class<TFacade> facadeClass) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		this.getCacheGroup(facadeClass).ensureInitialized(this.context.transaction);
	}

	/* --------------------- Query Methods -------------------------- */

	public <TFacade> TFacade get(Class<TFacade> facadeClass)
			throws UnsupportedOperationException, MissingObjectException {
		return this.internalGetResourceToken(facadeClass, null, null, null, null, null, null).tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetResourceToken(facadeClass, key.getClass(), null, null, key, null, null).tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException,
			MissingObjectException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetResourceToken(facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null).tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException,
			MissingObjectException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetResourceToken(facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3).tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException, MissingObjectException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException,
			MissingObjectException {
		return this.internalGetResourceToken(operation, facadeClass, null, null, null, null, null, null).tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetResourceToken(operation, facadeClass, key.getClass(), null, null, key, null, null).tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException, MissingObjectException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null).tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException, MissingObjectException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3).tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... keys) throws UnsupportedOperationException,
			MissingObjectException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		CacheHolder<TFacade, ?, ?> res = this.internalFindResourceToken(facadeClass, null, null, null, null, null, null);
		return res == null ? null : res.tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		CacheHolder<TFacade, ?, ?> res = this.internalFindResourceToken(facadeClass, key.getClass(), null, null, key, null, null);
		return res == null ? null : res.tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		CacheHolder<TFacade, ?, ?> res = this.internalFindResourceToken(facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
		return res == null ? null : res.tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		CacheHolder<TFacade, ?, ?> res = this.internalFindResourceToken(facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
		return res == null ? null : res.tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException {
		CacheHolder<TFacade, ?, ?> res = this.internalFindResourceToken(operation, facadeClass, null, null, null, null, null, null);
		return res == null ? null : res.tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		CacheHolder<TFacade, ?, ?> res = this.internalFindResourceToken(operation, facadeClass, key.getClass(), null, null, key, null, null);
		return res == null ? null : res.tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		CacheHolder<TFacade, ?, ?> res = this.internalFindResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
		return res == null ? null : res.tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		CacheHolder<TFacade, ?, ?> res = this.internalFindResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
		return res == null ? null : res.tryGetFacade(this.context.transaction);
	}

	public <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... keys) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass) throws MissingObjectException {
		return this.internalGetResourceToken(facadeClass, null, null, null, null, null, null);
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key)
			throws MissingObjectException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetResourceToken(facadeClass, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws MissingObjectException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetResourceToken(facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws MissingObjectException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetResourceToken(facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws MissingObjectException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws MissingObjectException {
		return this.internalGetResourceToken(operation, facadeClass, null, null, null, null, null, null);
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws MissingObjectException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetResourceToken(operation, facadeClass, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws MissingObjectException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws MissingObjectException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws MissingObjectException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass) {
		return this.internalFindResourceToken(facadeClass, null, null, null, null, null, null);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass, Object key) {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalFindResourceToken(facadeClass, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2) {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalFindResourceToken(facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3) {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalFindResourceToken(facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass) {
		return this.internalFindResourceToken(operation, facadeClass, null, null, null, null, null, null);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalFindResourceToken(operation, facadeClass, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalFindResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3) {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalFindResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	private final <TFacade> CacheHolder<TFacade, ?, ?> internalGetResourceToken(
			final Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class,
			final Object key1, final Object key2, final Object key3) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		final CacheHolderIndex<TFacade, ?, ?> itemIndex = this.getCacheGroup(facadeClass).getIndex(key1Class, key2Class, key3Class);
		final CacheHolder<TFacade, ?, ?> result = itemIndex.findHolder(key1, key2, key3, this.context.transaction);
		if (result != null) {
			return result;
		}
		throw new MissingObjectException("找不到[" + facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3 + "]对象");
	}

	final <TFacade> CacheHolder<TFacade, ?, ?> internalFindResourceToken(
			final Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class,
			final Object key1, final Object key2, final Object key3) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		final CacheHolderIndex<TFacade, ?, ?> itemIndex = this.getCacheGroup(facadeClass).getIndex(key1Class, key2Class, key3Class);
		return itemIndex.findHolder(key1, key2, key3, this.context.transaction);
	}

	private final <TFacade> CacheHolder<TFacade, ?, ?> internalGetResourceToken(
			final Operation<? super TFacade> operation,
			final Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class,
			final Object key1, final Object key2, final Object key3) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		final ContextImpl<?, ?, ?> context = this.context;
		final CacheGroup<TFacade, ?, ?> group = this.getCacheGroup(facadeClass);
		final CacheHolderIndex<TFacade, ?, ?> itemIndex = group.getIndex(key1Class, key2Class, key3Class);
		final CacheHolder<TFacade, ?, ?> result;
		final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
		resourceService.callBeforeAccessAuthorityResource(context);
		try {
			result = itemIndex.findHolder(context.getOperationAuthorityChecker(), operation, key1, key2, key3, context.transaction);
		} finally {
			resourceService.callEndAccessAuthorityResource(context);
		}
		if (result != null) {
			return result;
		}
		throw new MissingObjectException("找不到[" + facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3 + "]对象");
	}

	private final <TFacade> CacheHolder<TFacade, ?, ?> internalFindResourceToken(
			final Operation<? super TFacade> operation,
			final Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class,
			final Object key1, final Object key2, final Object key3) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		final CacheGroup<TFacade, ?, ?> group = this.getCacheGroup(facadeClass);
		final CacheHolderIndex<TFacade, ?, ?> itemIndex = group.getIndex(key1Class, key2Class, key3Class);
		final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
		resourceService.callBeforeAccessAuthorityResource(this.context);
		try {
			return itemIndex.findHolder(this.context.getOperationAuthorityChecker(), operation, key1, key2, key3, this.context.transaction);
		} finally {
			resourceService.callEndAccessAuthorityResource(this.context);
		}
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		return this.internalFillResourceList(facadeClass, null, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalFillResourceList(facadeClass, filter, null);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalFillResourceList(facadeClass, null, sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalFillResourceList(facadeClass, filter, sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final <TFacade> List<TFacade> internalFillResourceList(
			Class<TFacade> facadeClass, Filter<? super TFacade> filter,
			Comparator<? super TFacade> comparator) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		this.context.checkValid();
		final CacheGroup group = this.getCacheGroup(facadeClass);
		final List<TFacade> result = group.tryGetValueList(this.context.transaction, filter, comparator);
		if (result == null) {
			throw new DisposedException("缓存组已被销毁。");
		} else {
			return result;
		}
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		return this.internalFillResourceList(operation, facadeClass, null, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalFillResourceList(operation, facadeClass, filter, null);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalFillResourceList(operation, facadeClass, null, sortComparator);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalFillResourceList(operation, facadeClass, filter, sortComparator);
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final <TFacade> List<TFacade> internalFillResourceList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> comparator) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		final ContextImpl<?, ?, ?> context = this.context;
		context.checkValid();
		final CacheGroup group = this.getCacheGroup(facadeClass);
		final List<TFacade> result;
		final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
		resourceService.callBeforeAccessAuthorityResource(context);
		try {
			result = group.tryGetValueList(context.getOperationAuthorityChecker(), operation, context.transaction, filter, comparator);
		} finally {
			resourceService.callEndAccessAuthorityResource(context);
		}
		if (result == null) {
			throw new DisposedException("缓存组已被销毁。");
		} else {
			return result;
		}
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		return this.internalGetTreeNode(null, facadeClass, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, null, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetTreeNode(null, facadeClass, null, null, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, null, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetTreeNode(null, facadeClass, null, sortComparator, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, sortComparator, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetTreeNode(null, facadeClass, null, null, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, null, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetTreeNode(null, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetTreeNode(null, facadeClass, null, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetTreeNode(null, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, null, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, null, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, null, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, null, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, sortComparator, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, sortComparator, key.getClass(), null, null, key, null, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	private final <TFacade> TreeNodeImpl<TFacade> internalGetTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		this.checkValid();
		final CacheGroup<TFacade, ?, ?> group = this.findCacheGroup(facadeClass);
		final ContextImpl<?, ?, ?> context = this.context;
		if (group != null) {
			final TreeNodeImpl<TFacade> root;
			final CacheTree<TFacade, ?, ?> tree = group.getBindTree();
			if (operation == null) {
				root = tree.tryGetTreeValue(filter, sortComparator, context.transaction);
			} else {
				final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
				resourceService.callBeforeAccessAuthorityResource(this.context);
				try {
					root = tree.tryGetTreeValue(context.getOperationAuthorityChecker(), operation, filter, sortComparator, context.transaction);
				} finally {
					resourceService.callEndAccessAuthorityResource(this.context);
				}
			}
			return root;
		} else {
			throw new NotFoundGroupException(facadeClass, this.category);
		}
	}

	private final <TFacade> TreeNodeImpl<TFacade> internalGetTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator,
			final Class<?> key1Class, final Class<?> key2Class,
			final Class<?> key3Class, Object key1, Object key2, Object key3) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		this.checkValid();
		final TreeNodeImpl<TFacade> root;
		final CacheGroup<TFacade, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final CacheHolderIndex<TFacade, ?, ?> itemIndex = group.findIndex(key1Class, key2Class, key3Class);
			if (itemIndex != null) {
				final CacheHolder<TFacade, ?, ?> item = itemIndex.findHolder(key1, key2, key3, this.context.transaction);
				if (item != null) {
					final CacheTree<TFacade, ?, ?> tree = group.getBindTree();
					if (operation == null) {
						root = tree.tryGetTreeValue(filter, sortComparator, item, this.context.transaction);
					} else {
						final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
						resourceService.callBeforeAccessAuthorityResource(this.context);
						try {
							root = tree.tryGetTreeValue(this.context.getOperationAuthorityChecker(), operation, filter, sortComparator, item, this.context.transaction);
						} finally {
							resourceService.callEndAccessAuthorityResource(this.context);
						}
					}
				} else {
					root = new TreeNodeImpl<TFacade>(null, null);
				}
				return root;
			} else {
				throw new UnsupportedOperationException("没有定义[" + facadeClass + "]类型的资源服务， 或者相应的服务中没有定义相关键类型的资源提供器");
			}
		} else {
			throw new NotFoundGroupException(facadeClass, this.category);
		}
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass, ResourceToken<THolderFacade> holderToken) {
		return this.context.getResourceReferences(facadeClass, holderToken);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		return this.context.getResourceReferences(facadeClass, holderToken, filter);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		return this.context.getResourceReferences(facadeClass, holderToken, sortComparator);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		return this.context.getResourceReferences(facadeClass, holderToken, filter, sortComparator);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken) {
		return this.context.getResourceReferences(operation, facadeClass, holderToken);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		return this.context.getResourceReferences(operation, facadeClass, holderToken, filter);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		return this.context.getResourceReferences(operation, facadeClass, holderToken, sortComparator);
	}

	public <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		return this.context.getResourceReferences(operation, facadeClass, holderToken, filter, sortComparator);
	}

	public <TFacade> ResourceHandle<TFacade> lockResourceS(
			ResourceToken<TFacade> resourceToken) {
		return this.context.lockResourceS(resourceToken);
	}

	public <TFacade> ResourceHandle<TFacade> lockResourceU(
			ResourceToken<TFacade> resourceToken) {
		return this.context.lockResourceU(resourceToken);
	}

	/* --------------------- Update Methods -------------------------- */

	/**
	 * 克隆资源
	 * 
	 * @param tryReuse
	 *            尝试被重用的实例（减少对象创建成本）
	 */
	public final TImplM cloneResource(ResourceToken<TFacadeM> token,
			TImplM tryReuse) {
		return this.context.cloneResource(token, tryReuse);
	}

	/**
	 * 克隆资源
	 */
	public final TImplM cloneResource(ResourceToken<TFacadeM> token) {
		return this.context.cloneResource(token);
	}

	public TImplM modifyResource() throws DeadLockException {
		return this.internalModifyResource(null, null, null, null, null, null);
	}

	public <TKey> TImplM modifyResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalModifyResource(key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> TImplM modifyResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalModifyResource(key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalModifyResource(key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	public TImplM modifyResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalModifyResource(operation, null, null, null, null, null, null);
	}

	public <TKey> TImplM modifyResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalModifyResource(operation, key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalModifyResource(operation, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalModifyResource(operation, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	private final TImplM internalModifyResource(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3) {
		final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context = this.context;
		final Class<TFacadeM> facadeClass = context.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			// DIST
			this.context.checkCacheModifiable(group.define, group.ownSpace.identifier);
			final Object value = group.localTryModifyHolder(keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, context.transaction);
			if (value != null) {
				return (TImplM) value;
			}
		}
		throw new MissingObjectException(ServiceInvokeeBase.noResourceException(facadeClass, keyValue1, keyValue2, keyValue3, null).getMessage());
	}

	@SuppressWarnings("unchecked")
	private final TImplM internalModifyResource(
			final Operation<? super TFacadeM> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3) {
		final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context = this.context;
		final Class<TFacadeM> facadeClass = context.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final Object value;
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			// DIST
			this.context.checkCacheModifiable(group.define, group.ownSpace.identifier);
			resourceService.callBeforeAccessAuthorityResource(context);
			try {
				value = group.localTryModifyHolder(context.getOperationAuthorityChecker(), operation, keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, context.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(context);
			}
			if (value != null) {
				return (TImplM) value;
			}
		}
		throw new MissingObjectException(ServiceInvokeeBase.noResourceException(facadeClass, keyValue1, keyValue2, keyValue3, null).getMessage());
	}

	public void postModifiedResource(TImplM modifiedResource) {
		this.context.postModifiedResource(modifiedResource);
	}

	public TImplM removeResource() throws DeadLockException {
		return this.internalRemoveResource(null, null, null, null, null, null);
	}

	public <TKey> TImplM removeResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalRemoveResource(key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> TImplM removeResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalRemoveResource(key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalRemoveResource(key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	public TImplM removeResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalRemoveResource(operation, null, null, null, null, null, null);
	}

	public <TKey> TImplM removeResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalRemoveResource(operation, key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalRemoveResource(operation, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalRemoveResource(operation, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	private final TImplM internalRemoveResource(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3) {
		final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context = this.context;
		final Class<TFacadeM> facadeClass = context.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			// DIST
			this.context.checkCacheModifiable(group.define, group.ownSpace.identifier);
			final Object value = group.localTryRemoveHolder(keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, context.transaction);
			if (value != null) {
				return (TImplM) value;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private final TImplM internalRemoveResource(
			final Operation<? super TFacadeM> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3) {
		final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context = this.context;
		final Class<TFacadeM> facadeClass = context.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final Object value;
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			// DIST
			this.context.checkCacheModifiable(group.define, group.ownSpace.identifier);
			resourceService.callBeforeAccessAuthorityResource(context);
			try {
				value = group.localTryRemoveHolder(context.getOperationAuthorityChecker(), operation, keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, context.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(context);
			}
			if (value != null) {
				return (TImplM) value;
			}
		}
		return null;
	}

	public void invalidResource() throws DeadLockException {
		this.internalInvalidResource(null, null, null, null, null, null);
	}

	public <TKey> void invalidResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.internalInvalidResource(key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> void invalidResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		this.internalInvalidResource(key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.internalInvalidResource(key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	public void invalidResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		this.internalInvalidResource(operation, null, null, null, null, null, null);
	}

	public <TKey> void invalidResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.internalInvalidResource(operation, key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		this.internalInvalidResource(operation, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.internalInvalidResource(operation, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	private final void internalInvalidResource(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3) {
		final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context = this.context;
		final Class<TFacadeM> facadeClass = context.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			group.localTryInvalidHolder(keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, context.transaction);
		}
	}

	private final void internalInvalidResource(
			final Operation<? super TFacadeM> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3) {
		final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context = this.context;
		final Class<TFacadeM> facadeClass = context.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			resourceService.callBeforeAccessAuthorityResource(context);
			try {
				group.localTryInvalidHolder(context.getOperationAuthorityChecker(), operation, keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, context.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(context);
			}
		}
	}

	public void reloadResource() throws DeadLockException {
		this.internalReloadResource(null, null, null, null, null, null);
	}

	public <TKey> void reloadResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.internalReloadResource(key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> void reloadResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		this.internalReloadResource(key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> void reloadResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.internalReloadResource(key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> void reloadResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	public void reloadResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		this.internalReloadResource(operation, null, null, null, null, null, null);
	}

	public <TKey> void reloadResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.internalReloadResource(operation, key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> void reloadResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		this.internalReloadResource(operation, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> void reloadResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.internalReloadResource(operation, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> void reloadResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	private final void internalReloadResource(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3) {
		final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context = this.context;
		final Class<TFacadeM> facadeClass = context.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			group.localTryReloadHolder(keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, context.transaction);
		}
	}

	private final void internalReloadResource(
			final Operation<? super TFacadeM> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3) {
		final ContextImpl<TFacadeM, TImplM, TKeysHolderM> context = this.context;
		final Class<TFacadeM> facadeClass = context.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			resourceService.callBeforeAccessAuthorityResource(context);
			try {
				group.localTryReloadHolder(context.getOperationAuthorityChecker(), operation, keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, context.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(context);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ResourceToken<TFacadeM> putResource(TImplM resource) {
		return this.putResource(resource, (TKeysHolderM) resource, WhenExists.REPLACE);
	}

	public ResourceToken<TFacadeM> putResource(TImplM resource,
			TKeysHolderM keys) {
		return this.putResource(resource, keys, WhenExists.REPLACE);
	}

	@SuppressWarnings("unchecked")
	public ResourceToken<TFacadeM> putResource(TImplM resource,
			TKeysHolderM keys, WhenExists policy) {
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (keys == null) {
			throw new NullArgumentException("keys");
		}
		if (policy == null) {
			throw new NullArgumentException("policy");
		}
		final CacheGroup<TFacadeM, TImplM, TKeysHolderM> group = (CacheGroup<TFacadeM, TImplM, TKeysHolderM>) this.getCacheGroup(this.context.getFacadeClass());
		// DIST
		if (group.define.resourceService.isFilterExcluded(this.context, resource)) {
			return null;
		}
		this.context.checkCacheModifiable(group.define, group.ownSpace.identifier);
		return group.localTryCreateHolder(resource, keys, CacheDefine.WhenExistPolicyPutPolicyTranslator.toPutPolicy(policy), this.context.transaction);
	}

	@SuppressWarnings("unchecked")
	public ResourceToken<TFacadeM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource) {
		return this.putResource(treeParent, resource, (TKeysHolderM) resource, WhenExists.REPLACE);
	}

	public ResourceToken<TFacadeM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource,
			TKeysHolderM keys) {
		return this.putResource(treeParent, resource, keys, WhenExists.REPLACE);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResourceToken<TFacadeM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource,
			TKeysHolderM keys, WhenExists policy) {
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (keys == null) {
			throw new NullArgumentException("keys");
		}
		if (policy == null) {
			throw new NullArgumentException("policy");
		}
		final ContextImpl<?, ?, ?> context = this.context;
		final CacheGroup<TFacadeM, TImplM, TKeysHolderM> group = (CacheGroup<TFacadeM, TImplM, TKeysHolderM>) this.getCacheGroup(context.getFacadeClass());
		// DIST
		if (group.define.resourceService.isFilterExcluded(this.context, resource)) {
			return null;
		}
		this.context.checkCacheModifiable(group.define, group.ownSpace.identifier);
		final CacheHolder<TFacadeM, TImplM, TKeysHolderM> item = group.localTryCreateHolder(resource, keys, CacheDefine.WhenExistPolicyPutPolicyTranslator.toPutPolicy(policy), context.transaction);
		if (item != null) {
			final CacheTree tree = group.getBindTree();
			tree.localTryCreateNode(treeParent == null ? null : (CacheHolder<TFacadeM, ?, ?>) treeParent, item, context.transaction);
			return item;
		}
		throw new DisposedException("外观类型为[" + context.getFacadeClass() + "]，标识为[" + this.category + "]的缓存组已被销毁。");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void putResource(ResourceToken<TFacadeM> treeParent,
			ResourceToken<TFacadeM> child) {
		if (child == null) {
			throw new NullArgumentException("child");
		}
		final ContextImpl<?, ?, ?> context = this.context;
		context.checkValid();
		final CacheTree tree = this.getCacheGroup(context.getFacadeClass()).getBindTree();
		tree.localTryCreateNode((CacheHolder<TFacadeM, ?, ?>) treeParent, (CacheHolder<TFacadeM, ?, ?>) child, context.transaction);
	}

	public <THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		this.context.putResourceReference(holder, reference);
	}

	public <TReferenceFacade> void putResourceReferenceBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		this.context.putResourceReferenceBy(holder, reference);
	}

	public final <THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		this.context.removeResourceReference(holder, reference);
	}

	public final <TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		this.context.removeResourceReferenceBy(holder, reference);
	}

	// ---------------------------------以下权限相关-----------------------------------------------------

	public TImplM cloneResource(Operation<? super TFacadeM> operation,
			ResourceToken<TFacadeM> token) {
		return this.context.cloneResource(operation, token);
	}

	public TImplM cloneResource(Operation<? super TFacadeM> operation,
			ResourceToken<TFacadeM> token, TImplM tryReuse) {
		return this.context.cloneResource(operation, token, tryReuse);
	}

	public <THolderFacade> void removeResourceReference(
			Operation<? super TFacadeM> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		this.context.removeResourceReference(operation, holder, reference);
	}

	public final <TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		this.context.removeResourceReferenceBy(operation, holder, reference);
	}

	// ---------------------------------以上权限相关-----------------------------------------------------

}
