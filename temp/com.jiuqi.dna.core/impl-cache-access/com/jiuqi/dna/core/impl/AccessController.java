package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.ActorAuthorityChecker;
import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.type.GUID;

abstract class AccessController implements ActorAuthorityChecker {

	static final Authority getAuthority(final long[] ACL,
			final OperationEntry operation, final long holderIdentifier) {
		final long authorityCode = AccessControlHelper.getAuthority(ACL, holderIdentifier) & operation.authorityMask;
		if (authorityCode == 0L) {
			return Authority.UNDEFINE;
		} else if (authorityCode == operation.allowAuthorityCode) {
			return Authority.ALLOW;
		} else {
			return Authority.DENY;
		}
	}

	AccessController(final GUID accessControlVersion,
			final Transaction transaction) {
		this.accessControlVersion = accessControlVersion;
		this.transaction = transaction;
	}

	public final GUID getOrgID() {
		return this.accessControlVersion;
	}

	public <TFacade> Authority getAuthority(
			final Operation<? super TFacade> operation,
			final ResourceToken<TFacade> holder) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (holder instanceof AccessControlCacheHolder<?, ?, ?>) {
			final AccessControlCacheHolder<?, ?, ?> ACHolder = (AccessControlCacheHolder<?, ?, ?>) holder;
			// final ContextImpl<?, ?, ?> context = this.transaction
			// .getCurrentContext();
			// final ResourceServiceBase<?, ?, ?> resourceService =
			// ACHolder.ownGroup.define.resourceService;
			// resourceService.callBeforeAccessAuthorityResource(context);
			// try {
			return this.internalGetAuthority(OperationEntry.operationEntryOf(operation, ACHolder.ownGroup.define.accessControlDefine.operationEntrys), ACHolder);
			// } finally {
			// resourceService.callEndAccessAuthorityResource(context);
			// }
		} else if (holder instanceof AccessControlCacheHolderOfGroup) {
			final AccessControlCacheHolderOfGroup ACGroup = (AccessControlCacheHolderOfGroup) holder;
			// final ContextImpl<?, ?, ?> context = this.transaction
			// .getCurrentContext();
			// final ResourceServiceBase<?, ?, ?> resourceService =
			// ACGroup.cacheGroup.define.resourceService;
			// resourceService.callBeforeAccessAuthorityResource(context);
			// try {
			return this.internalGetAuthority(OperationEntry.operationEntryOf(operation, ACGroup.cacheGroup.define.accessControlDefine.operationEntrys), ACGroup);
			// } finally {
			// resourceService.callEndAccessAuthorityResource(context);
			// }
		} else {
			throw new UnsupportedAccessControlException(holder.getFacadeClass());
		}
	}

	public <TFacade> boolean hasAuthority(
			final Operation<? super TFacade> operation,
			final ResourceToken<TFacade> holder) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (holder instanceof AccessControlCacheHolder<?, ?, ?>) {
			final AccessControlCacheHolder<?, ?, ?> ACHolder = (AccessControlCacheHolder<?, ?, ?>) holder;
			// final ContextImpl<?, ?, ?> context = this.transaction
			// .getCurrentContext();
			// final ResourceServiceBase<?, ?, ?> resourceService =
			// ACHolder.ownGroup.define.resourceService;
			// resourceService.callBeforeAccessAuthorityResource(context);
			// try {
			return this.internalHasAuthority(OperationEntry.operationEntryOf(operation, ACHolder.ownGroup.define.accessControlDefine.operationEntrys), ACHolder);
			// } finally {
			// resourceService.callEndAccessAuthorityResource(context);
			// }
		} else if (holder instanceof AccessControlCacheHolderOfGroup) {
			final AccessControlCacheHolderOfGroup ACGroup = (AccessControlCacheHolderOfGroup) holder;
			// final ContextImpl<?, ?, ?> context = this.transaction
			// .getCurrentContext();
			// final ResourceServiceBase<?, ?, ?> resourceService =
			// ACGroup.cacheGroup.define.resourceService;
			// resourceService.callBeforeAccessAuthorityResource(context);
			// try {
			return this.internalHasAuthority(OperationEntry.operationEntryOf(operation, ACGroup.cacheGroup.define.accessControlDefine.operationEntrys), ACGroup);
			// } finally {
			// resourceService.callEndAccessAuthorityResource(context);
			// }
		} else {
			throw new UnsupportedAccessControlException(holder.getFacadeClass());
		}
	}

	abstract Authority internalGetAuthority(OperationEntry operation,
			AccessControlCacheHolder<?, ?, ?> holder);

	abstract Authority internalGetAuthority(OperationEntry operation,
			AccessControlCacheHolderOfGroup group);

	abstract boolean internalHasAuthority(OperationEntry operation,
			AccessControlCacheHolder<?, ?, ?> holder);

	abstract boolean internalHasAuthority(OperationEntry operation,
			AccessControlCacheHolderOfGroup group);

	abstract boolean internalHasAuthority(OperationEntry operation,
			AccessControlCacheHolder<?, ?, ?> holder,
			boolean defaultAuthority);

	abstract AccessControlPolicy getPolicy();

	final Transaction transaction;

	private final GUID accessControlVersion;

}