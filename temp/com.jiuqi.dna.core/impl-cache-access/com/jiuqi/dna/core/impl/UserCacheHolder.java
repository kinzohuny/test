package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.Role;

final class UserCacheHolder extends
		ActorCacheHolder<User, UserImplement, UserImplement> {

	UserCacheHolder(
			final CacheGroup<User, UserImplement, UserImplement> ownGroup,
			final UserImplement value, final UserImplement keysHolder,
			final Long fixLongIdentifier) {
		super(ownGroup, value.identifier, null, value, keysHolder, fixLongIdentifier);
		value.userHolder = this;
	}

	@Override
	final UserImplement tryGetModifiableValue() {
		final UserImplement modifiableValue = super.tryGetModifiableValue();
		if (modifiableValue != null) {
			modifiableValue.userHolder = this;
		}
		return modifiableValue;
	}

	@Override
	final void tryPostModifiedValueWithoutCheck(final Object modifiedValue,
			final Object newKeysHolder, final Transaction transaction) {
		super.tryPostModifiedValueWithoutCheck(modifiedValue, newKeysHolder, transaction);
		((UserImplement) modifiedValue).userHolder = this;
	}

	@SuppressWarnings("unchecked")
	final long[][] tryGetOperationACLSnap(final Transaction transaction) {
		@SuppressWarnings("rawtypes")
		final List<RoleCacheHolder> assignedRoleList = (List) this.tryGetReferenceHolders(Role.class, this.isModifiableOnTransaction(transaction), transaction);
		// synchronized (this.actorLock) {
		final long[][] ACLSnap = new long[assignedRoleList.size() + 1][];
		final long[] userACL = this.tryGetOperationACL(transaction);
		ACLSnap[0] = userACL;
		// if (AccessControlConstants.isDefaultACVersion(ACVersion)
		// || !AccessControlHelper.isEmpty(userACL)) {
		// ACLSnap[0] = userACL;
		// } else {
		// ACLSnap[0] = this.tryGetOperationACL(null, transaction);
		// }
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
		// if (AccessControlConstants.isDefaultACVersion(ACVersion)
		// || !AccessControlHelper.isEmpty(userACL)) {
		// ACLSnap[0] = userACL;
		// } else {
		// ACLSnap[0] = this.tryGetAccreditACL(null, transaction);
		// }
		for (int index = 0, endIndex = assignedRoleList.size(); index < endIndex;) {
			final long[] ACL = assignedRoleList.get(index).tryGetAccreditACL(transaction);
			ACLSnap[++index] = ACL;
		}
		return ACLSnap;
		// }
	}
}