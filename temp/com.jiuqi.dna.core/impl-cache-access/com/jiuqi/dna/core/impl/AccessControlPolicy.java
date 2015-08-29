package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.resource.ResourceToken;

enum AccessControlPolicy {

	ACTORFIRST_SINGLEINHERIT(true) {

		@Override
		final void buildAuthorityInheritPath(
				final AuthorityInheritPathImplement path,
				final Transaction transaction) {
			AuthorityInheritPathImplement.NodeImplement node = path.baseNode;
			final ResourceToken<?> holder = node.value;
			if (holder instanceof AccessControlCacheHolder<?, ?, ?>) {
				AccessControlCacheHolder<?, ?, ?> ACHolder = (AccessControlCacheHolder<?, ?, ?>) holder;
				AccessControlCacheHolder<?, ?, ?> lastHolder;
				while (true) {
					lastHolder = ACHolder;
					final AccessControlCacheHolder<?, ?, ?>[] nextHolders = ACHolder.getNextHolderInAuthorityInheritPath(transaction);
					if (nextHolders == null) {
						break;
					} else {
						ACHolder = nextHolders[0];
					}
					node = node.setInheritNode(ACHolder);
				}
				node.setInheritNode(lastHolder.ownGroup.accessControlInformation.accessControlCacheItem);
				return;
			} else if (holder instanceof AccessControlCacheHolderOfGroup) {
				return;
			} else {
				throw new UnsupportedAccessControlException(holder.getFacadeClass());
			}
		}

		@Override
		final UserAccessController newUserAccessController(
				final UserCacheHolder user, final boolean operationAuthority,
				final Transaction transaction) {
			if (operationAuthority) {
				return new UserAccessController.ActorFirstSingleInheritanceUAC(user.getFacade(), user.tryGetOperationACLSnap(transaction), user.ACVersion, transaction, operationAuthority);
			} else {
				return new UserAccessController.ActorFirstSingleInheritanceUAC(user.getFacade(), user.tryGetAccreditACLSnap(transaction), user.ACVersion, transaction, operationAuthority);
			}
		}

		@Override
		final RoleAccessController newRoleAccessController(
				final RoleCacheHolder role, final boolean operationAuthority,
				final Transaction transaction) {
			if (operationAuthority) {
				return new RoleAccessController.ActorFirstSingleInheritanceRAC(role, role.tryGetOperationACL(transaction), role.ACVersion, transaction);
			} else {
				return new RoleAccessController.ActorFirstSingleInheritanceRAC(role, role.tryGetAccreditACL(transaction), role.ACVersion, transaction);
			}
		}

		@Override
		final IdentifyAccessController newIdentifyAccessController(
				final User user, final IdentifyCacheHolder identify,
				final boolean operationAuthority, final Transaction transaction) {
			if (operationAuthority) {
				return new IdentifyAccessController.ActorFirstSingleInheritanceIAC(user, identify.tryGetOperationACLSnap(transaction), identify.ACVersion, transaction);
			} else {
				return new IdentifyAccessController.ActorFirstSingleInheritanceIAC(user, identify.tryGetAccreditACLSnap(transaction), identify.ACVersion, transaction);
			}
		}

	},

	ACTORFIRST_MULTIINHERIT(false) {

		@Override
		final void buildAuthorityInheritPath(
				final AuthorityInheritPathImplement path,
				final Transaction transaction) {
			// TODO
			throw new UnsupportedOperationException("暂不支持查看多继承的权限继承路径。");
		}

		@Override
		final UserAccessController newUserAccessController(
				final UserCacheHolder user, final boolean operationAuthority,
				final Transaction transaction) {
			if (operationAuthority) {
				return new UserAccessController.ActorFirstMultipleInheritanceUAC(user, user.tryGetOperationACLSnap(transaction), user.ACVersion, transaction);
			} else {
				return new UserAccessController.ActorFirstMultipleInheritanceUAC(user, user.tryGetAccreditACLSnap(transaction), user.ACVersion, transaction);
			}
		}

		@Override
		final RoleAccessController newRoleAccessController(
				final RoleCacheHolder role, final boolean operationAuthority,
				final Transaction transaction) {
			if (operationAuthority) {
				return new RoleAccessController.ActorFirstMultipleInheritanceRAC(role, role.tryGetOperationACL(transaction), role.ACVersion, transaction);
			} else {
				return new RoleAccessController.ActorFirstMultipleInheritanceRAC(role, role.tryGetAccreditACL(transaction), role.ACVersion, transaction);
			}
		}

		@Override
		final IdentifyAccessController newIdentifyAccessController(
				final User user, final IdentifyCacheHolder identifyHolder,
				final boolean operationAuthority, final Transaction transaction) {
			if (operationAuthority) {
				return new IdentifyAccessController.ActorFirstMultipleInheritanceIAC(user, identifyHolder.tryGetOperationACLSnap(transaction), identifyHolder.ACVersion, transaction);
			} else {
				return new IdentifyAccessController.ActorFirstMultipleInheritanceIAC(user, identifyHolder.tryGetAccreditACLSnap(transaction), identifyHolder.ACVersion, transaction);
			}
		}

	};

	private AccessControlPolicy(final boolean singleInherit) {
		this.singleInherit = singleInherit;
	}

	abstract void buildAuthorityInheritPath(AuthorityInheritPathImplement path,
			Transaction transaction);

	abstract UserAccessController newUserAccessController(UserCacheHolder user,
			boolean operationAuthority, Transaction transaction);

	abstract RoleAccessController newRoleAccessController(RoleCacheHolder role,
			boolean operationAuthority, Transaction transaction);

	abstract IdentifyAccessController newIdentifyAccessController(User user,
			IdentifyCacheHolder identify, boolean operationAuthority,
			Transaction transaction);

	final boolean singleInherit;

	static final AccessControlPolicy CURRENT_POLICY;

	static {
		if (Boolean.getBoolean("com.jiuqi.dna.core.cache.accesscontrol.multiinherit")) {
			CURRENT_POLICY = AccessControlPolicy.ACTORFIRST_MULTIINHERIT;
		} else {
			CURRENT_POLICY = AccessControlPolicy.ACTORFIRST_SINGLEINHERIT;
		}
	}

}