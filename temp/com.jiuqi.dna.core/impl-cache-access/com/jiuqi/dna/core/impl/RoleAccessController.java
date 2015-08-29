package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.auth.RoleAuthorityChecker;
import com.jiuqi.dna.core.type.GUID;

abstract class RoleAccessController extends AccessController implements
		RoleAuthorityChecker {

	static final class ActorFirstSingleInheritanceRAC extends
			RoleAccessController {

		ActorFirstSingleInheritanceRAC(final Role role, final long[] ACL,
				final GUID accessControlVersion, final Transaction transaction) {
			super(accessControlVersion, transaction);
			this.role = role;
			this.ACL = ACL;
		}

		public final Role getRole() {
			return this.role;
		}

		@Override
		final Authority internalGetAuthority(OperationEntry operation,
				AccessControlCacheHolder<?, ?, ?> holder) {
			return getAuthority(this.ACL, operation, holder.ACLongIdentifier);
		}

		@Override
		final Authority internalGetAuthority(final OperationEntry operation,
				final AccessControlCacheHolderOfGroup group) {
			return getAuthority(this.ACL, operation, group.cacheGroup.accessControlInformation.ACLongIdentifier);
		}

		@Override
		final boolean internalHasAuthority(OperationEntry operation,
				AccessControlCacheHolder<?, ?, ?> holder) {
			AccessControlCacheHolder<?, ?, ?> lastHolder;
			OperationEntry lastOperation;
			while (true) {
				final Authority authority = getAuthority(this.ACL, operation, holder.ACLongIdentifier);
				if (authority != Authority.UNDEFINE) {
					return authority == Authority.ALLOW;
				} else {
					lastHolder = holder;
					lastOperation = operation;
				}
				final AccessControlCacheHolder<?, ?, ?>[] nextHolders = holder.getNextHolderInAuthorityInheritPath(super.transaction);
				if (nextHolders == null) {
					break;
				} else {
					holder = nextHolders[0];
				}
				operation = lastHolder.tryGetMappingOperationEntry(operation, holder.ownGroup.define);
				if (operation == null) {
					break;
				}
			}
			final Authority authority = getAuthority(this.ACL, lastOperation, lastHolder.ownGroup.longIdentifier);
			if (authority == Authority.UNDEFINE) {
				return lastHolder.getDefaultAuthority();
			} else {
				return authority == Authority.ALLOW;
			}
		}

		@Override
		final boolean internalHasAuthority(final OperationEntry operation,
				final AccessControlCacheHolderOfGroup group) {
			final CacheGroup<?, ?, ?> cacheGroup = group.cacheGroup;
			final Authority authority = getAuthority(this.ACL, operation, cacheGroup.accessControlInformation.ACLongIdentifier);
			if (authority != Authority.UNDEFINE) {
				return authority == Authority.ALLOW;
			} else {
				return cacheGroup.define.accessControlDefine.defaultAuthority;
			}
		}

		@Override
		final boolean internalHasAuthority(final OperationEntry operation,
				final AccessControlCacheHolder<?, ?, ?> holder,
				final boolean defaultAuthority) {
			final Authority authority = getAuthority(this.ACL, operation, holder.ACLongIdentifier);
			if (authority != Authority.UNDEFINE) {
				return authority == Authority.ALLOW;
			} else {
				return defaultAuthority;
			}
		}

		@Override
		final AccessControlPolicy getPolicy() {
			return AccessControlPolicy.ACTORFIRST_SINGLEINHERIT;
		}

		final Role role;

		private final long[] ACL;

	}

	static final class ActorFirstMultipleInheritanceRAC extends
			RoleAccessController {

		ActorFirstMultipleInheritanceRAC(final Role role, final long[] ACL,
				final GUID accessControlVersion, final Transaction transaction) {
			super(accessControlVersion, transaction);
			this.role = role;
			this.ACL = ACL;
		}

		public final Role getRole() {
			return this.role;
		}

		@Override
		final Authority internalGetAuthority(OperationEntry operation,
				AccessControlCacheHolder<?, ?, ?> holder) {
			return getAuthority(this.ACL, operation, holder.ACLongIdentifier);
		}

		@Override
		final Authority internalGetAuthority(final OperationEntry operation,
				final AccessControlCacheHolderOfGroup group) {
			return getAuthority(this.ACL, operation, group.cacheGroup.accessControlInformation.ACLongIdentifier);
		}

		@Override
		final boolean internalHasAuthority(OperationEntry operation,
				AccessControlCacheHolder<?, ?, ?> holder) {
			AccessControlCacheHolder<?, ?, ?> lastHolder;
			OperationEntry lastOperation;
			while (true) {
				final Authority authority = getAuthority(this.ACL, operation, holder.ACLongIdentifier);
				if (authority != Authority.UNDEFINE) {
					return authority == Authority.ALLOW;
				} else {
					lastHolder = holder;
					lastOperation = operation;
				}
				final AccessControlCacheHolder<?, ?, ?>[] nextHolders = holder.getNextHolderInAuthorityInheritPath(super.transaction);
				if (nextHolders == null) {
					break;
				} else {
					holder = nextHolders[0];
				}
				operation = lastHolder.tryGetMappingOperationEntry(operation, holder.ownGroup.define);
				if (operation == null) {
					break;
				}
			}
			final Authority authority = getAuthority(this.ACL, lastOperation, lastHolder.ownGroup.longIdentifier);
			if (authority == Authority.UNDEFINE) {
				return lastHolder.getDefaultAuthority();
			} else {
				return authority == Authority.ALLOW;
			}
		}

		@Override
		final boolean internalHasAuthority(final OperationEntry operation,
				final AccessControlCacheHolderOfGroup group) {
			final CacheGroup<?, ?, ?> cacheGroup = group.cacheGroup;
			final Authority authority = getAuthority(this.ACL, operation, cacheGroup.accessControlInformation.ACLongIdentifier);
			if (authority != Authority.UNDEFINE) {
				return authority == Authority.ALLOW;
			} else {
				return cacheGroup.define.accessControlDefine.defaultAuthority;
			}
		}

		@Override
		final boolean internalHasAuthority(final OperationEntry operation,
				final AccessControlCacheHolder<?, ?, ?> holder,
				final boolean defaultAuthority) {
			final Authority authority = getAuthority(this.ACL, operation, holder.ACLongIdentifier);
			if (authority != Authority.UNDEFINE) {
				return authority == Authority.ALLOW;
			} else {
				return defaultAuthority;
			}
		}

		@Override
		final AccessControlPolicy getPolicy() {
			return AccessControlPolicy.ACTORFIRST_MULTIINHERIT;
		}

		final Role role;

		private final long[] ACL;

	}

	RoleAccessController(final GUID accessControlVersion,
			final Transaction transaction) {
		super(accessControlVersion, transaction);
	}

}
