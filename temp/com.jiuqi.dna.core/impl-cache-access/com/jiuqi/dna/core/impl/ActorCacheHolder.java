package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.exception.DisposedException;
import com.jiuqi.dna.core.impl.Cache.ACGroupContainer;
import com.jiuqi.dna.core.spi.auth.callback.ACEntryException_InvalidEntry;
import com.jiuqi.dna.core.spi.auth.callback.AccessControlEntry;
import com.jiuqi.dna.core.spi.auth.callback.AccreditAuthorityInformation;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeAccreditAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeOperationAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.GetAccreditAuthorityInformationKey;
import com.jiuqi.dna.core.spi.auth.callback.GetOperationAuthorityInformationKey;
import com.jiuqi.dna.core.spi.auth.callback.OperationAuthorityInformation;
import com.jiuqi.dna.core.type.GUID;

abstract class ActorCacheHolder<TFacade, TImplement extends TFacade, TKeysHolder>
		extends AccessControlCacheHolder<TFacade, TImplement, TKeysHolder> {

	// private static final class ModifiableAC {
	//
	// private ModifiableAC(final GUID[] ACVersions, final long[][] ACLs) {
	// this.ACVersions = ACVersions.clone();
	// if (ACLs == null) {
	// this.operationACLs = new long[ACVersions.length][];
	// } else {
	// this.operationACLs = ACLs.clone();
	// }
	// }
	//
	// private final void createOperationACL(final GUID ACVersion,
	// final long[] ACL) {
	// final int versionCount = this.ACVersions.length;
	// for (int index = 1, endIndex = versionCount; index < endIndex;
	// index++) {
	// final GUID version = this.ACVersions[index];
	// if (ACVersion.equals(version)) {
	// this.operationACLs[index] = ACL;
	// return;
	// }
	// }
	// final GUID[] newACVersions = new GUID[versionCount + 1];
	// final long[][] newACLs = new long[versionCount + 1][];
	// System.arraycopy(this.ACVersions, 0, newACVersions, 0,
	// versionCount);
	// System.arraycopy(this.operationACLs, 0, newACLs, 0,
	// versionCount);
	// newACVersions[versionCount] = ACVersion;
	// newACLs[versionCount] = ACL;
	// this.ACVersions = newACVersions;
	// this.operationACLs = newACLs;
	// }
	//
	// private final long[] modifyOperationACL(final GUID ACVersion) {
	// if (AccessControlConstants.isDefaultACVersion(ACVersion)) {
	// return this.operationACLs[0].clone();
	// }
	// final int versionCount = this.ACVersions.length;
	// for (int index = 1, endIndex = versionCount; index < endIndex;
	// index++) {
	// final GUID version = this.ACVersions[index];
	// if (ACVersion.equals(version)) {
	// return this.operationACLs[index].clone();
	// }
	// }
	// return null;
	// }
	//
	// private final long[] removeOperationACL(final GUID ACVersion) {
	// int versionCount = this.ACVersions.length;
	// for (int index = 1, endIndex = versionCount; index < endIndex;
	// index++) {
	// final GUID version = this.ACVersions[index];
	// if (ACVersion.equals(version)) {
	// final long[] ACL = this.operationACLs[index];
	// versionCount--;
	// final GUID[] newACVersions = new GUID[versionCount];
	// final long[][] newACLs = new long[versionCount][];
	// System.arraycopy(this.ACVersions, 0, newACVersions, 0,
	// index);
	// System.arraycopy(this.operationACLs, 0, newACLs, 0,
	// index);
	// if (index < versionCount) {
	// System.arraycopy(this.ACVersions, index + 1,
	// newACVersions, index, versionCount - index);
	// System.arraycopy(this.operationACLs, index + 1,
	// newACLs, index, versionCount - index);
	// }
	// this.ACVersions = newACVersions;
	// this.operationACLs = newACLs;
	// return ACL == null ? AccessControlHelper.EMPTY_ACL
	// : ACL;
	// }
	// }
	// return null;
	// }
	//
	// private final void postModifiedOperationACL(final GUID ACVersion,
	// final long[] ACL) {
	// if (AccessControlConstants.isDefaultACVersion(ACVersion)) {
	// this.operationACLs[0] = ACL;
	// return;
	// }
	// final int versionCount = this.ACVersions.length;
	// for (int index = 1, endIndex = versionCount; index < endIndex;
	// index++) {
	// final GUID version = this.ACVersions[index];
	// if (ACVersion.equals(version)) {
	// this.operationACLs[index] = ACL;
	// return;
	// }
	// }
	// throw new UnsupportedOperationException();
	// }
	//
	// private GUID[] ACVersions;
	//
	// private long[][] operationACLs;
	//
	// }

	private static final <TFacade, TImplement extends TFacade, TKeysHolder> void processOperationAuthorityInformation(
			final ACGroupContainer ACGroupContainer,
			final ActorCacheHolder<TFacade, TImplement, TKeysHolder> actorHolder,
			final OperationAuthorityInformation authorityInformation,
			final Transaction transaction) {
		// final ArrayList<ACVersionEntry> ACVersionEntryList =
		// authorityInformation.ACVersionEntryList;
		final ArrayList<AccessControlEntry.AuthorityEntry> authorityEntryList = authorityInformation.authorityEntryList;
		long[] operationACL = AccessControlHelper.EMPTY_ACL;
		for (int index = 0, endIndex = authorityEntryList.size(); index < endIndex; index++) {
			final AccessControlEntry.AuthorityEntry authorityEntry = authorityEntryList.get(index);
			final CacheGroup<?, ?, ?> group = ACGroupContainer.get(authorityEntry.groupIdentifier);
			if (group != null) {
				final AccessControlCacheHolder<?, ?, ?> ACHolder = group.accessControlInformation.accessControlIndex.findAccessControlHolder(authorityEntry.itemIdentifier, transaction);
				if (ACHolder != null) {
					operationACL = AccessControlHelper.setAuthority(operationACL, ACHolder.ACLongIdentifier, authorityEntry.dataItemList);
					continue;
				}
			}
			authorityEntry.exception = ACEntryException_InvalidEntry.INSTANCE;
		}
		actorHolder.operationACL = operationACL;
		// if (ACVersionEntryList.size() == 0) {
		// actorHolder.ACVersions = DEFAULT_ACVERSIONS;
		// long[] operationACL = AccessControlHelper.EMPTY_ACL;
		// for (int index = 0, endIndex = authorityEntryList.size(); index <
		// endIndex; index++) {
		// final OperationAuthorityEntry authorityEntry = authorityEntryList
		// .get(index);
		// if (AccessControlConstants
		// .isDefaultACVersion(authorityEntry.ACVersion)) {
		// final CacheGroup<?, ?, ?> group = ACGroupContainer
		// .get(authorityEntry.groupIdentifier);
		// if (group != null) {
		// final AccessControlCacheHolder<?, ?, ?> ACHolder =
		// group.accessControlInformation.accessControlIndex
		// .findAccessControlHolder(
		// authorityEntry.itemIdentifier,
		// transaction);
		// if (ACHolder != null) {
		// operationACL = AccessControlHelper
		// .setAuthority(operationACL,
		// ACHolder.ACLongIdentifier,
		// authorityEntry.dataItemList);
		// continue;
		// }
		// }
		// }
		// authorityEntry.exception =
		// ACEntryException_InvalidEntry.INSTANCE;
		// }
		// actorHolder.operationACLs = new long[][] { operationACL };
		// } else {
		// final ArrayList<GUID> ACVersionList = new ArrayList<GUID>();
		// ACVersionList.add(null);
		// for (int index = 0, endIndex = ACVersionEntryList.size(); index <
		// endIndex; index++) {
		// final ACVersionEntry ACVersionEntry = ACVersionEntryList
		// .get(index);
		// final GUID ACVersion = ACVersionEntry.ACVersion;
		// if (AccessControlConstants.isDefaultACVersion(ACVersion)
		// || ACVersionList.contains(ACVersion)) {
		// ACVersionEntry.exception =
		// ACEntryException_InvalidEntry.INSTANCE;
		// } else {
		// ACVersionList.add(ACVersion);
		// }
		// }
		// final int ACVersionCount = ACVersionList.size();
		// final long[][] operationACLs = new long[ACVersionCount][];
		// Arrays.fill(operationACLs, AccessControlHelper.EMPTY_ACL);
		// for (int index = 0, endIndex = authorityEntryList.size(); index <
		// endIndex; index++) {
		// final OperationAuthorityEntry authorityEntry = authorityEntryList
		// .get(index);
		// int ACVersionIndex;
		// final GUID ACVersion = authorityEntry.ACVersion;
		// if (AccessControlConstants.isDefaultACVersion(ACVersion)) {
		// ACVersionIndex = 0;
		// } else {
		// ACVersionIndex = 1;
		// getACVersionIndex: {
		// for (; ACVersionIndex < ACVersionCount; ACVersionIndex++) {
		// if (ACVersionList.get(ACVersionIndex).equals(
		// ACVersion)) {
		// break getACVersionIndex;
		// }
		// }
		// authorityEntry.exception =
		// ACEntryException_InvalidEntry.INSTANCE;
		// continue;
		// }
		// }
		// final CacheGroup<?, ?, ?> group = cache.ACGroupContainer
		// .get(authorityEntry.groupIdentifier);
		// if (group != null) {
		// final AccessControlCacheHolder<?, ?, ?> ACHolder =
		// group.accessControlInformation.accessControlIndex
		// .findAccessControlHolder(
		// authorityEntry.itemIdentifier,
		// transaction);
		// if (ACHolder != null) {
		// operationACLs[ACVersionIndex] = AccessControlHelper
		// .setAuthority(
		// operationACLs[ACVersionIndex],
		// ACHolder.ACLongIdentifier,
		// authorityEntry.dataItemList);
		// continue;
		// }
		// }
		// authorityEntry.exception =
		// ACEntryException_InvalidEntry.INSTANCE;
		// }
		// actorHolder.ACVersions = new GUID[ACVersionCount];
		// ACVersionList.toArray(actorHolder.ACVersions);
		// actorHolder.operationACLs = operationACLs;
		// }
	}

	private static final long[] processAccreditAuthorityInformation(
			final ACGroupContainer ACGroupContainer,
			final AccreditAuthorityInformation authorityInformation,
			final Transaction transaction) {
		if (authorityInformation.noSuchACVersion) {
			return null;
		} else {
			long[] accreditACL = AccessControlHelper.EMPTY_ACL;
			final List<AccessControlEntry.AuthorityEntry> authorityEntryList = authorityInformation.authorityEntryList;
			for (int index = 0, endIndex = authorityEntryList.size(); index < endIndex; index++) {
				final AccessControlEntry.AuthorityEntry authorityEntry = authorityEntryList.get(index);
				final CacheGroup<?, ?, ?> group = ACGroupContainer.get(authorityEntry.groupIdentifier);
				if (group != null) {
					final AccessControlCacheHolder<?, ?, ?> ACHolder = group.accessControlInformation.accessControlIndex.findAccessControlHolder(authorityEntry.itemIdentifier, transaction);
					if (ACHolder != null) {
						accreditACL = AccessControlHelper.setAuthority(accreditACL, ACHolder.ACLongIdentifier, authorityEntry.dataItemList);
						continue;
					}
				}
				authorityEntry.exception = ACEntryException_InvalidEntry.INSTANCE;
			}
			return accreditACL;
		}
	}

	// private static final GUID[] DEFAULT_ACVERSIONS;
	//
	// static {
	// DEFAULT_ACVERSIONS = new GUID[] { null };
	// }

	ActorCacheHolder(
			final CacheGroup<TFacade, TImplement, TKeysHolder> ownGroup,
			final GUID identifier, final GUID ACVersion,
			final TImplement value, final TKeysHolder keysHolder,
			final Long fixLongIdentifier) {
		super(ownGroup, value, keysHolder, fixLongIdentifier);
		this.identifier = identifier;
		this.ACVersion = ACVersion;
		this.resetACL();
	}

	final GUID identifier;

	final GUID ACVersion;

	final void resetACL() {
		if (this.loadedACL) {
			synchronized (this.actorLock) {
				if (this.loadedACL) {
					this.loadedACL = false;
					this.operationACL = null;
					this.modifingOperationACL = null;
					// this.ACVersions = null;
					// this.operationACLs = null;
					// this.modifingAC = null;
				}
			}
		}
	}
	
	final void resetACLInCluster(final Transaction transaction) {
		synchronized (this.actorLock) {
			this.loadedACL = false;
			this.operationACL = null;
			this.modifingOperationACL = null;
			this.needResetInCluster = true;
			
			transaction.handleAcquirable(this, AcquireFor.MODIFY);
			// this.ACVersions = null;
			// this.operationACLs = null;
			// this.modifingAC = null;
		}
	}

	final long[] tryGetAccreditACL(final Transaction transaction) {
		final Context context = transaction.getCurrentContext();
		// final boolean forUser = this.asUserHolder() != null;
		long[] ACL;
		final AccreditAuthorityInformation authorityInformation = context.get(AccreditAuthorityInformation.class, new GetAccreditAuthorityInformationKey(// forUser,
		this.identifier, this.ACVersion));
		if (!authorityInformation.noSuchACVersion) {
			ACL = processAccreditAuthorityInformation(this.ownGroup.define.ownCache.ACGroupContainer, authorityInformation, transaction);
			context.handle(new FinishInitializeAccreditAuthorityTask(
			// forUser,
			this.identifier, this.ACVersion, authorityInformation));
		} else {
			ACL = AccessControlHelper.EMPTY_ACL;
		}
		if (super.isDisposed()) {
			throw new DisposedException("缓存项已被销毁。");
		} else {
			return ACL;
		}
		// while (true) {
		// if (AccessControlConstants.isDefaultACVersion(ACVersion)
		// || !AccessControlHelper.isEmpty(ACL)) {
		// if (super.isDisposed()) {
		// throw new DisposedException("缓存项已被销毁。");
		// } else {
		// return ACL;
		// }
		// }
		// ACVersion = null;
		// }
	}

	final long[] tryGetOperationACL(final Transaction transaction) {
		// while (true) {
		synchronized (this.actorLock) {
			this.tryLoadACL(transaction);
			if (super.isDisposed()) {
				throw new DisposedException("缓存项已被销毁。");
			} else {
				// if (this.loadedACL) {
				if (this.modifingOperationACL == null || !this.isModifiableOnTransaction(transaction)) {
					return this.operationACL;
				} else {
					return this.modifingOperationACL;
				}
				// }
			}
		}
		// }
	}

	//
	// final long[] tryGetAccreditACL(GUID ACVersion,
	// final Transaction transaction) {
	// final Context context = transaction.getCurrentContext();
	// // final boolean forUser = this.asUserHolder() != null;
	// long[] ACL;
	// while (true) {
	// final AccreditAuthorityInformation authorityInformation = context
	// .get(AccreditAuthorityInformation.class,
	// new GetAccreditAuthorityInformationKey(// forUser,
	// this.ACGUIDIdentifier, ACVersion));
	// if (!authorityInformation.noSuchACVersion) {
	// ACL = processAccreditAuthorityInformation(
	// this.ownGroup.define.ownCache.ACGroupContainer,
	// authorityInformation, transaction);
	// context.handle(new FinishInitializeAccreditAuthorityTask(
	// // forUser,
	// this.ACGUIDIdentifier, ACVersion,
	// authorityInformation));
	// } else {
	// ACL = AccessControlHelper.EMPTY_ACL;
	// }
	// if (AccessControlConstants.isDefaultACVersion(ACVersion)
	// || !AccessControlHelper.isEmpty(ACL)) {
	// if (super.isDisposed()) {
	// throw new DisposedException("缓存项已被销毁。");
	// } else {
	// return ACL;
	// }
	// }
	// ACVersion = null;
	// }
	// }
	// final boolean hasACVersion(final GUID ACVersion,
	// final Transaction transaction) {
	// if (AccessControlConstants.isDefaultACVersion(ACVersion)) {
	// return true;
	// } else {
	// while (true) {
	// this.tryLoadACL(transaction);
	// synchronized (this.actorLock) {
	// if (super.isDisposed()) {
	// throw new DisposedException("缓存项已被销毁。");
	// } else {
	// if (this.loadedACL) {
	// final GUID[] ACVersions;
	// if (this.modifingAC == null
	// || !super
	// .isModifiableOnTransaction(transaction)) {
	// ACVersions = this.ACVersions;
	// } else {
	// ACVersions = this.modifingAC.ACVersions;
	// }
	// for (GUID existACVersion : ACVersions) {
	// if (ACVersion.equals(existACVersion)) {
	// return true;
	// }
	// }
	// return false;
	// }
	// }
	// }
	// }
	// }
	// }
	//
	// final long[] tryGetOperationACL(final GUID ACVersion,
	// final Transaction transaction) {
	// return this.getOperationACL(ACVersion, transaction, true);
	// }
	//
	// final long[] getOperationACL(final GUID ACVersion,
	// final Transaction transaction, final boolean returnDefault) {
	// while (true) {
	// this.tryLoadACL(transaction);
	// synchronized (this.actorLock) {
	// if (super.isDisposed()) {
	// throw new DisposedException("缓存项已被销毁。");
	// } else {
	// if (this.loadedACL) {
	// if (AccessControlConstants
	// .isDefaultACVersion(ACVersion)) {
	// if (this.modifingAC == null
	// || !this.isModifiableOnTransaction(transaction)) {
	// return this.operationACLs[0];
	// } else {
	// return this.modifingAC.operationACLs[0];
	// }
	// } else {
	// final GUID[] ACVersions;
	// final long[][] ACLs;
	// if (this.modifingAC == null
	// || !this.isModifiableOnTransaction(transaction)) {
	// ACVersions = this.ACVersions;
	// ACLs = this.operationACLs;
	// } else {
	// ACVersions = this.modifingAC.ACVersions;
	// ACLs = this.modifingAC.operationACLs;
	// }
	// for (int index = 1, endIndex = ACVersions.length; index < endIndex;
	// index++) {
	// if (ACVersion.equals(ACVersions[index])) {
	// final long[] ACL = ACLs[index];
	// if (AccessControlHelper.isEmpty(ACL)) {
	// break;
	// } else {
	// return ACLs[index];
	// }
	// }
	// }
	// return returnDefault ? ACLs[0] : null;
	// }
	// }
	// }
	// }
	// }
	// }
	//
	// final void localCreateOperationACL(final GUID ACVersion, long[] ACL,
	// final Transaction transaction) {
	// transaction.handleAcquirable(this, AcquireFor.MODIFY);
	// if (super.isDisposed()) {
	// throw new DisposedException("缓存项已被销毁。");
	// } else {
	// this.tryLoadACL(transaction);
	// if (this.modifingAC == null) {
	// this.modifingAC = new ModifiableAC(this.ACVersions,
	// this.operationACLs);
	// }
	// this.modifingAC.createOperationACL(ACVersion, ACL);
	// }
	// }
	//
	// final long[] localModifyOperationACL(final GUID ACVersion,
	// final Transaction transaction) {
	// transaction.handleAcquirable(this, AcquireFor.MODIFY);
	// if (super.isDisposed()) {
	// throw new DisposedException("缓存项已被销毁。");
	// } else {
	// this.tryLoadACL(transaction);
	// if (this.modifingAC == null) {
	// this.modifingAC = new ModifiableAC(this.ACVersions,
	// this.operationACLs);
	// }
	// return this.modifingAC.modifyOperationACL(ACVersion);
	// }
	// }
	//
	// final long[] localRemoveOperationACL(final GUID ACVersion,
	// final Transaction transaction) {
	// transaction.handleAcquirable(this, AcquireFor.MODIFY);
	// if (super.isDisposed()) {
	// throw new DisposedException("缓存项已被销毁。");
	// } else {
	// this.tryLoadACL(transaction);
	// if (this.modifingAC == null) {
	// this.modifingAC = new ModifiableAC(this.ACVersions,
	// this.operationACLs);
	// }
	// return this.modifingAC.removeOperationACL(ACVersion);
	// }
	// }
	//
	// final void postModifiedOperationACL(final GUID ACVersion, long[] ACL,
	// final Transaction transaction) {
	// if (AccessControlHelper.isEmpty(ACL)) {
	// ACL = AccessControlHelper.EMPTY_ACL;
	// }
	// if (this.modifingAC == null
	// || !this.isModifiableOnTransaction(transaction)) {
	// throw new CacheStateError();
	// } else {
	// this.modifingAC.postModifiedOperationACL(ACVersion, ACL);
	// }
	// }

	final long[] localModifyOperationACL(final Transaction transaction) {
		transaction.handleAcquirable(this, AcquireFor.MODIFY);
		if (super.isDisposed()) {
			throw new DisposedException("缓存项已被销毁。");
		} else {
			this.tryLoadACL(transaction);
			if (this.modifingOperationACL == null) {
				this.modifingOperationACL = this.operationACL.clone();
			}
			return this.modifingOperationACL;
		}
	}

	final void postModifiedOperationACL(long[] ACL,
			final Transaction transaction) {
		if (AccessControlHelper.isEmpty(ACL)) {
			ACL = AccessControlHelper.EMPTY_ACL;
		}
		if (this.modifingOperationACL == null || !this.isModifiableOnTransaction(transaction)) {
			throw new CacheStateError();
		} else {
			this.modifingOperationACL = ACL;
		}
	}

	@Override
	final void collectModifiedHolderData(
			final CacheSynchronizeCollector collector,
			final Transaction transaction) {
		super.collectModifiedHolderData(collector, transaction);
		if (this.modifingOperationACL != null) {
			collector.addReloadAuhtorityData(this);
			return;
		}
		if (this.needResetInCluster) {
			collector.addReloadAuhtorityData(this);
			this.needResetInCluster = false;
		}
	}

	@Override
	final void onTransactionCommit(final Transaction transaction) {
		super.onTransactionCommit(transaction);
		synchronized (this.actorLock) {
			if (this.modifingOperationACL != null) {
				this.operationACL = this.modifingOperationACL;
				this.modifingOperationACL = null;
			}
		}
	}

	@Override
	final void onTransactionRollback(final Transaction transaction) {
		super.onTransactionRollback(transaction);
		this.modifingOperationACL = null;
	}

	private final void tryLoadACL(final Transaction transaction) {
		if (!this.loadedACL) {
			synchronized (this.actorLock) {
				if (!this.loadedACL) {
					final ContextImpl<?, ?, ?> context = transaction.getCurrentContext();
					// 从数据库获取权限信息
					final OperationAuthorityInformation authorityInformation = context.get(OperationAuthorityInformation.class, new GetOperationAuthorityInformationKey(this.identifier, this.ACVersion));
					// 处理权限信息
					processOperationAuthorityInformation(this.ownGroup.define.ownCache.ACGroupContainer, this, authorityInformation, transaction);
					this.loadedACL = true;
					// 清理数据库无效数据
					context.handle(new FinishInitializeOperationAuthorityTask(this.identifier, this.ACVersion, authorityInformation));
				}
			}
		}
	}

	protected final Object actorLock = new Object();

	// private volatile byte ACInitializeState;

	private volatile boolean loadedACL;

	private long[] operationACL;

	// private volatile GUID[] ACVersions;
	//
	// private volatile long[][] operationACLs;
	//
	// private volatile ModifiableAC modifingAC;

	private long[] modifingOperationACL;
	
	private boolean needResetInCluster;

	// private final GUID[] getACVersions() {
	// GUID[] ACVersions;
	// ModifiableAC modifingAC;
	// synchronized (this.actorLock) {
	// modifingAC = this.modifingAC;
	// if (modifingAC == null) {
	// return this.ACVersions;
	// }
	// ACVersions = this.ACVersions;
	// }
	// if (super.isModifiableOnCurrentThread()) {
	// ACVersions = modifingAC.ACVersions;
	// }
	// return ACVersions;
	// }

	@Deprecated
	public final int getMappingOrganizationCount() {
		if (this instanceof UserCacheHolder) {
			return this.tryGetReferenceHolders(Identify.class, super.isModifiableOnCurrentThread(), null).size();
		} else {
			throw new UnsupportedOperationException("非用户对象不支持到组织身份的映射。");
		}
	}

	@Deprecated
	public final GUID getMappingOrganizationID(int index) {
		if (this instanceof UserCacheHolder) {
			return this.tryGetReferenceHolders(Identify.class, super.isModifiableOnCurrentThread(), null).get(index).getFacade().identifyIdentifier;
		} else {
			throw new UnsupportedOperationException("非用户对象不支持到组织身份的映射。");
		}
	}

}