package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.auth.Role;
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

final class IdentifyCacheHolder extends
		CacheHolder<Identify, Identify, Identify> {

	IdentifyCacheHolder(
			final CacheGroup<Identify, Identify, Identify> ownGroup,
			final Identify value, final Identify keysHolder,
			final Long fixLongIdentifier) {
		super(ownGroup, value, keysHolder, fixLongIdentifier);
		this.identifier = value.userIdentifier;
		this.ACVersion = value.identifyIdentifier;
	}

	private static final <TFacade, TImplement extends TFacade, TKeysHolder> void processOperationAuthorityInformation(
			final ACGroupContainer ACGroupContainer,
			final IdentifyCacheHolder identifyHolder,
			final OperationAuthorityInformation authorityInformation,
			final Transaction transaction) {
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
		identifyHolder.operationACL = operationACL;
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

	final GUID identifier;

	final GUID ACVersion;

	final void resetACL() {
		if (this.loadedACL) {
			synchronized (this.actorLock) {
				if (this.loadedACL) {
					this.loadedACL = false;
					this.operationACL = null;
					this.modifingOperationACL = null;
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
		}
	}

	final long[] tryGetAccreditACL(final Transaction transaction) {
		final Context context = transaction.getCurrentContext();
		long[] ACL;
		final AccreditAuthorityInformation authorityInformation = context.get(AccreditAuthorityInformation.class, new GetAccreditAuthorityInformationKey(// forUser,
		this.identifier, this.ACVersion));
		if (!authorityInformation.noSuchACVersion) {
			ACL = processAccreditAuthorityInformation(this.ownGroup.define.ownCache.ACGroupContainer, authorityInformation, transaction);
			context.handle(new FinishInitializeAccreditAuthorityTask(this.identifier, this.ACVersion, authorityInformation));
		} else {
			ACL = AccessControlHelper.EMPTY_ACL;
		}
		if (super.isDisposed()) {
			throw new DisposedException("缓存项已被销毁。");
		} else {
			return ACL;
		}
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

	private volatile boolean loadedACL;

	private long[] operationACL;

	private long[] modifingOperationACL;
	
	private boolean needResetInCluster;

	@SuppressWarnings("unchecked")
	final long[][] tryGetOperationACLSnap(final Transaction transaction) {
		@SuppressWarnings("rawtypes")
		final List<RoleCacheHolder> assignedRoleList = (List) this.tryGetReferenceHolders(Role.class, this.isModifiableOnTransaction(transaction), transaction);
		// synchronized (this.actorLock) {
		final long[][] ACLSnap = new long[assignedRoleList.size() + 1][];
		final long[] userACL = this.tryGetOperationACL(transaction);
		ACLSnap[0] = userACL;
		for (int index = 0, endIndex = assignedRoleList.size(); index < endIndex;) {
			final long[] ACL = assignedRoleList.get(index).tryGetOperationACL(transaction);
			ACLSnap[++index] = ACL;
		}
		return ACLSnap;
		// }
	}

	@SuppressWarnings("unchecked")
	final long[][] tryGetAccreditACLSnap(final Transaction transaction) {
		@SuppressWarnings("rawtypes")
		final List<RoleCacheHolder> assignedRoleList = (List) this.tryGetReferenceHolders(Role.class, this.isModifiableOnTransaction(transaction), transaction);
		// synchronized (this.actorLock) {
		final long[][] ACLSnap = new long[assignedRoleList.size() + 1][];
		final long[] userACL = this.tryGetAccreditACL(transaction);
		ACLSnap[0] = userACL;
		for (int index = 0, endIndex = assignedRoleList.size(); index < endIndex;) {
			final long[] ACL = assignedRoleList.get(index).tryGetAccreditACL(transaction);
			ACLSnap[++index] = ACL;
		}
		return ACLSnap;
		// }
	}

}
