package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.CacheDefine.ReferenceDefine;
import com.jiuqi.dna.core.type.GUID;

class AccessControlCacheHolder<TFacade, TImplement extends TFacade, TKeysHolder>
		extends CacheHolder<TFacade, TImplement, TKeysHolder> {

	AccessControlCacheHolder(
			final CacheGroup<TFacade, TImplement, TKeysHolder> ownGroup,
			final TImplement value, final TKeysHolder keysHolder,
			final Long fixLongIdentifier) {
		super(ownGroup, value, keysHolder, fixLongIdentifier);
		this.ACLongIdentifier = ownGroup.define.ownCache.ACIdentifierGenerator.next(ownGroup.inCluster);
		this.ACGUIDIdentifier = (GUID) (ownGroup.define.accessControlDefine.getKeyValue1(keysHolder));
	}

	@Override
	public final String toString() {
		return super.toString() + "\n" + "ACLongID:[" + this.ACLongIdentifier + "]\n" + "ACGUID:[" + this.ACGUIDIdentifier + "]";
	}

	final String getAccessControlTitle() {
		return super.tryGetAccessControlTitle(super.ownGroup.define.accessControlDefine.provider);
	}

	@Override
	final AccessControlCacheHolder<TFacade, TImplement, TKeysHolder> asAccessControlHolder() {
		return this;
	}

	final OperationEntry tryGetMappingOperationEntry(
			final OperationEntry operation, final CacheDefine<?, ?, ?> define) {
		final CacheDefine<TFacade, TImplement, TKeysHolder> thisDefine = super.ownGroup.define;
		if (define == thisDefine) {
			return operation;
		} else {
			return thisDefine.tryGetMappingOperationEntry(operation, define);
		}
	}

	// final AccessControlCacheHolder<?, ?, ?>
	// getNextHolderInAuthorityInheritPath_ForRefuseFirst(
	// final Transaction transaction) {
	// final CacheGroup<TFacade, TImplement, TKeysHolder> ownGroup =
	// super.ownGroup;
	// for (ReferenceDefine<?> ACReferenceDefine : ownGroup.define
	// .getAccessControlReferenceDefines()) {
	// ownGroup.ensureReferenceCacheInitialized(ACReferenceDefine,
	// transaction);
	// }
	// synchronized (this) {
	// final AccessControlCacheHolder<?, ?, ?>[] references = this
	// .tryGetAccessContorlReferences(this
	// .isModifiableOnTransaction(transaction));
	// if (references == null) {
	// final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.ownGroup
	// .forceGetBindTree();
	// if (tree == null) {
	// return null;
	// } else {
	// final CacheHolder<?, ?, ?> holder = tree.tryGetParentOf(
	// this, transaction);
	// if (holder == null) {
	// return null;
	// } else {
	// return holder.isDisposed() ? null : holder
	// .asAccessControlHolder();
	// }
	// }
	// } else {
	// return references[0];
	// }
	// }
	// }

	final AccessControlCacheHolder<?, ?, ?>[] getNextHolderInAuthorityInheritPath(
			final Transaction transaction) {
		if (this.isModifiableOnTransaction(transaction)) {
			return this.internalGetNextHolderInAuthorityInheritPath(transaction, true);
		} else {
			AccessControlCacheHolder<?, ?, ?>[] nextHolderInAuthorityInheritPath = this.cachedNextHolderInAuthorityInheritPath;
			if (nextHolderInAuthorityInheritPath == null) {
				nextHolderInAuthorityInheritPath = this.internalGetNextHolderInAuthorityInheritPath(transaction, false);
				this.cachedNextHolderInAuthorityInheritPath = nextHolderInAuthorityInheritPath;
			}
			return nextHolderInAuthorityInheritPath;
		}
	}

	final boolean getDefaultAuthority() {
		return super.ownGroup.define.accessControlDefine.defaultAuthority;
	}

	final void createACReferenctHandle(
			final ReferenceIndex<?>.Handle referenceHandle) {
		if (AccessControlPolicy.CURRENT_POLICY.singleInherit) {
			ACReferenceHandle existHandle = this.firstACReferenceHandle;
			while (existHandle != null) {
				switch (existHandle.referenceHandle.getState()) {
				case ReferenceIndex.HANDLE_STATE_RESOLVED:
				case ReferenceIndex.HANDLE_STATE_CREATED:
					throw new UnsupportedOperationException("不允许同时建立对多个资源的访问控制引用。");
				}
				existHandle = existHandle.next;
			}
		}
		final ACReferenceHandle newHandle = new ACReferenceHandle(referenceHandle);
		if (this.firstACReferenceHandle != null) {
			newHandle.next = this.firstACReferenceHandle;
		}
		this.firstACReferenceHandle = newHandle;
	}

	final void disposeACReferenceHandle(
			final ReferenceIndex<?>.Handle referenceHandle) {
		ACReferenceHandle existHandle = this.firstACReferenceHandle;
		ACReferenceHandle lastExistHandle = null;
		while (existHandle != null) {
			if (existHandle.referenceHandle == referenceHandle) {
				if (lastExistHandle == null) {
					this.firstACReferenceHandle = existHandle.next;
				} else {
					lastExistHandle.next = existHandle.next;
				}
				existHandle.next = null;
				break;
			}
			lastExistHandle = existHandle;
			existHandle = existHandle.next;
		}
	}

	final void resetCachedNextHolderInAuthorityInheritPath() {
		this.cachedNextHolderInAuthorityInheritPath = null;
	}

	private final AccessControlCacheHolder<?, ?, ?>[] internalGetNextHolderInAuthorityInheritPath(
			final Transaction transaction, final boolean isExclusiveTransaction) {
		final CacheGroup<TFacade, TImplement, TKeysHolder> ownGroup = super.ownGroup;
		final ReferenceDefine<?>[] referenceDefines = ownGroup.define.getAccessControlReferenceDefines();
		AccessControlCacheHolder<?, ?, ?>[] nextsInInheritPath = null;
		if (referenceDefines != null) {
			for (ReferenceDefine<?> ACReferenceDefine : referenceDefines) {
				ownGroup.ensureReferenceCacheInitialized(ACReferenceDefine, transaction);
			}
			if (this.firstACReferenceHandle != null) {
				synchronized (this) {
					if (!super.isDisposed()) {
						ACReferenceHandle existHandle = this.firstACReferenceHandle;
						if (isExclusiveTransaction) {
							while (existHandle != null) {
								switch (existHandle.referenceHandle.getState()) {
								case ReferenceIndex.HANDLE_STATE_RESOLVED:
								case ReferenceIndex.HANDLE_STATE_CREATED:
									if (nextsInInheritPath == null) {
										nextsInInheritPath = new AccessControlCacheHolder<?, ?, ?>[] { existHandle.referenceHandle.cacheHolder.asAccessControlHolder() };
									} else {
										final int oldCount = nextsInInheritPath.length;
										final AccessControlCacheHolder<?, ?, ?>[] newACReferences = new AccessControlCacheHolder<?, ?, ?>[oldCount + 1];
										System.arraycopy(nextsInInheritPath, 0, newACReferences, 0, oldCount);
										newACReferences[oldCount] = existHandle.referenceHandle.cacheHolder.asAccessControlHolder();
										nextsInInheritPath = newACReferences;
									}
								}
								existHandle = existHandle.next;
							}
						} else {
							while (existHandle != null) {
								switch (existHandle.referenceHandle.getState()) {
								case ReferenceIndex.HANDLE_STATE_RESOLVED:
								case ReferenceIndex.HANDLE_STATE_REMOVED:
									if (nextsInInheritPath == null) {
										nextsInInheritPath = new AccessControlCacheHolder<?, ?, ?>[] { existHandle.referenceHandle.cacheHolder.asAccessControlHolder() };
									} else {
										final int oldCount = nextsInInheritPath.length;
										final AccessControlCacheHolder<?, ?, ?>[] newACReferences = new AccessControlCacheHolder<?, ?, ?>[oldCount + 1];
										System.arraycopy(nextsInInheritPath, 0, newACReferences, 0, oldCount);
										newACReferences[oldCount] = existHandle.referenceHandle.cacheHolder.asAccessControlHolder();
										nextsInInheritPath = newACReferences;
									}
								}
								existHandle = existHandle.next;
							}
						}
					}
				}
			}
			if (nextsInInheritPath != null) {
				return nextsInInheritPath;
			}
		}
		final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.ownGroup.forceGetBindTree();
		if (tree != null) {
			final CacheHolder<?, ?, ?> holder = tree.tryGetParentOf(this, transaction);
			if (holder != null) {
				nextsInInheritPath = new AccessControlCacheHolder<?, ?, ?>[] { holder.asAccessControlHolder() };
			}
		}
		return nextsInInheritPath;
	}

	final long ACLongIdentifier;

	final GUID ACGUIDIdentifier;

	private volatile ACReferenceHandle firstACReferenceHandle;

	private volatile AccessControlCacheHolder<?, ?, ?>[] cachedNextHolderInAuthorityInheritPath;

	private static final class ACReferenceHandle {

		private ACReferenceHandle(final ReferenceIndex<?>.Handle referenceHandle) {
			this.referenceHandle = referenceHandle;
		}

		final ReferenceIndex<?>.Handle referenceHandle;

		volatile ACReferenceHandle next;

	}
	
	final GUID getACGUIDIdentifier() {
		return this.ACGUIDIdentifier;
	}
}
