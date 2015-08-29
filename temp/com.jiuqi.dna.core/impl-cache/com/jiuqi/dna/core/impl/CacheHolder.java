package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.DisposedException;
import com.jiuqi.dna.core.impl.CacheDefine.Provider;
import com.jiuqi.dna.core.impl.CacheDefine.ReferenceDefine;
import com.jiuqi.dna.core.impl.Utils.ObjectAccessor;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.misc.SortUtil;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.resource.ResourceTokenLink;

class CacheHolder<TFacade, TImplement extends TFacade, TKeysHolder> extends
		Acquirable implements ResourceToken<TFacade> {

	final TImplement unsafeGetValue(){
		return this.value;
	}
	
	static final byte STATE_CREATED = 0;

	static final byte STATE_RESOLVED = 1;

	static final byte STATE_REMOVED = 2;

	static final byte STATE_DISPOSED = 3;

	@SuppressWarnings("rawtypes")
	private static final ObjectAccessor<CacheHolder, ReferenceIndex.Handle> FIRSTOWNREFERENCEHANDLE_FIELDACCESSOR = Utils.newObjectAccessor(CacheHolder.class, ReferenceIndex.Handle.class, "firstOwnReferenceHandle");

	CacheHolder(final CacheGroup<TFacade, TImplement, TKeysHolder> ownGroup,
			final TImplement value, final TKeysHolder keysHolder,
			final Long fixLongIdentifier) {
		this.ownGroup = ownGroup;
		this.value = value;
		this.keysHolder = keysHolder;
		this.referenceModified = false;
		this.state = STATE_CREATED;
		this.longIdentifier = ownGroup.define.ownCache.clusterHolderContainer.onCreatedHolder(this, fixLongIdentifier);
	}

	final TFacade getFacade(final Transaction transaction)
			throws DisposedException {
		return this.getValue(transaction);
	}

	final TFacade tryGetFacade(final Transaction transaction) {
		return this.tryGetValue(transaction);
	}

	final ResourceToken<TFacade> getParent(final Transaction transaction) {
		final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.ownGroup.forceGetBindTree();
		if (tree == null) {
			return null;
		} else {
			return tree.tryGetParentOf(this, transaction);
		}
	}

	final ResourceTokenLink<TFacade> getChildren(final Transaction transaction) {
		final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.ownGroup.forceGetBindTree();
		if (tree == null) {
			return new ResourceTokenLinkImplement<TFacade>(this);
		} else {
			return tree.tryGetChildrenOf(this, transaction);
		}
	}

	@Override
	public String toString() {
		return "id:" + this.longIdentifier + "," + " value:" + this.value;
	}

	@Override
	final boolean needSynchronizeInCluster() {
		return this.ownGroup.needSynchronizeInCluster();
	}

	@Override
	void onTransactionCommit(final Transaction transaction) {
		switch (this.state) {
		case STATE_RESOLVED:
			if (this.modifying != null && this.modifying.posted) {
				this.ownGroup.finishModifyHolder(this, transaction);
			} else {
				this.ownGroup.cancelModifyHolder(this, transaction);
			}
		case STATE_CREATED:
			this.ownGroup.resolveHolder(this, transaction);
			break;
		case STATE_REMOVED:
			this.ownGroup.disposeHolder(this, transaction);
		case STATE_DISPOSED:
			return;
		default:
			throw new UnsupportedOperationException();
		}
		if (this.referenceModified) {
			ReferenceIndex<?> existReferenceIndex = this.firstReferenceIndex;
			while (existReferenceIndex != null) {
				existReferenceIndex.onTransactionCommit();
				existReferenceIndex = existReferenceIndex.next;
			}
			this.referenceModified = false;
		}
	}

	@Override
	void onTransactionRollback(final Transaction transaction) {
		switch (this.state) {
		case STATE_RESOLVED:
			if (this.modifying != null) {
				this.ownGroup.cancelModifyHolder(this, transaction);
				synchronized (this) {
					this.modifying = null;
				}
			}
			break;
		case STATE_REMOVED:
			this.ownGroup.resolveHolder(this, transaction);
			break;
		case STATE_CREATED:
			this.ownGroup.disposeHolder(this, transaction);
		case STATE_DISPOSED:
			return;
		default:
			throw new UnsupportedOperationException();
		}
		if (this.referenceModified) {
			ReferenceIndex<?> existReferenceIndex = this.firstReferenceIndex;
			while (existReferenceIndex != null) {
				existReferenceIndex.onTransactionRollback();
				existReferenceIndex = existReferenceIndex.next;
			}
			this.referenceModified = false;
		}
	}

	AccessControlCacheHolder<TFacade, TImplement, TKeysHolder> asAccessControlHolder() {
		return null;
	}

	// UserCacheHolder asUserHolder() {
	// return null;
	// }
	//
	// RoleCacheHolder asRoleHolder() {
	// return null;
	// }

	final boolean isDisposed() {
		return this.state == STATE_DISPOSED;
	}

	synchronized final boolean isVisibleIn(final Transaction transaction) {
		switch (this.state) {
		case STATE_RESOLVED:
			return true;
		case STATE_CREATED:
			return this.isModifiableOnTransaction(transaction);
		case STATE_REMOVED:
			return !this.isModifiableOnTransaction(transaction);
		case STATE_DISPOSED:
			return false;
		default:
			throw new UnsupportedOperationException();
		}
	}

	final byte getHolderState() {
		return this.state;
	}

	final void resolve(final Transaction transaction) {
		this.checkExclusiveTransaction(transaction);
		synchronized (this) {
			this.state = STATE_RESOLVED;
			if (this.modifying != null && this.modifying.posted) {
				this.value = this.modifying.value;
				this.keysHolder = this.modifying.keysHolder;
			}
			this.modifying = null;
		}
	}

	final boolean localRemove(final Transaction transaction) {
		transaction.handleAcquirable(this, AcquireFor.REMOVE);
		final boolean mark;
		if (this.haveModifedKeyValues(transaction)) {
			this.ownGroup.cancelModifyHolder(this, transaction);
		}
		if (this.ownTreeNode != null) {
			this.ownTreeNode.ownTree.localTryRemoveNode(this, transaction);
		}
		synchronized (this) {
			this.modifying = null;
			switch (this.state) {
			case STATE_CREATED:
				this.state = STATE_DISPOSED;
				this.ownTreeNode = null;
				mark = true;
				break;
			case STATE_RESOLVED:
				this.state = STATE_REMOVED;
				mark = false;
				break;
			case STATE_REMOVED:
			case STATE_DISPOSED:
				return false;
			default:
				throw new UnsupportedOperationException();
			}
		}
		ReferenceIndex<TFacade>.Handle handle = this.firstOwnReferenceHandle;
		while (handle != null) {
			if (!handle.deletedInReference) {
				handle.remove(transaction, true);
			}
			handle = handle.nextUndeletedInReference();
		}
		return mark;
	}

	final void remoteRemove(final Transaction transaction) {
		if (this.ownTreeNode != null) {
			this.ownTreeNode.ownTree.remoteRemoveNode(this, transaction);
		}
		synchronized (this) {
			this.modifying = null;
			switch (this.state) {
			case STATE_RESOLVED:
				this.state = STATE_REMOVED;
				break;
			case STATE_REMOVED:
			case STATE_DISPOSED:
				return;
			case STATE_CREATED:
				throw new CacheStateError();
			default:
				throw new UnsupportedOperationException();
			}
		}
		ReferenceIndex<TFacade>.Handle handle = this.firstOwnReferenceHandle;
		while (handle != null) {
			if (!handle.deletedInReference) {
				handle.remove(transaction, false);
			}
			handle = handle.nextUndeletedInReference();
		}
	}

	final void dispose(final Transaction transaction) {
		this.checkExclusiveTransaction(transaction);
		synchronized (this) {
			this.modifying = null;
			this.state = STATE_DISPOSED;
		}
	}

	final void doDispose(final ExceptionCatcher exceptionCatcher) {
		ReferenceIndex<TFacade>.Handle ownReferenceHandle;
		ReferenceIndex<?> referenceIndex;
		final TImplement value;
		final TKeysHolder keysHolder;
		synchronized (this) {
			if (this.modifying == null) {
				value = this.value;
				keysHolder = this.keysHolder;
			} else {
				value = this.modifying.value;
				keysHolder = this.modifying.keysHolder;
			}
			ownReferenceHandle = this.firstOwnReferenceHandle;
			referenceIndex = this.firstReferenceIndex;
			this.value = null;
			this.keysHolder = null;
			this.modifying = null;
			this.firstReferenceIndex = null;
			this.firstOwnReferenceHandle = null;
			this.ownTreeNode = null;
		}
		this.ownGroup.define.ownCache.clusterHolderContainer.onDisposedHolder(this);
		while (ownReferenceHandle != null) {
			ownReferenceHandle.disposeWhenReferenceDispose();
			final ReferenceIndex<TFacade>.Handle tempHandle = ownReferenceHandle;
			ownReferenceHandle = ownReferenceHandle.nextInReference;
			tempHandle.nextInReference = null;
		}
		while (referenceIndex != null) {
			referenceIndex.disposeAllHandle();
			referenceIndex = referenceIndex.next;
		}
		try {
			this.ownGroup.define.resourceService.disposeResource(value, keysHolder, exceptionCatcher);
		} catch (Throwable throwable) {
			exceptionCatcher.catchException(throwable, this);
		}
	}

	final void forceSetResolved() {
		this.state = STATE_RESOLVED;
	}

	final TKeysHolder forceGetKeysHolder() {
		return this.keysHolder;
	}

	final void forceSetValueAndKeysHolder(final TImplement value,
			final TKeysHolder keysHolder) {
		this.value = value;
		this.keysHolder = keysHolder;
	}

	final void forceSetModifyingWithNull() {
		this.modifying = null;
	}

	final CacheTree.Node<TFacade, TImplement, TKeysHolder> forceGetOwnTreeNode() {
		return this.ownTreeNode;
	}

	/**
	 * 如果在当前事务中，早先删除了缓存项，之后又把一个相同键值的缓存项加进来，则认为是对之前被删除的缓存项的修改操作。
	 */
	final boolean trySetModifyingFromRemoved(final Transaction transaction,
			final TImplement value, final TKeysHolder keysHolder) {
		if (this.isModifiableOnTransaction(transaction) && this.state == STATE_REMOVED) {
			this.state = STATE_RESOLVED;
			this.tryGetModifiableValue();
			this.tryPostModifiedValueWithoutCheck(value, keysHolder, transaction);
			return true;
		}
		return false;
	}

	synchronized final TImplement getValue(final Transaction transaction) {
		switch (this.state) {
		case STATE_RESOLVED:
			if (this.modifying == null || !this.isModifiableOnTransaction(transaction)) {
				return this.value;
			} else {
				return this.modifying.value;
			}
		case STATE_CREATED:
			return this.isModifiableOnTransaction(transaction) ? this.modifying == null ? this.value : this.modifying.value : null;
		case STATE_REMOVED:
			return this.isModifiableOnTransaction(transaction) ? null : this.value;
		case STATE_DISPOSED:
			throw this.disposedException();
		default:
			throw new UnsupportedOperationException();
		}
	}

	synchronized final TImplement tryGetValue(final Transaction transaction) {
		switch (this.state) {
		case STATE_RESOLVED:
			if (this.modifying == null || !this.isModifiableOnTransaction(transaction)) {
				return this.value;
			} else {
				return this.modifying.value;
			}
		case STATE_CREATED:
			return this.isModifiableOnTransaction(transaction) ? this.modifying == null ? this.value : this.modifying.value : null;
		case STATE_REMOVED:
			return this.isModifiableOnTransaction(transaction) ? null : this.value;
		case STATE_DISPOSED:
			return null;
		default:
			throw new UnsupportedOperationException();
		}
	}

	synchronized final TKeysHolder tryGetKeysHolder(
			final Transaction transaction) {
		switch (this.state) {
		case STATE_RESOLVED:
			if (this.modifying == null || !this.isModifiableOnTransaction(transaction)) {
				return this.keysHolder;
			} else {
				return this.modifying.keysHolder;
			}
		case STATE_CREATED:
			return this.isModifiableOnTransaction(transaction) ? this.modifying == null ? this.keysHolder : this.modifying.keysHolder : null;
		case STATE_REMOVED:
			return this.isModifiableOnTransaction(transaction) ? null : this.keysHolder;
		case STATE_DISPOSED:
			return null;
		default:
			throw new UnsupportedOperationException();
		}
	}

	final boolean haveModifedKeyValues(final Transaction transaction) {
		this.checkExclusiveTransaction(transaction);
		return this.modifying != null && this.modifying.modifiedKeyValues;
	}

	synchronized TImplement tryGetModifiableValue() {
		if (this.modifying == null) {
			this.modifying = new ModifiableHolder(this.value, this.keysHolder);
		}
		return this.modifying.getValue();
	}

	final TImplement tryGetModifyingValue(final Transaction transaction) {
		if (this.modifying == null) {
			return null;
		} else {
			return this.modifying.value;// this.modifying.posted ? null :
			// this.modifying.value;
		}
	}

	final void tryPostModifiedValue(final Object modifiedValue,
			final Transaction transaction) {
		this.checkExclusiveTransaction(transaction);
		if (this.modifying != null) {
			this.modifying.postValue(modifiedValue, null, transaction);
		} else {
			throw new IllegalStateException("当前尚未发起任何修改操作。");
		}
	}

	final void tryPostModifiedValue(final Object modifiedValue,
			final Object newKeysHolder, final Transaction transaction) {
		this.checkExclusiveTransaction(transaction);
		if (this.modifying != null) {
			this.modifying.postValue(modifiedValue, newKeysHolder, transaction);
		} else {
			throw new IllegalStateException("当前尚未发起任何修改操作。");
		}
	}

	void tryPostModifiedValueWithoutCheck(final Object modifiedValue,
			final Object newKeysHolder, final Transaction transaction) {
		this.checkExclusiveTransaction(transaction);
		if (this.modifying != null) {
			this.modifying.postValueWithoutCheck(modifiedValue, newKeysHolder, transaction);
		} else {
			throw new IllegalStateException("当前尚未发起任何修改操作。");
		}
	}

	final String tryGetAccessControlTitle(
			final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> provider) {
		if (this.ownGroup.define.isAccessControlDefine()) {
			synchronized (this) {
				switch (this.state) {
				case STATE_RESOLVED:
					if (this.modifying == null || !this.isModifiableOnCurrentThread()) {
						return provider.getAccessControlTitle(this.value, this.keysHolder);
					} else {
						return provider.getAccessControlTitle(this.modifying.value, this.modifying.keysHolder);
					}
				case STATE_CREATED:
					return this.isModifiableOnCurrentThread() ? this.modifying == null ? provider.getAccessControlTitle(this.value, this.keysHolder) : provider.getAccessControlTitle(this.modifying.value, this.modifying.keysHolder) : null;
				case STATE_REMOVED:
					return this.isModifiableOnCurrentThread() ? null : provider.getAccessControlTitle(this.value, this.keysHolder);
				case STATE_DISPOSED:
					return null;
				default:
					throw new UnsupportedOperationException();
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * @return 返回null代表缓存项已经被销毁
	 */
	@SuppressWarnings("unchecked")
	final <TReferenceFacade> List<TReferenceFacade> tryGetReferences(
			final Class<TReferenceFacade> referenceFacadeClass,
			final Filter<? super TReferenceFacade> filter,
			final Comparator<? super TReferenceFacade> comparator,
			final Transaction transaction) {
		this.ownGroup.ensureReferenceCacheInitialized(this.ownGroup.define.getReferenceDefine(referenceFacadeClass), transaction);
		ReferenceIndex<?> existReferenceIndex;
		synchronized (this) {
			if (this.isDisposed()) {
				return null;
			} else {
				if (this.firstReferenceIndex == null) {
					return new ArrayList<TReferenceFacade>(0);
				} else {
					findIndex: {
						existReferenceIndex = this.firstReferenceIndex;
						while (existReferenceIndex != null) {
							if (existReferenceIndex.referenceDefine.referenceCacheDefine.facadeClass == referenceFacadeClass) {
								break findIndex;
							} else {
								existReferenceIndex = existReferenceIndex.next;
							}
						}
						return new ArrayList<TReferenceFacade>(0);
					}
				}
			}
		}
		return ((ReferenceIndex<TReferenceFacade>) existReferenceIndex).getReferences(filter, comparator, transaction);
	}

	/**
	 * @return 返回null代表缓存项已经被销毁
	 */
	@SuppressWarnings("unchecked")
	final <TReferenceFacade> List<TReferenceFacade> tryGetReferences(
			final AccessController accessController,
			final Operation<? super TReferenceFacade> operation,
			final Class<TReferenceFacade> referenceFacadeClass,
			final Filter<? super TReferenceFacade> filter,
			final Comparator<? super TReferenceFacade> comparator,
			final Transaction transaction) {
		this.ownGroup.ensureReferenceCacheInitialized(this.ownGroup.define.getReferenceDefine(referenceFacadeClass), transaction);
		ReferenceIndex<?> existReferenceIndex;
		synchronized (this) {
			if (this.isDisposed()) {
				return null;
			} else {
				if (this.firstReferenceIndex == null) {
					return new ArrayList<TReferenceFacade>(0);
				} else {
					findIndex: {
						existReferenceIndex = this.firstReferenceIndex;
						while (existReferenceIndex != null) {
							if (existReferenceIndex.referenceDefine.referenceCacheDefine.facadeClass == referenceFacadeClass) {
								break findIndex;
							} else {
								existReferenceIndex = existReferenceIndex.next;
							}
						}
						return new ArrayList<TReferenceFacade>(0);
					}
				}
			}
		}
		return ((ReferenceIndex<TReferenceFacade>) existReferenceIndex).getReferences(accessController, operation, filter, comparator, transaction);
	}

	/**
	 * @return 返回null代表缓存项已经被销毁
	 */
	@SuppressWarnings("unchecked")
	final <TReferenceByFacade> List<TReferenceByFacade> tryGetReferencesBy(
			final Class<TReferenceByFacade> referenceByFacadeClass,
			final Transaction transaction) {
		final CacheDefine<?, ?, ?> define = this.ownGroup.define.ownCache.findDefine(referenceByFacadeClass);
		if (define == null) {
			return new ArrayList<TReferenceByFacade>(0);
		}
		this.ownGroup.ensureReferenceCacheInitialized(define.findReferenceDefine(this.ownGroup.define.facadeClass), transaction);
		final ArrayList<TReferenceByFacade> valueList = new ArrayList<TReferenceByFacade>();
		ReferenceIndex<?>.Handle ownReferenceHandle = this.firstOwnReferenceHandle;
		while (ownReferenceHandle != null) {
			if (!ownReferenceHandle.deletedInReference) {
				final CacheHolder<?, ?, ?> referenceByItem = ownReferenceHandle.ownReference().ownCacheHolder;
				if (referenceByItem.ownGroup.define.facadeClass == referenceByFacadeClass && ownReferenceHandle.isVisibleIn(transaction)) {
					valueList.add((TReferenceByFacade) (referenceByItem.tryGetValue(transaction)));
				}
			}
			ownReferenceHandle = ownReferenceHandle.nextUndeletedInReference();
		}
		return valueList;
	}

	@SuppressWarnings("unchecked")
	final <TReferenceFacade> List<CacheHolder<TReferenceFacade, ?, ?>> tryGetReferenceHolders(
			final Class<TReferenceFacade> referenceFacadeClass,
			final boolean currentExclusiveThread, final Transaction transaction) {
		if (transaction != null) {
			this.ownGroup.ensureReferenceCacheInitialized(this.ownGroup.define.getReferenceDefine(referenceFacadeClass), transaction);
		}
		ReferenceIndex<?> existReferenceIndex;
		synchronized (this) {
			if (this.isDisposed()) {
				return null;
			} else {
				if (this.firstReferenceIndex == null) {
					return new ArrayList<CacheHolder<TReferenceFacade, ?, ?>>(0);
				} else {
					findIndex: {
						existReferenceIndex = this.firstReferenceIndex;
						while (existReferenceIndex != null) {
							if (existReferenceIndex.referenceDefine.referenceCacheDefine.facadeClass == referenceFacadeClass) {
								break findIndex;
							} else {
								existReferenceIndex = existReferenceIndex.next;
							}
						}
						return new ArrayList<CacheHolder<TReferenceFacade, ?, ?>>(0);
					}
				}
			}
		}
		return ((ReferenceIndex<TReferenceFacade>) existReferenceIndex).getReferenceCacheHolders(currentExclusiveThread);
	}

	@SuppressWarnings("unchecked")
	final <TReferenceFacade> void localTryCreateReference(
			final CacheHolder<TReferenceFacade, ?, ?> reference,
			final Transaction transaction) {
		final CacheDefine<TReferenceFacade, ?, ?> referenceCacheDefine = reference.ownGroup.define;
		ReferenceIndex<?> existReferenceIndex;
		transaction.handleAcquirable(this, AcquireFor.MODIFY_ITEMS);
		findIndex: {
			synchronized (this) {
				if (this.isDisposed()) {
					return;
				}
				existReferenceIndex = this.firstReferenceIndex;
				while (existReferenceIndex != null) {
					if (existReferenceIndex.referenceDefine.referenceCacheDefine == referenceCacheDefine) {
						break findIndex;
					} else {
						existReferenceIndex = existReferenceIndex.next;
					}
				}
				existReferenceIndex = new ReferenceIndex<TReferenceFacade>(this, (ReferenceDefine<TReferenceFacade>) (this.ownGroup.define.getReferenceDefine(referenceCacheDefine.facadeClass)));
				existReferenceIndex.next = this.firstReferenceIndex;
				this.firstReferenceIndex = existReferenceIndex;
			}
		}
		((ReferenceIndex<TReferenceFacade>) existReferenceIndex).createHandle(reference);
		this.referenceModified = true;
	}

	@SuppressWarnings("unchecked")
	final <TReferenceFacade> void localTryRemoveReference(
			final CacheHolder<TReferenceFacade, ?, ?> reference,
			final Transaction transaction) {
		final CacheDefine<TReferenceFacade, ?, ?> referenceCacheDefine = reference.ownGroup.define;
		ReferenceIndex<?> existReferenceIndex;
		transaction.handleAcquirable(this, AcquireFor.MODIFY_ITEMS);
		synchronized (this) {
			if (this.isDisposed()) {
				return;
			}
			findIndex: {
				existReferenceIndex = this.firstReferenceIndex;
				while (existReferenceIndex != null) {
					if (existReferenceIndex.referenceDefine.referenceCacheDefine == referenceCacheDefine) {
						break findIndex;
					}
					existReferenceIndex = existReferenceIndex.next;
				}
				return;
			}
		}
		((ReferenceIndex<TReferenceFacade>) existReferenceIndex).removeHandle(reference);
		this.referenceModified = true;
	}

	final <TReferenceFacade> void localCreateReferenceAndCommit(
			final CacheHolder<TReferenceFacade, ?, ?> reference) {
		this.internalCreateReferenceAndCommit(reference);
	}

	@SuppressWarnings("unchecked")
	final <TReferenceFacade> void remoteCreateReference(
			final CacheHolder<TReferenceFacade, ?, ?> reference,
			final Transaction transaction) {
		final CacheDefine<TReferenceFacade, ?, ?> referenceCacheDefine = reference.ownGroup.define;
		ReferenceIndex<?> existReferenceIndex;
		findIndex: {
			synchronized (this) {
				existReferenceIndex = this.firstReferenceIndex;
				while (existReferenceIndex != null) {
					if (existReferenceIndex.referenceDefine.referenceCacheDefine == referenceCacheDefine) {
						break findIndex;
					} else {
						existReferenceIndex = existReferenceIndex.next;
					}
				}
				existReferenceIndex = new ReferenceIndex<TReferenceFacade>(this, (ReferenceDefine<TReferenceFacade>) (this.ownGroup.define.getReferenceDefine(referenceCacheDefine.facadeClass)));
				existReferenceIndex.next = this.firstReferenceIndex;
				this.firstReferenceIndex = existReferenceIndex;
			}
		}
		((ReferenceIndex<TReferenceFacade>) existReferenceIndex).createHandle(reference);
		this.referenceModified = true;
	}

	@SuppressWarnings("unchecked")
	final <TReferenceFacade> void remoteRemoveReference(
			final CacheHolder<TReferenceFacade, ?, ?> reference,
			final Transaction transaction) {
		final CacheDefine<TReferenceFacade, ?, ?> referenceCacheDefine = reference.ownGroup.define;
		ReferenceIndex<?> existReferenceIndex;
		synchronized (this) {
			findIndex: {
				existReferenceIndex = this.firstReferenceIndex;
				while (existReferenceIndex != null) {
					if (existReferenceIndex.referenceDefine.referenceCacheDefine == referenceCacheDefine) {
						break findIndex;
					}
					existReferenceIndex = existReferenceIndex.next;
				}
				throw new CacheStateError();
			}
		}
		((ReferenceIndex<TReferenceFacade>) existReferenceIndex).removeHandle(reference);
		this.referenceModified = true;
	}

	@SuppressWarnings("unchecked")
	final <TReferenceFacade> void removeReferenceAndCommit(
			final CacheHolder<TReferenceFacade, ?, ?> reference) {
		final CacheDefine<TReferenceFacade, ?, ?> referenceCacheDefine = reference.ownGroup.define;
		ReferenceIndex<?> existReferenceIndex;
		existReferenceIndex = this.firstReferenceIndex;
		while (existReferenceIndex != null) {
			if (existReferenceIndex.referenceDefine.referenceCacheDefine == referenceCacheDefine) {
				((ReferenceIndex<TReferenceFacade>) existReferenceIndex).removeHandleAndCommit(reference);
				return;
			} else {
				existReferenceIndex = existReferenceIndex.next;
			}
		}
	}

	final void tryRemoveAllReference(final Transaction transaction) {
		transaction.handleAcquirable(this, AcquireFor.MODIFY_ITEMS);
		synchronized (this) {
			if (this.isDisposed()) {
				return;
			}
			ReferenceIndex<?> existReferenceIndex = this.firstReferenceIndex;
			while (existReferenceIndex != null) {
				existReferenceIndex.removeAllHandle();
				existReferenceIndex = existReferenceIndex.next;
			}
		}
		this.referenceModified = true;
	}

	final void tryRemoveAllReference(final Class<?> referenceFacadeClass,
			final Transaction transaction) {
		transaction.handleAcquirable(this, AcquireFor.MODIFY_ITEMS);
		synchronized (this) {
			if (this.isDisposed()) {
				return;
			}
			ReferenceIndex<?> existReferenceIndex = this.firstReferenceIndex;
			while (existReferenceIndex != null) {
				if (existReferenceIndex.referenceDefine.referenceCacheDefine.facadeClass == referenceFacadeClass) {
					existReferenceIndex.removeAllHandle();
					break;
				} else {
					existReferenceIndex = existReferenceIndex.next;
				}
			}
		}
		this.referenceModified = true;
	}

	/**
	 * 该方法仅供CacheTree中的方法调用
	 */
	final void tryPutToTreeNode(
			final CacheTree.Node<TFacade, TImplement, TKeysHolder> treeNode) {
		synchronized (this) {
			this.ownTreeNode = treeNode;
		}
	}

	final <TReferenceFacade> void remoteCreateReferenceAndCommit(
			final CacheHolder<TReferenceFacade, ?, ?> reference) {
		this.internalCreateReferenceAndCommit(reference);
	}

	final void putToTreeNodeAndCommit(
			final CacheTree.Node<TFacade, TImplement, TKeysHolder> treeNode) {
		this.ownTreeNode = treeNode;
	}

	/**
	 * 该方法仅供CacheTree中的方法调用
	 */
	final CacheTree.Node<TFacade, TImplement, TKeysHolder> whenInitializeFindTreeNodeIn(
			final boolean get) {
		final CacheTree.Node<TFacade, TImplement, TKeysHolder> existTreeNode = this.ownTreeNode;
		if (get && existTreeNode == null) {
			throw new IllegalStateException("缓存项[" + this + "]尚未加入到缓存树中。");
		} else {
			return existTreeNode;
		}
	}

	/**
	 * 该方法仅供CacheTree中的方法调用
	 */
	final CacheTree.Node<TFacade, TImplement, TKeysHolder> findTreeNodeIn(
			final Transaction transaction, final boolean get) {
		synchronized (this) {
			if (!this.isDisposed()) {
				return this.findTreeNodeIn(this.isModifiableOnTransaction(transaction), get);
			}
		}
		if (get) {
			throw new IllegalStateException("缓存项[" + this + "]尚未加入到缓存树中。");
		} else {
			return null;
		}
	}

	final CacheTree.Node<TFacade, TImplement, TKeysHolder> findTreeNodeIn(
			final boolean isExclusivedThread, final boolean get) {
		CacheTree.Node<TFacade, TImplement, TKeysHolder> ownTreeNode = this.ownTreeNode;
		if (ownTreeNode != null) {
			if (isExclusivedThread) {
				final CacheTree.Node<TFacade, TImplement, TKeysHolder> tempOwnTreeNode = ownTreeNode.getTempNode();
				if (tempOwnTreeNode != null) {
					if (tempOwnTreeNode.getState() == CacheTree.Node.STATE_CREATED) {
						return tempOwnTreeNode;
					} else {
						throw new UnsupportedOperationException();
					}
				} else {
					switch (ownTreeNode.getState()) {
					case CacheTree.Node.STATE_RESOLVED:
					case CacheTree.Node.STATE_CREATED:
						return ownTreeNode;
					case CacheTree.Node.STATE_REMOVED:
					case CacheTree.Node.STATE_DISPOSED:
						break;
					default:
						throw new UnsupportedOperationException();
					}
				}
			} else {
				switch (ownTreeNode.getState()) {
				case CacheTree.Node.STATE_RESOLVED:
				case CacheTree.Node.STATE_REMOVED:
					return ownTreeNode;
				case CacheTree.Node.STATE_CREATED:
				case CacheTree.Node.STATE_DISPOSED:
					break;
				default:
					throw new UnsupportedOperationException();
				}
			}
		}
		if (get) {
			throw new IllegalStateException("缓存项[" + this + "]尚未加入到缓存树中。");
		} else {
			return null;
		}
	}

	void collectHolderData(final CacheInitializeCollector collector) {
		collector.addCreateHolderData(this, this.value, this.keysHolder);
		synchronized (this) {
			ReferenceIndex<?> existReferenceIndex = this.firstReferenceIndex;
			while (existReferenceIndex != null) {
				existReferenceIndex.collectReferenceData(collector);
				existReferenceIndex = existReferenceIndex.next;
			}
		}
	}

	void collectModifiedHolderData(final CacheSynchronizeCollector collector,
			final Transaction transaction) {
		switch (this.ownGroup.getState()) {
		case CacheGroup.STATE_DISPOSED:
			return;
		case CacheGroup.STATE_REMOVED:
			if (this.ownGroup.isModifiableOnTransaction(transaction)) {
				return;
			}
		}
		switch (this.getHolderState()) {
		case CacheHolder.STATE_CREATED:
			if (this.modifying != null && this.modifying.posted) {
				collector.addCreateHolderData(this, this.modifying.value, this.modifying.keysHolder);
			} else {
				collector.addCreateHolderData(this, this.value, this.keysHolder);
			}
			break;
		case CacheHolder.STATE_RESOLVED:
			if (this.modifying != null && this.modifying.posted) {
				collector.addModifyHolderData(this, this.modifying.value, this.modifying.keysHolder);
			}
			break;
		case CacheHolder.STATE_REMOVED:
			collector.addRemoveHolderData(this);
			break;
		default:
			return;
		}
		synchronized (this) {
			ReferenceIndex<?> existReferenceIndex = this.firstReferenceIndex;
			while (existReferenceIndex != null) {
				existReferenceIndex.collectModifiedReferenceData(collector);
				existReferenceIndex = existReferenceIndex.next;
			}
		}
	}

	final DisposedException disposedException() {
		return new DisposedException("缓存项已被销毁。外观类型为[" + this.ownGroup.define.facadeClass + "]");
	}

	private final boolean tryPutToReferenceHandle(
			final ReferenceIndex<TFacade>.Handle referenceHandle) {
		if (this.isDisposed()) {
			return false;
		} else {
			this.putToReferenceHandleAndCommit(referenceHandle);
			return true;
		}
	}

	private final void putToReferenceHandleAndCommit(
			final ReferenceIndex<TFacade>.Handle referenceHandle) {
		for (;;) {
			ReferenceIndex<TFacade>.Handle firstOwnReferenceHandle = this.firstOwnReferenceHandle;
			referenceHandle.nextInReference = firstOwnReferenceHandle;
			if (FIRSTOWNREFERENCEHANDLE_FIELDACCESSOR.CAS(this, firstOwnReferenceHandle, referenceHandle)) {
				// 回收后面已删除的节点
				referenceHandle.nextUndeletedInReference();
				return;
			}
		}
	}

	private final void tryRemoveFromReferenceHandle(
			final ReferenceIndex<TFacade>.Handle referenceHandle) {
		referenceHandle.deletedInReference = true;
		ReferenceIndex<?>.Handle ownReferenceHandle = this.firstOwnReferenceHandle;
		if (ownReferenceHandle == referenceHandle) {
			FIRSTOWNREFERENCEHANDLE_FIELDACCESSOR.CAS(this, ownReferenceHandle, ownReferenceHandle.nextUndeletedInReference());
			return;
		} else {
			for (;;) {
				ReferenceIndex<?>.Handle lastOwnReferenceHandle = ownReferenceHandle;
				ownReferenceHandle = ownReferenceHandle.nextInReference;
				if (ownReferenceHandle == null) {
					return;
				}
				if (!ownReferenceHandle.deletedInReference) {
					// 没有打上删除标记，肯定不是当前要删除的节点
					continue;
				}
				if (ownReferenceHandle == referenceHandle) {
					ReferenceIndex.HANDLE_NEXTINREFERENCE_FIELDACCESSOR.CAS(lastOwnReferenceHandle, referenceHandle, referenceHandle.nextUndeletedInReference());
					return;
				}
			}
		}
	}

	private final void checkExclusiveTransaction(final Transaction transaction) {
		if (!this.isModifiableOnTransaction(transaction)) {
			throw new CacheStateError();
		}
	}

	@SuppressWarnings("unchecked")
	private final <TReferenceFacade> void internalCreateReferenceAndCommit(
			final CacheHolder<TReferenceFacade, ?, ?> reference) {
		final CacheDefine<TReferenceFacade, ?, ?> referenceCacheDefine = reference.ownGroup.define;
		ReferenceIndex<?> existReferenceIndex;
		existReferenceIndex = this.firstReferenceIndex;
		while (existReferenceIndex != null) {
			if (existReferenceIndex.referenceDefine.referenceCacheDefine == referenceCacheDefine) {
				((ReferenceIndex<TReferenceFacade>) existReferenceIndex).createHandleAndCommit(reference);
				return;
			} else {
				existReferenceIndex = existReferenceIndex.next;
			}
		}
		ReferenceIndex<TReferenceFacade> newReferenctIndex = new ReferenceIndex<TReferenceFacade>(this, (ReferenceDefine<TReferenceFacade>) (this.ownGroup.define.getReferenceDefine(referenceCacheDefine.facadeClass)));
		newReferenctIndex.next = this.firstReferenceIndex;
		this.firstReferenceIndex = newReferenctIndex;
		newReferenctIndex.createHandleAndCommit(reference);
	}

	final long longIdentifier;

	final CacheGroup<TFacade, TImplement, TKeysHolder> ownGroup;

	volatile CacheHolder<?, ?, ?> nextInGlobalContainer;

	private volatile TImplement value;

	private volatile TKeysHolder keysHolder;

	private volatile byte state;

	private volatile ReferenceIndex<?> firstReferenceIndex;

	private volatile ReferenceIndex<TFacade>.Handle firstOwnReferenceHandle;

	private volatile CacheTree.Node<TFacade, TImplement, TKeysHolder> ownTreeNode;

	private volatile ModifiableHolder modifying;

	private volatile boolean referenceModified;

	private final class ModifiableHolder {

		private ModifiableHolder(final TImplement value,
				final TKeysHolder keysHolder) {
			this.value = value;
			this.keysHolder = keysHolder;
			this.modifiedKeyValues = false;
			this.posted = true;
		}

		final TImplement getValue() {
			// if (this.posted && this.value == CacheHolder.this.value) {
			if (this.value == CacheHolder.this.value) {
				this.posted = false;
			}
			return this.value = OBJAContext.clone(this.value, null, CacheHolder.this.ownGroup.define.implementStruct);
			// return this.value;
		}

		private final void postValue(final Object modifiedValue,
				Object newKeysHolder, final Transaction transaction) {
			if (modifiedValue == this.value) {
				this.postValueWithoutCheck(modifiedValue, newKeysHolder, transaction);
			} else {
				throw new IllegalArgumentException("无效的对象。[" + modifiedValue + "]");
			}
		}

		@SuppressWarnings("unchecked")
		private final void postValueWithoutCheck(final Object modifiedValue,
				Object newKeysHolder, final Transaction transaction) {
			// if (this.posted) {
			// throw new IllegalStateException("当前的修改结果已经提交。");
			// } else {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder = CacheHolder.this;
			if (newKeysHolder == null && holder.value == holder.keysHolder) {
				newKeysHolder = modifiedValue;
			}
			final TKeysHolder keysHolder = (TKeysHolder) newKeysHolder;
			this.modifiedKeyValues = holder.ownGroup.onPostModifiedHolder(holder, holder.keysHolder, keysHolder);
			this.value = (TImplement) modifiedValue;
			this.keysHolder = keysHolder;
			this.posted = true;
			// }
		}

		private TImplement value;

		private TKeysHolder keysHolder;

		private boolean modifiedKeyValues;

		private boolean posted;

	}

	static final class ReferenceIndex<TReferenceFacade> {

		static final byte HANDLE_STATE_CREATED = 0;

		static final byte HANDLE_STATE_RESOLVED = 1;

		static final byte HANDLE_STATE_REMOVED = 2;

		static final byte HANDLE_STATE_DISPOSED = 3;

		@SuppressWarnings("rawtypes")
		private static final ObjectAccessor<ReferenceIndex.Handle, ReferenceIndex.Handle> HANDLE_NEXTINREFERENCE_FIELDACCESSOR = Utils.newObjectAccessor(ReferenceIndex.Handle.class, ReferenceIndex.Handle.class, "nextInReference");

		private ReferenceIndex(final CacheHolder<?, ?, ?> ownItem,
				final ReferenceDefine<TReferenceFacade> referenceDefine) {
			this.ownCacheHolder = ownItem;
			this.referenceDefine = referenceDefine;
			this.modified = false;
		}

		private final void reset() {
			this.modified = false;
		}

		private final void createHandleAndCommit(
				final CacheHolder<TReferenceFacade, ?, ?> reference) {
			Handle existHandle = this.firstHandle;
			while (existHandle != null) {
				if (existHandle.cacheHolder == reference) {
					return;
				} else {
					existHandle = existHandle.nextInIndex;
				}
			}
			final Handle newHandle = new Handle(reference);
			newHandle.nextInIndex = this.firstHandle;
			this.firstHandle = newHandle;
			if (this.referenceDefine.isAccessControlReferenceDefine()) {
				this.ownCacheHolder.asAccessControlHolder().createACReferenctHandle(newHandle);
			}
			newHandle.state = HANDLE_STATE_RESOLVED;
			reference.putToReferenceHandleAndCommit(newHandle);
		}

		private final void removeHandleAndCommit(
				final CacheHolder<TReferenceFacade, ?, ?> reference) {
			Handle lastHandle = null;
			Handle existHandle = this.firstHandle;
			while (existHandle != null) {
				if (existHandle.cacheHolder == reference) {
					this.disposeHandle(existHandle, lastHandle, false);
					return;
				} else {
					lastHandle = existHandle;
					existHandle = existHandle.nextInIndex;
				}
			}
		}

		private final void whenReferenceRemovedRemoveHandle(
				final Handle handle, final Transaction transaction,
				final boolean forLocal) {
			Handle lastHandle = null;
			synchronized (this) {
				if (this.ownCacheHolder.isDisposed()) {
					return;
				} else {
					Handle existHandle = this.firstHandle;
					while (existHandle != null) {
						if (existHandle == handle) {
							if (handle.state == HANDLE_STATE_CREATED) {
								this.disposeHandle(handle, lastHandle, false);
							} else {
								handle.state = HANDLE_STATE_REMOVED;
							}
							return;
						} else {
							lastHandle = existHandle;
							existHandle = existHandle.nextInIndex;
						}
					}
				}
			}
		}

		private final void whenReferenceDisposedDisposeHandle(
				final Handle handle) {
			if (handle.isDisposed()) {
				return;
			}
			Handle lastHandle = null;
			synchronized (this) {
				Handle existHandle = this.firstHandle;
				while (existHandle != null) {
					if (existHandle == handle) {
						this.disposeHandle(existHandle, lastHandle, true);
						return;
					} else {
						lastHandle = existHandle;
						existHandle = existHandle.nextInIndex;
					}
				}
			}
		}

		private final List<TReferenceFacade> getReferences(
				final Filter<? super TReferenceFacade> filter,
				final Comparator<? super TReferenceFacade> comparator,
				final Transaction transaction) {
			final List<TReferenceFacade> referenceList = new ArrayList<TReferenceFacade>();
			synchronized (this) {
				if (this.ownCacheHolder.isDisposed()) {
					return null;
				} else {
					if (this.ownCacheHolder.isModifiableOnTransaction(transaction)) {
						Handle existHandle = this.firstHandle;
						Handle lastExistHandle = null;
						while (existHandle != null) {
							switch (existHandle.state) {
							case HANDLE_STATE_CREATED:
							case HANDLE_STATE_RESOLVED:
								final TReferenceFacade value = existHandle.cacheHolder.tryGetValue(transaction);
								if (value != null) {
									referenceList.add(value);
								}
							case HANDLE_STATE_REMOVED:
								break;
							case HANDLE_STATE_DISPOSED:
								Handle next = existHandle.nextInIndex;
								if (lastExistHandle == null) {
									this.firstHandle = next;
								} else {
									lastExistHandle.nextInIndex = next;
								}
								existHandle.nextInIndex = null;
								existHandle = next;
								continue;
							default:
								throw new UnsupportedOperationException();
							}
							lastExistHandle = existHandle;
							existHandle = existHandle.nextInIndex;
						}
					} else {
						Handle existHandle = this.firstHandle;
						Handle lastExistHandle = null;
						while (existHandle != null) {
							switch (existHandle.state) {
							case HANDLE_STATE_REMOVED:
							case HANDLE_STATE_RESOLVED:
								final TReferenceFacade value = existHandle.cacheHolder.tryGetValue(transaction);
								if (value != null) {
									referenceList.add(value);
								}
							case HANDLE_STATE_CREATED:
								break;
							case HANDLE_STATE_DISPOSED:
								Handle next = existHandle.nextInIndex;
								if (lastExistHandle == null) {
									this.firstHandle = next;
								} else {
									lastExistHandle.nextInIndex = next;
								}
								existHandle.nextInIndex = null;
								existHandle = next;
								continue;
							default:
								throw new UnsupportedOperationException();
							}
							lastExistHandle = existHandle;
							existHandle = existHandle.nextInIndex;
						}
					}
				}
			}
			this.processReferences(referenceList, filter, comparator);
			return referenceList;
		}

		private final List<TReferenceFacade> getReferences(
				final AccessController accessController,
				final Operation<? super TReferenceFacade> operation,
				final Filter<? super TReferenceFacade> filter,
				final Comparator<? super TReferenceFacade> comparator,
				final Transaction transaction) {
			final List<TReferenceFacade> referenceList = new ArrayList<TReferenceFacade>();
			final OperationEntry operationEntry = OperationEntry.operationEntryOf(operation, this.referenceDefine.referenceCacheDefine.accessControlDefine.operationEntrys);
			Handle lastExistHandle = null;
			synchronized (this) {
				if (this.ownCacheHolder.isDisposed()) {
					return null;
				} else {
					if (this.ownCacheHolder.isModifiableOnTransaction(transaction)) {
						Handle existHandle = this.firstHandle;
						while (existHandle != null) {
							switch (existHandle.state) {
							case HANDLE_STATE_CREATED:
							case HANDLE_STATE_RESOLVED:
								if (accessController.internalHasAuthority(operationEntry, existHandle.cacheHolder.asAccessControlHolder())) {
									final TReferenceFacade value = existHandle.cacheHolder.tryGetValue(transaction);
									if (value != null) {
										referenceList.add(value);
									}
								}
							case HANDLE_STATE_REMOVED:
								break;
							case HANDLE_STATE_DISPOSED:
								Handle next = existHandle.nextInIndex;
								if (lastExistHandle == null) {
									this.firstHandle = next;
								} else {
									lastExistHandle.nextInIndex = next;
								}
								existHandle.nextInIndex = null;
								existHandle = next;
								continue;
							default:
								throw new UnsupportedOperationException();
							}
							lastExistHandle = existHandle;
							existHandle = existHandle.nextInIndex;
						}
					} else {
						Handle existHandle = this.firstHandle;
						while (existHandle != null) {
							switch (existHandle.state) {
							case HANDLE_STATE_REMOVED:
							case HANDLE_STATE_RESOLVED:
								if (accessController.hasAuthority(operation, existHandle.cacheHolder)) {
									final TReferenceFacade value = existHandle.cacheHolder.tryGetValue(transaction);
									if (value != null) {
										referenceList.add(value);
									}
								}
							case HANDLE_STATE_CREATED:
								break;
							case HANDLE_STATE_DISPOSED:
								Handle next = existHandle.nextInIndex;
								if (lastExistHandle == null) {
									this.firstHandle = next;
								} else {
									lastExistHandle.nextInIndex = next;
								}
								existHandle.nextInIndex = null;
								existHandle = next;
								continue;
							default:
								throw new UnsupportedOperationException();
							}
							lastExistHandle = existHandle;
							existHandle = existHandle.nextInIndex;
						}
					}
				}
			}
			this.processReferences(referenceList, filter, comparator);
			return referenceList;
		}

		private final void processReferences(
				final List<TReferenceFacade> references,
				final Filter<? super TReferenceFacade> filter,
				final Comparator<? super TReferenceFacade> comparator) {
			if (references.size() != 0) {
				final int oldSize = references.size();
				int acceptedCount = 0;
				final boolean filted;
				final Filter<? super TReferenceFacade> defaultFilter = this.referenceDefine.defaultFilter;
				if (defaultFilter != null) {
					if (filter != null) {
						for (int index = 0, endIndex = oldSize; index < endIndex; index++) {
							final TReferenceFacade reference = references.get(index);
							if (filter.accept(reference) && defaultFilter.accept(reference)) {
								if (acceptedCount != index) {
									references.set(acceptedCount, reference);
								}
								acceptedCount++;
							}
						}
					} else {
						for (int index = 0, endIndex = oldSize; index < endIndex; index++) {
							final TReferenceFacade reference = references.get(index);
							if (defaultFilter.accept(reference)) {
								if (acceptedCount != index) {
									references.set(acceptedCount, reference);
								}
								acceptedCount++;
							}
						}
					}
					filted = true;
				} else if (filter != null) {
					for (int index = 0, endIndex = oldSize; index < endIndex; index++) {
						final TReferenceFacade reference = references.get(index);
						if (filter.accept(reference)) {
							if (acceptedCount != index) {
								references.set(acceptedCount, reference);
							}
							acceptedCount++;
						}
					}
					filted = true;
				} else {
					filted = false;
				}
				if (filted) {
					if (acceptedCount == 0) {
						references.clear();
					} else if (acceptedCount == 1) {
						final TReferenceFacade reference = references.get(0);
						references.clear();
						references.add(reference);
					} else {
						for (int index = references.size() - 1, endIndex = acceptedCount; index >= endIndex; index--) {
							references.remove(index);
						}
					}
				}
				if (references.size() > 1) {
					if (comparator != null) {
						SortUtil.sort(references, comparator);
					} else if (this.referenceDefine.defaultComparator != null) {
						SortUtil.sort(references, this.referenceDefine.defaultComparator);
					}
				}
			}
		}

		private final List<CacheHolder<TReferenceFacade, ?, ?>> getReferenceCacheHolders(
				final boolean currentExclusiveThread) {
			final List<CacheHolder<TReferenceFacade, ?, ?>> referenceList = new ArrayList<CacheHolder<TReferenceFacade, ?, ?>>();
			Handle lastExistHandle = null;
			synchronized (this) {
				if (this.ownCacheHolder.isDisposed()) {
					return null;
				} else {
					if (currentExclusiveThread) {
						Handle existHandle = this.firstHandle;
						while (existHandle != null) {
							switch (existHandle.state) {
							case HANDLE_STATE_CREATED:
							case HANDLE_STATE_RESOLVED:
								referenceList.add(existHandle.cacheHolder);
								break;
							case HANDLE_STATE_DISPOSED:
								Handle next = existHandle.nextInIndex;
								if (lastExistHandle == null) {
									this.firstHandle = next;
								} else {
									lastExistHandle.nextInIndex = next;
								}
								existHandle.nextInIndex = null;
								existHandle = next;
								continue;
							case HANDLE_STATE_REMOVED:
								break;
							default:
								throw new UnsupportedOperationException();
							}
							lastExistHandle = existHandle;
							existHandle = existHandle.nextInIndex;
						}
					} else {
						Handle existHandle = this.firstHandle;
						while (existHandle != null) {
							switch (existHandle.state) {
							case HANDLE_STATE_REMOVED:
							case HANDLE_STATE_RESOLVED:
								referenceList.add(existHandle.cacheHolder);
								break;
							case HANDLE_STATE_DISPOSED:
								Handle next = existHandle.nextInIndex;
								if (lastExistHandle == null) {
									this.firstHandle = next;
								} else {
									lastExistHandle.nextInIndex = next;
								}
								existHandle.nextInIndex = null;
								existHandle = next;
								continue;
							case HANDLE_STATE_CREATED:
								break;
							default:
								throw new UnsupportedOperationException();
							}
							lastExistHandle = existHandle;
							existHandle = existHandle.nextInIndex;
						}
					}
				}
			}
			return referenceList;
		}

		private final void createHandle(
				final CacheHolder<TReferenceFacade, ?, ?> reference) {
			synchronized (this) {
				if (this.ownCacheHolder.isDisposed()) {
					return;
				} else {
					Handle existHandle = this.firstHandle;
					while (existHandle != null) {
						if (existHandle.cacheHolder == reference) {
							switch (existHandle.state) {
							case HANDLE_STATE_CREATED:
							case HANDLE_STATE_RESOLVED:
								return;
							case HANDLE_STATE_REMOVED:
							case HANDLE_STATE_DISPOSED:
								break;
							default:
								throw new UnsupportedOperationException();
							}
						}
						existHandle = existHandle.nextInIndex;
					}
				}
			}
			final Handle newHandle = new Handle(reference);
			if (reference.tryPutToReferenceHandle(newHandle)) {
				synchronized (this) {
					if (this.ownCacheHolder.isDisposed()) {
						return;
					} else {
						newHandle.nextInIndex = this.firstHandle;
						this.firstHandle = newHandle;
						if (this.referenceDefine.isAccessControlReferenceDefine()) {
							this.ownCacheHolder.asAccessControlHolder().createACReferenctHandle(newHandle);
						}
						this.modified = true;
					}
				}
			}
		}

		private final void removeHandle(
				final CacheHolder<TReferenceFacade, ?, ?> reference) {
			Handle lastHandle = null;
			Handle existHandle;
			synchronized (this) {
				if (this.ownCacheHolder.isDisposed()) {
					return;
				} else {
					existHandle = this.firstHandle;
					find: {
						while (existHandle != null) {
							if (existHandle.cacheHolder == reference) {
								break find;
							} else {
								lastHandle = existHandle;
								existHandle = existHandle.nextInIndex;
							}
						}
						return;
					}
				}
			}
			if (existHandle.state == HANDLE_STATE_CREATED) {
				this.disposeHandle(existHandle, lastHandle, false);
			} else {
				existHandle.state = HANDLE_STATE_REMOVED;
				this.modified = true;
			}
		}

		private final void removeAllHandle() {
			Handle lastHandle = null;
			Handle existHandle = this.firstHandle;
			while (existHandle != null) {
				if (existHandle.state == HANDLE_STATE_CREATED) {
					this.disposeHandle(existHandle, lastHandle, false);
				} else {
					existHandle.state = HANDLE_STATE_REMOVED;
					this.modified = true;
				}
				lastHandle = existHandle;
				existHandle = existHandle.nextInIndex;
			}
		}

		private final void disposeAllHandle() {
			Handle existHandle = this.firstHandle;
			this.firstHandle = null;
			while (existHandle != null) {
				existHandle.cacheHolder.tryRemoveFromReferenceHandle(existHandle);
				existHandle = existHandle.nextInIndex;
			}
		}

		private final void disposeHandle(final Handle existHandle,
				final Handle lastHandle, final boolean causeByDisposeReference) {
			if (!causeByDisposeReference) {
				existHandle.cacheHolder.tryRemoveFromReferenceHandle(existHandle);
			}
			synchronized (this) {
				if (lastHandle == null) {
					this.firstHandle = existHandle.nextInIndex;
				} else {
					lastHandle.nextInIndex = existHandle.nextInIndex;
				}
				existHandle.nextInIndex = null;
				if (this.referenceDefine.isAccessControlReferenceDefine()) {
					this.ownCacheHolder.asAccessControlHolder().disposeACReferenceHandle(existHandle);
				}
				existHandle.state = HANDLE_STATE_DISPOSED;
			}
		}

		private final void onTransactionCommit() {
			if (this.modified) {
				Handle lastExistHandle = null;
				Handle existHandle = this.firstHandle;
				while (existHandle != null) {
					switch (existHandle.state) {
					case HANDLE_STATE_CREATED:
						existHandle.state = HANDLE_STATE_RESOLVED;
					case HANDLE_STATE_RESOLVED:
						lastExistHandle = existHandle;
						existHandle = existHandle.nextInIndex;
						continue;
					case HANDLE_STATE_REMOVED:
					case HANDLE_STATE_DISPOSED:
						final Handle nextHandle = existHandle.nextInIndex;
						this.disposeHandle(existHandle, lastExistHandle, false);
						existHandle = nextHandle;
						continue;
					default:
						throw new UnsupportedOperationException();
					}
				}
				if (this.referenceDefine.isAccessControlReferenceDefine()) {
					this.ownCacheHolder.asAccessControlHolder().resetCachedNextHolderInAuthorityInheritPath();
				}
			}
			this.reset();
		}

		private final void onTransactionRollback() {
			if (this.modified) {
				Handle lastExistHandle = null;
				Handle existHandle = this.firstHandle;
				while (existHandle != null) {
					switch (existHandle.state) {
					case HANDLE_STATE_REMOVED:
						existHandle.state = HANDLE_STATE_RESOLVED;
					case HANDLE_STATE_RESOLVED:
						lastExistHandle = existHandle;
						existHandle = existHandle.nextInIndex;
						continue;
					case HANDLE_STATE_DISPOSED:
					case HANDLE_STATE_CREATED:
						final Handle nextHandle = existHandle.nextInIndex;
						this.disposeHandle(existHandle, lastExistHandle, false);
						existHandle = nextHandle;
						continue;
					default:
						throw new UnsupportedOperationException();
					}
				}
			}
			this.reset();
		}

		private final void collectReferenceData(
				final CacheInitializeCollector collector) {
			Handle existHandle = this.firstHandle;
			while (existHandle != null) {
				collector.addCreateReferenceData(this.ownCacheHolder, existHandle.cacheHolder);
				existHandle = existHandle.nextInIndex;
			}
		}

		private final void collectModifiedReferenceData(
				final CacheSynchronizeCollector collector) {
			if (this.modified) {
				Handle existHandle = this.firstHandle;
				while (existHandle != null) {
					switch (existHandle.state) {
					case HANDLE_STATE_CREATED:
						collector.addCreateReferenceData(this.ownCacheHolder, existHandle.cacheHolder);
						break;
					case HANDLE_STATE_REMOVED:
						collector.addRemoveReferenceData(this.ownCacheHolder, existHandle.cacheHolder);
						break;
					}
					existHandle = existHandle.nextInIndex;
				}
			}
		}

		@Deprecated
		private final List<TReferenceFacade> getReferences() {
			final List<TReferenceFacade> referenceList = new ArrayList<TReferenceFacade>();
			synchronized (this) {
				if (this.ownCacheHolder.isDisposed()) {
					return null;
				} else {
					if (this.ownCacheHolder.isModifiableOnCurrentThread()) {
						Handle existHandle = this.firstHandle;
						Handle lastExistHandle = null;
						while (existHandle != null) {
							switch (existHandle.state) {
							case HANDLE_STATE_CREATED:
							case HANDLE_STATE_RESOLVED:
								final TReferenceFacade value = existHandle.cacheHolder.tryGetValue();
								if (value != null) {
									referenceList.add(value);
								}
							case HANDLE_STATE_REMOVED:
								break;
							case HANDLE_STATE_DISPOSED:
								Handle next = existHandle.nextInIndex;
								if (lastExistHandle == null) {
									this.firstHandle = next;
								} else {
									lastExistHandle.nextInIndex = next;
								}
								existHandle.nextInIndex = null;
								existHandle = next;
								continue;
							default:
								throw new UnsupportedOperationException();
							}
							lastExistHandle = existHandle;
							existHandle = existHandle.nextInIndex;
						}
					} else {
						Handle existHandle = this.firstHandle;
						Handle lastExistHandle = null;
						while (existHandle != null) {
							switch (existHandle.state) {
							case HANDLE_STATE_REMOVED:
							case HANDLE_STATE_RESOLVED:
								final TReferenceFacade value = existHandle.cacheHolder.tryGetValue();
								if (value != null) {
									referenceList.add(value);
								}
							case HANDLE_STATE_CREATED:
								break;
							case HANDLE_STATE_DISPOSED:
								Handle next = existHandle.nextInIndex;
								if (lastExistHandle == null) {
									this.firstHandle = next;
								} else {
									lastExistHandle.nextInIndex = next;
								}
								existHandle.nextInIndex = null;
								existHandle = next;
								continue;
							default:
								throw new UnsupportedOperationException();
							}
							lastExistHandle = existHandle;
							existHandle = existHandle.nextInIndex;
						}
					}
				}
			}
			return referenceList;
		}

		private final CacheHolder<?, ?, ?> ownCacheHolder;

		private final ReferenceDefine<TReferenceFacade> referenceDefine;

		private volatile Handle firstHandle;

		private volatile ReferenceIndex<?> next;

		private boolean modified;

		final class Handle {

			private Handle(final CacheHolder<TReferenceFacade, ?, ?> cacheHolder) {
				this.cacheHolder = cacheHolder;
				this.state = HANDLE_STATE_CREATED;
				this.deletedInReference = false;
			}

			final Handle nextUndeletedInReference() {
				for (;;) {
					Handle next = this.nextInReference;
					if (next == null) {
						return null;
					}
					if (!next.deletedInReference) {
						return next;
					}
					Handle firstUndeletedNext = next;
					while (firstUndeletedNext != null && firstUndeletedNext.deletedInReference) {
						firstUndeletedNext = firstUndeletedNext.nextInReference;
					}
					if (HANDLE_NEXTINREFERENCE_FIELDACCESSOR.CAS(this, next, firstUndeletedNext)) {
						return firstUndeletedNext;
					}
				}
			}

			final byte getState() {
				return this.state;
			}

			private final ReferenceIndex<TReferenceFacade> ownReference() {
				return ReferenceIndex.this;
			}

			private final boolean isVisibleIn(final Transaction transaction) {
				final CacheHolder<?, ?, ?> ownHolder = ReferenceIndex.this.ownCacheHolder;
				synchronized (ReferenceIndex.this) {
					switch (this.state) {
					case ReferenceIndex.HANDLE_STATE_RESOLVED:
						return true;
					case ReferenceIndex.HANDLE_STATE_CREATED:
						return ownHolder.isModifiableOnTransaction(transaction);
					case ReferenceIndex.HANDLE_STATE_REMOVED:
						return !ownHolder.isModifiableOnTransaction(transaction);
					case ReferenceIndex.HANDLE_STATE_DISPOSED:
						return false;
					default:
						throw new UnsupportedOperationException();
					}
				}
			}

			private final boolean isVisibleInCurrentThread() {
				final CacheHolder<?, ?, ?> ownHolder = ReferenceIndex.this.ownCacheHolder;
				synchronized (ReferenceIndex.this) {
					switch (ownHolder.state) {
					case ReferenceIndex.HANDLE_STATE_RESOLVED:
						return true;
					case ReferenceIndex.HANDLE_STATE_CREATED:
						return ownHolder.isModifiableOnCurrentThread();
					case ReferenceIndex.HANDLE_STATE_REMOVED:
						return !ownHolder.isModifiableOnCurrentThread();
					case ReferenceIndex.HANDLE_STATE_DISPOSED:
						return false;
					default:
						throw new UnsupportedOperationException();
					}
				}
			}

			private final void remove(final Transaction transaction,
					final boolean forLocal) {
				ReferenceIndex.this.whenReferenceRemovedRemoveHandle(this, transaction, forLocal);
			}

			private final void disposeWhenReferenceDispose() {
				ReferenceIndex.this.whenReferenceDisposedDisposeHandle(this);
			}

			private final boolean isDisposed() {
				return this.state == HANDLE_STATE_DISPOSED;
			}

			final CacheHolder<TReferenceFacade, ?, ?> cacheHolder;

			private volatile byte state;

			private volatile Handle nextInIndex;

			private volatile Handle nextInReference;

			private volatile boolean deletedInReference;

		}

	}

	// ------------------------------------------------------------------------
	// 兼容旧版本

	@SuppressWarnings("unchecked")
	public final Class<TFacade> getFacadeClass() {
		return (Class<TFacade>) (this.ownGroup.define.facadeClass);
	}

	public final ResourceKind getKind() {
		return this.ownGroup.define.kind;
	}

	public final Object getCategory() {
		return this.ownGroup.longIdentifier;
	}

	public final TFacade getFacade() throws DisposedException {
		synchronized (this) {
			switch (this.state) {
			case STATE_RESOLVED:
				if (this.modifying == null || !this.isModifiableOnCurrentThread()) {
					return this.value;
				} else {
					return this.modifying.value;
				}
			case STATE_CREATED:
				return this.isModifiableOnCurrentThread() ? this.modifying == null ? this.value : this.modifying.value : null;
			case STATE_REMOVED:
				return this.isModifiableOnCurrentThread() ? null : this.value;
			case STATE_DISPOSED:
				throw this.disposedException();
			default:
				throw new UnsupportedOperationException();
			}
		}
	}

	public final TFacade tryGetFacade() {
		return this.tryGetValue();
	}

	@SuppressWarnings("deprecation")
	public final ResourceToken<TFacade> getParent() {
		final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.ownGroup.forceGetBindTree();
		if (tree == null) {
			return null;
		} else {
			return tree.tryGetParentOf(this);
		}
	}

	@SuppressWarnings("deprecation")
	public final ResourceTokenLink<TFacade> getChildren() {
		final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.ownGroup.forceGetBindTree();
		if (tree == null) {
			return null;
		} else {
			return tree.tryGetChildrenOf(this);
		}
	}

	@SuppressWarnings("unchecked")
	public final <TSuperFacade> ResourceToken<TSuperFacade> getSuperToken(
			final Class<TSuperFacade> superTokenFacadeClass)
			throws IllegalArgumentException {
		ReferenceIndex<?>.Handle referenceHandle = this.firstOwnReferenceHandle;
		while (referenceHandle != null) {
			if (!referenceHandle.deletedInReference) {
				final CacheHolder<?, ?, ?> referenceByHolder = referenceHandle.ownReference().ownCacheHolder;
				if (referenceByHolder.ownGroup.define.facadeClass == superTokenFacadeClass && referenceHandle.isVisibleInCurrentThread()) {
					return (ResourceToken<TSuperFacade>) referenceByHolder;
				}
			}
			referenceHandle = referenceHandle.nextUndeletedInReference();
		}
		return null;
	}

	public final <TSubFacade> ResourceTokenLink<TSubFacade> getSubTokens(
			final Class<TSubFacade> subTokenFacadeClass)
			throws IllegalArgumentException {
		final List<CacheHolder<TSubFacade, ?, ?>> referenceHolderList;
		try {
			referenceHolderList = this.tryGetReferenceHolders(subTokenFacadeClass, this.isModifiableOnCurrentThread(), null);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException(e);
		}
		if (referenceHolderList == null) {
			throw this.disposedException();
		} else {
			if (referenceHolderList.size() == 0) {
				return null;
			} else {
				final ResourceTokenLinkImplement<TSubFacade> firstNode = new ResourceTokenLinkImplement<TSubFacade>(referenceHolderList.get(0));
				ResourceTokenLinkImplement<TSubFacade> lastNode = firstNode;
				for (int index = 1, endIndex = referenceHolderList.size(); index < endIndex; index++) {
					final ResourceTokenLinkImplement<TSubFacade> currentNode = new ResourceTokenLinkImplement<TSubFacade>(referenceHolderList.get(index));
					lastNode.next = currentNode;
					lastNode = currentNode;
				}
				return firstNode;
			}
		}
	}

	final TImplement tryGetValue() {
		synchronized (this) {
			switch (this.state) {
			case STATE_RESOLVED:
				if (this.modifying == null || !this.isModifiableOnCurrentThread()) {
					return this.value;
				} else {
					return this.modifying.value;
				}
			case STATE_CREATED:
				return this.isModifiableOnCurrentThread() ? this.modifying == null ? this.value : this.modifying.value : null;
			case STATE_REMOVED:
				return this.isModifiableOnCurrentThread() ? null : this.value;
			case STATE_DISPOSED:
				return null;
			default:
				throw new UnsupportedOperationException();
			}
		}
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	final <TReferenceFacade> List<TReferenceFacade> tryGetReferences(
			final Class<TReferenceFacade> referenceFacadeClass) {
		ReferenceIndex<?> existReferenceIndex;
		synchronized (this) {
			if (this.isDisposed()) {
				return null;
			} else {
				if (this.firstReferenceIndex == null) {
					return new ArrayList<TReferenceFacade>(0);
				} else {
					findIndex: {
						existReferenceIndex = this.firstReferenceIndex;
						while (existReferenceIndex != null) {
							if (existReferenceIndex.referenceDefine.referenceCacheDefine.facadeClass == referenceFacadeClass) {
								break findIndex;
							} else {
								existReferenceIndex = existReferenceIndex.next;
							}
						}
						return new ArrayList<TReferenceFacade>(0);
					}
				}
			}
		}
		return ((ReferenceIndex<TReferenceFacade>) existReferenceIndex).getReferences();
	}

	final void restoreSerialUserData(final Object userData,
			final ContextImpl<?, ?, ?> context) {
		context.restoreCacheHolderUserData(this.ownGroup.define.resourceService, userData, this.value, this.keysHolder);
	}

	final void restoreSerialUserDataDist(final Object userData,
			final ContextImpl<?, ?, ?> context) {
		switch (this.state) {
		case STATE_CREATED:
			context.restoreCacheHolderUserData(this.ownGroup.define.resourceService, userData, this.value, this.keysHolder);
			break;
		case STATE_RESOLVED:
			context.restoreCacheHolderUserData(this.ownGroup.define.resourceService, userData, this.modifying.value, this.modifying.keysHolder);
			break;
		}
	}
}
