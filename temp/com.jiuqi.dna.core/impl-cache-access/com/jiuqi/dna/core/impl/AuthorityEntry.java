package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.type.GUID;

public abstract class AuthorityEntry {

	private static final int buildAuthorityInformation(
			final AccessController accessController,
			final OperationEntry[] operationEntrys,
			final AccessControlCacheHolderOfGroup group) {
		int authorityInformation = 0;
		for (OperationEntry operationEntry : operationEntrys) {
			if (accessController.internalHasAuthority(operationEntry, group)) {
				authorityInformation |= operationEntry.operation.getMask();
			}
		}
		return authorityInformation;
	}

	private static final int buildAuthorityInformation(
			final AccessController accessController,
			final OperationEntry[] operationEntrys,
			final AccessControlCacheHolder<?, ?, ?> holder) {
		int authorityInformation = 0;
		for (OperationEntry operationEntry : operationEntrys) {
			if (accessController.internalHasAuthority(operationEntry, holder)) {
				authorityInformation |= operationEntry.operation.getMask();
			}
		}
		return authorityInformation;
	}

	protected AuthorityEntry(final ResourceToken<?> resourceToken) {
		if (resourceToken == null) {
			throw new NullArgumentException("resourceToken");
		}
		if (resourceToken instanceof AccessControlCacheHolderOfGroup) {
			this.accessControlHolder = null;
			this.accessControlGroup = (AccessControlCacheHolderOfGroup) resourceToken;
		} else if (resourceToken instanceof AccessControlCacheHolder<?, ?, ?>) {
			this.accessControlHolder = (AccessControlCacheHolder<?, ?, ?>) resourceToken;
			this.accessControlGroup = this.accessControlHolder.ownGroup.accessControlInformation.accessControlCacheItem;
		} else {
			throw new UnsupportedAccessControlException(resourceToken.getFacadeClass());
		}
		this.filled = false;
		this.changed = false;
		this.authorityInformation = 0xFFFF0000;
	}

	public final ResourceToken<?> getAccessControlItem() {
		return this.accessControlHolder == null ? this.accessControlGroup : this.accessControlHolder;
	}

	public Operation<?>[] getOperations() {
		return this.accessControlGroup.cacheGroup.define.accessControlDefine.operations;
	}

	public boolean filled() {
		return this.filled;
	}

	public boolean isInherit(final Operation<?> operation) {
		return this.getAuthority(operation) == Authority.UNDEFINE;
	}

	public final boolean hasOperationAuthority(final Operation<?> operation) {
		Authority result = this.getAuthority(operation);
		if (result == Authority.UNDEFINE) {
			int mask = operation.getMask();
			return (this.authorityInformation & mask) == mask;
		} else {
			return result == Authority.ALLOW;
		}
	}

	public final boolean hasAccreditAuthority(final Operation<?> operation) {
		if (!this.filled) {
			throw new IllegalStateException("授权项尚未填充权限信息");
		}
		final int opMask = operation.getMask();
		return ((this.authorityInformation >>> 16) & opMask) == opMask;
	}

	public Authority getAuthority(Operation<?> operation) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (!this.filled) {
			throw new IllegalStateException("授权项尚未填充权限信息");
		}
		final int opMask = operation.getMask();
		final int result = this.authorityCode & AccessControlHelper.toAuthorityMask(opMask);
		if (result == 0) {
			return Authority.UNDEFINE;
		}
		if (result == AccessControlHelper.toAuthorityCode(opMask, Authority.ALLOW.code)) {
			return Authority.ALLOW;
		}
		return Authority.DENY;
	}

	public void setAuthority(Operation<?> operation, Authority authority) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (authority == null) {
			throw new NullArgumentException("authority");
		}
		// if (!this.filled) {
		// throw new IllegalStateException("授权项尚未填充权限信息");
		// }
		final int opMask = operation.getMask();
		this.authorityCode &= (~AccessControlHelper.toAuthorityCode(opMask, 0x3));
		this.authorityCode |= AccessControlHelper.toAuthorityCode(opMask, authority.code);
		this.changed = true;
	}

	public GUID getGroupIdentifier() {
		return this.accessControlGroup.cacheGroup.accessControlInformation.ACGUIDIdentifier;
	}

	public GUID getItemIdentifier() {
		if (this.accessControlHolder == null) {
			return null;
		} else {
			return this.accessControlHolder.ACGUIDIdentifier;
		}
	}

	// public List<OperationAuthorityInformation.Entry> getAuthorityEntryList()
	// {
	// // TODO
	// throw new UnsupportedOperationException();
	// }

	final void fill(final long[] actorACL,
			final AccessController accessController,
			final UserAccessController loginUserAccessController) {
		final CacheGroup<?, ?, ?> group = this.accessControlGroup.cacheGroup;
		final OperationEntry[] operationEntrys = group.define.accessControlDefine.operationEntrys;
		if (this.accessControlHolder == null) {
			this.authorityInformation = (buildAuthorityInformation(loginUserAccessController, operationEntrys, this.accessControlGroup) << 16) | buildAuthorityInformation(accessController, operationEntrys, this.accessControlGroup);
			this.authorityCode = AccessControlHelper.getAuthority(actorACL, group.accessControlInformation.ACLongIdentifier);
		} else {
			final AccessControlCacheHolder<?, ?, ?> holder = this.accessControlHolder;
			this.authorityInformation = (buildAuthorityInformation(loginUserAccessController, operationEntrys, holder) << 16) | buildAuthorityInformation(accessController, operationEntrys, holder);
			this.authorityCode = AccessControlHelper.getAuthority(actorACL, holder.ACLongIdentifier);
		}
		this.filled = true;
		this.changed = false;
	}

	final boolean isChanged() {
		return this.changed;
	}

	final long getACLongIdentifier() {
		if (this.accessControlHolder == null) {
			return this.accessControlGroup.cacheGroup.accessControlInformation.ACLongIdentifier;
		} else {
			return this.accessControlHolder.ACLongIdentifier;
		}
	}

	private final AccessControlCacheHolderOfGroup accessControlGroup;

	private final AccessControlCacheHolder<?, ?, ?> accessControlHolder;

	int authorityCode;

	private boolean filled;

	private boolean changed;

	private int authorityInformation;

}
