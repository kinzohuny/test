package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.resource.ResourceToken;

final class AccessControllerFactory {

	static final UserAccessController ANONYM_USER_ACCESSCONTROLLER;

	static final UserAccessController SYSTEM_USER_ACCESSCONTROLLER;

	static final UserAccessController DEBUGGER_USER_ACCESSCONTROLLER;

	static {
		ANONYM_USER_ACCESSCONTROLLER = new UserAccessController(null, null) {

			public final User getUser() {
				return BuildInUser.anonym;
			}

			@Override
			public final <TFacade> Authority getAuthority(
					final Operation<? super TFacade> operation,
					final ResourceToken<TFacade> resoureceToken) {
				return Authority.DENY;
			}

			@Override
			public final <TFacade> boolean hasAuthority(
					final Operation<? super TFacade> operation,
					final ResourceToken<TFacade> resoureceToken) {
				return false;
			}

			@Override
			final Authority internalGetAuthority(
					final OperationEntry operation,
					final AccessControlCacheHolder<?, ?, ?> holder) {
				return Authority.DENY;
			}

			@Override
			final Authority internalGetAuthority(
					final OperationEntry operation,
					final AccessControlCacheHolderOfGroup group) {
				return Authority.DENY;
			}

			@Override
			final boolean internalHasAuthority(final OperationEntry operation,
					final AccessControlCacheHolder<?, ?, ?> holder) {
				return false;
			}

			@Override
			final boolean internalHasAuthority(final OperationEntry operation,
					final AccessControlCacheHolderOfGroup group) {
				return false;
			}

			@Override
			final boolean internalHasAuthority(final OperationEntry operation,
					final AccessControlCacheHolder<?, ?, ?> holder,
					final boolean defaultAuthority) {
				return defaultAuthority;
			}

			@Override
			final AccessControlPolicy getPolicy() {
				return AccessControlPolicy.CURRENT_POLICY;
			}

		};
		SYSTEM_USER_ACCESSCONTROLLER = new UserAccessController(null, null) {

			public final User getUser() {
				return BuildInUser.system;
			}

			@Override
			public final <TFacade> Authority getAuthority(
					final Operation<? super TFacade> operation,
					final ResourceToken<TFacade> resoureceToken) {
				return Authority.ALLOW;
			}

			@Override
			public final <TFacade> boolean hasAuthority(
					final Operation<? super TFacade> operation,
					final ResourceToken<TFacade> resoureceToken) {
				return true;
			}

			@Override
			final Authority internalGetAuthority(
					final OperationEntry operation,
					final AccessControlCacheHolder<?, ?, ?> holder) {
				return Authority.ALLOW;
			}

			@Override
			final Authority internalGetAuthority(
					final OperationEntry operation,
					final AccessControlCacheHolderOfGroup group) {
				return Authority.ALLOW;
			}

			@Override
			final boolean internalHasAuthority(final OperationEntry operation,
					final AccessControlCacheHolder<?, ?, ?> holder) {
				return true;
			}

			@Override
			final boolean internalHasAuthority(final OperationEntry operation,
					final AccessControlCacheHolderOfGroup group) {
				return true;
			}

			@Override
			final boolean internalHasAuthority(final OperationEntry operation,
					final AccessControlCacheHolder<?, ?, ?> holder,
					final boolean defaultAuthority) {
				return defaultAuthority;
			}

			@Override
			final AccessControlPolicy getPolicy() {
				return AccessControlPolicy.CURRENT_POLICY;
			}

		};
		DEBUGGER_USER_ACCESSCONTROLLER = new UserAccessController(null, null) {

			public final User getUser() {
				return BuildInUser.debugger;
			}

			@Override
			public final <TFacade> Authority getAuthority(
					final Operation<? super TFacade> operation,
					final ResourceToken<TFacade> resoureceToken) {
				return Authority.ALLOW;
			}

			@Override
			public final <TFacade> boolean hasAuthority(
					final Operation<? super TFacade> operation,
					final ResourceToken<TFacade> resoureceToken) {
				return true;
			}

			@Override
			final Authority internalGetAuthority(
					final OperationEntry operation,
					final AccessControlCacheHolder<?, ?, ?> holder) {
				return Authority.ALLOW;
			}

			@Override
			final Authority internalGetAuthority(
					final OperationEntry operation,
					final AccessControlCacheHolderOfGroup group) {
				return Authority.ALLOW;
			}

			@Override
			final boolean internalHasAuthority(final OperationEntry operation,
					final AccessControlCacheHolder<?, ?, ?> holder) {
				return true;
			}

			@Override
			final boolean internalHasAuthority(final OperationEntry operation,
					final AccessControlCacheHolderOfGroup group) {
				return true;
			}

			@Override
			final boolean internalHasAuthority(final OperationEntry operation,
					final AccessControlCacheHolder<?, ?, ?> holder,
					final boolean defaultAuthority) {
				return defaultAuthority;
			}

			@Override
			final AccessControlPolicy getPolicy() {
				return AccessControlPolicy.CURRENT_POLICY;
			}

		};
	}

	

	// @SuppressWarnings("unused")
	// private static final class ActorRefuseFirst_SingleInherit_User extends
	// UserAccessController {
	//
	// private static final Authority hasAuthority(final long[][] ACLs,
	// final OperationEntry operation, final long holderIdentifier) {
	// Authority authority = Authority.UNDEFINE;
	// for (int index = 1, endIndex = ACLs.length; index < endIndex; index++) {
	// long authorityCode = AccessControlHelper.getAuthority(
	// ACLs[index], holderIdentifier)
	// & operation.authorityMask;
	// if (authorityCode == 0) {
	// continue;
	// } else if (authorityCode == operation.allowAuthorityCode) {
	// authority = Authority.ALLOW;
	// } else {
	// return Authority.DENY;
	// }
	// }
	// return authority;
	// }
	//
	// private ActorRefuseFirst_SingleInherit_User(final User user,
	// final long[][] ACLs, final GUID accessControlVersion,
	// final Transaction transaction) {
	// super(accessControlVersion, transaction);
	// this.user = user;
	// this.ACLs = ACLs;
	// }
	//
	// public final User getUser() {
	// return this.user;
	// }
	//
	// @Override
	// final Authority internalGetAuthority(OperationEntry operation,
	// AccessControlCacheHolder<?, ?, ?> holder) {
	// return getAuthority(this.ACLs[0], operation,
	// holder.ACLongIdentifier);
	// }
	//
	// @Override
	// final Authority internalGetAuthority(final OperationEntry operation,
	// final AccessControlCacheHolderOfGroup group) {
	// return getAuthority(this.ACLs[0], operation,
	// group.cacheGroup.accessControlInformation.ACLongIdentifier);
	// }
	//
	// @Override
	// final boolean internalHasAuthority(OperationEntry operation,
	// AccessControlCacheHolder<?, ?, ?> holder) {
	// AccessControlCacheHolder<?, ?, ?> lastHolder;
	// OperationEntry lastOperation;
	// boolean allowed = false;
	// while (true) {
	// // 判断用户的直接权限
	// final Authority authority = getAuthority(this.ACLs[0],
	// operation, holder.ACLongIdentifier);
	// if (authority != Authority.UNDEFINE) {
	// return authority != Authority.DENY;
	// } else {
	// // 判断角色的权限
	// switch (hasAuthority(this.ACLs, operation,
	// holder.ACLongIdentifier)) {
	// case DENY:
	// return false;
	// case ALLOW:
	// allowed = true;
	// }
	// lastHolder = holder;
	// lastOperation = operation;
	// }
	// final AccessControlCacheHolder<?, ?, ?>[] nextHolders = holder
	// .getNextHolderInAuthorityInheritPath(super.transaction);
	// if (nextHolders == null) {
	// break;
	// } else {
	// holder = nextHolders[0];
	// }
	// operation = lastHolder.tryGetMappingOperationEntry(operation,
	// holder.ownGroup.define);
	// if (operation == null) {
	// break;
	// }
	// }
	// Authority authority = getAuthority(
	// this.ACLs[0],
	// lastOperation,
	// lastHolder.ownGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// authority = hasAuthority(
	// this.ACLs,
	// lastOperation,
	// lastHolder.ownGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return allowed || lastHolder.getDefaultAuthority();
	// }
	// }
	// return authority != Authority.DENY;
	// }
	//
	// @Override
	// final boolean internalHasAuthority(final OperationEntry operation,
	// final AccessControlCacheHolderOfGroup group) {
	// final CacheGroup<?, ?, ?> cacheGroup = group.cacheGroup;
	// Authority authority = getAuthority(this.ACLs[0], operation,
	// cacheGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// authority = hasAuthority(this.ACLs, operation,
	// cacheGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return cacheGroup.define.accessControlDefine.defaultAuthority;
	// }
	// }
	// return authority != Authority.DENY;
	// }
	//
	// @Override
	// final boolean internalHasAuthority(final OperationEntry operation,
	// final AccessControlCacheHolder<?, ?, ?> holder,
	// final boolean defaultAuthority) {
	// Authority authority = getAuthority(this.ACLs[0], operation,
	// holder.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// authority = hasAuthority(this.ACLs, operation,
	// holder.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return defaultAuthority;
	// }
	// }
	// return authority != Authority.DENY;
	// }
	//
	// @Override
	// final AccessControlPolicy getPolicy() {
	// throw new UnsupportedOperationException();
	// }
	//
	// final User user;
	//
	// private final long[][] ACLs;
	//
	// }
	//
	// @SuppressWarnings("unused")
	// private static final class ActorRefuseFirst_SingleInherit_Role extends
	// RoleAccessController {
	//
	// private ActorRefuseFirst_SingleInherit_Role(final Role role,
	// final long[] ACL, final GUID accessControlVersion,
	// final Transaction transaction) {
	// super(accessControlVersion, transaction);
	// this.role = role;
	// this.ACL = ACL;
	// }
	//
	// public final Role getRole() {
	// return this.role;
	// }
	//
	// @Override
	// final Authority internalGetAuthority(OperationEntry operation,
	// AccessControlCacheHolder<?, ?, ?> holder) {
	// return getAuthority(this.ACL, operation, holder.ACLongIdentifier);
	// }
	//
	// @Override
	// final Authority internalGetAuthority(final OperationEntry operation,
	// final AccessControlCacheHolderOfGroup group) {
	// return getAuthority(this.ACL, operation,
	// group.cacheGroup.accessControlInformation.ACLongIdentifier);
	// }
	//
	// @Override
	// final boolean internalHasAuthority(OperationEntry operation,
	// AccessControlCacheHolder<?, ?, ?> holder) {
	// AccessControlCacheHolder<?, ?, ?> lastHolder;
	// OperationEntry lastOperation;
	// while (true) {
	// final Authority authority = getAuthority(this.ACL, operation,
	// holder.ACLongIdentifier);
	// if (authority != Authority.UNDEFINE) {
	// return authority != Authority.DENY;
	// } else {
	// lastHolder = holder;
	// lastOperation = operation;
	// }
	// final AccessControlCacheHolder<?, ?, ?>[] nextHolders = holder
	// .getNextHolderInAuthorityInheritPath(super.transaction);
	// if (nextHolders == null) {
	// break;
	// } else {
	// holder = nextHolders[0];
	// }
	// operation = lastHolder.tryGetMappingOperationEntry(operation,
	// holder.ownGroup.define);
	// if (operation == null) {
	// break;
	// }
	// }
	// final Authority authority = getAuthority(this.ACL, lastOperation,
	// lastHolder.ownGroup.longIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return lastHolder.getDefaultAuthority();
	// } else {
	// return authority != Authority.DENY;
	// }
	// }
	//
	// @Override
	// final boolean internalHasAuthority(final OperationEntry operation,
	// final AccessControlCacheHolderOfGroup group) {
	// final CacheGroup<?, ?, ?> cacheGroup = group.cacheGroup;
	// final Authority authority = getAuthority(this.ACL, operation,
	// cacheGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return cacheGroup.define.accessControlDefine.defaultAuthority;
	// } else {
	// return authority != Authority.DENY;
	// }
	// }
	//
	// @Override
	// final boolean internalHasAuthority(final OperationEntry operation,
	// final AccessControlCacheHolder<?, ?, ?> holder,
	// final boolean defaultAuthority) {
	// final Authority authority = getAuthority(this.ACL, operation,
	// holder.ACLongIdentifier);
	// if (authority != Authority.UNDEFINE) {
	// return defaultAuthority;
	// } else {
	// return authority != Authority.DENY;
	// }
	// }
	//
	// @Override
	// final AccessControlPolicy getPolicy() {
	// throw new UnsupportedOperationException();
	// }
	//
	// final Role role;
	//
	// private final long[] ACL;
	//
	// }
	//
	// @SuppressWarnings("unused")
	// private static final class ActorRefuseFirst_MultiInherit_User extends
	// UserAccessController {
	//
	// private static final Authority hasAuthority(final long[][] ACLs,
	// final OperationEntry operation, final long holderIdentifier) {
	// Authority authority = Authority.UNDEFINE;
	// for (int index = 1, endIndex = ACLs.length; index < endIndex; index++) {
	// long authorityCode = AccessControlHelper.getAuthority(
	// ACLs[index], holderIdentifier)
	// & operation.authorityMask;
	// if (authorityCode == 0) {
	// continue;
	// } else if (authorityCode == operation.allowAuthorityCode) {
	// authority = Authority.ALLOW;
	// } else {
	// return Authority.DENY;
	// }
	// }
	// return authority;
	// }
	//
	// private ActorRefuseFirst_MultiInherit_User(final User user,
	// final long[][] ACLs, final GUID accessControlVersion,
	// final Transaction transaction) {
	// super(accessControlVersion, transaction);
	// this.user = user;
	// this.ACLs = ACLs;
	// }
	//
	// public final User getUser() {
	// return this.user;
	// }
	//
	// @Override
	// final Authority internalGetAuthority(OperationEntry operation,
	// AccessControlCacheHolder<?, ?, ?> holder) {
	// return getAuthority(this.ACLs[0], operation,
	// holder.ACLongIdentifier);
	// }
	//
	// @Override
	// final Authority internalGetAuthority(final OperationEntry operation,
	// final AccessControlCacheHolderOfGroup group) {
	// return getAuthority(this.ACLs[0], operation,
	// group.cacheGroup.accessControlInformation.ACLongIdentifier);
	// }
	//
	// @Override
	// final boolean internalHasAuthority(OperationEntry operation,
	// AccessControlCacheHolder<?, ?, ?> holder) {
	// Authority authority = getAuthority(this.ACLs[0], operation,
	// holder.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// boolean allowed = false;
	// switch (hasAuthority(this.ACLs, operation,
	// holder.ACLongIdentifier)) {
	// case DENY:
	// return false;
	// case ALLOW:
	// allowed = true;
	// }
	// final AccessControlCacheHolder<?, ?, ?>[] nextHolders = holder
	// .getNextHolderInAuthorityInheritPath(super.transaction);
	// if (nextHolders != null) {
	// authority = this.internalHasAuthority(operation, holder,
	// nextHolders, allowed);
	// } else {
	// authority = getAuthority(
	// this.ACLs[0],
	// operation,
	// holder.ownGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// authority = hasAuthority(
	// this.ACLs,
	// operation,
	// holder.ownGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return allowed || holder.getDefaultAuthority();
	// }
	// }
	// }
	// }
	// return authority != Authority.DENY;
	// }
	//
	// @Override
	// final boolean internalHasAuthority(final OperationEntry operation,
	// final AccessControlCacheHolderOfGroup group) {
	// final CacheGroup<?, ?, ?> cacheGroup = group.cacheGroup;
	// Authority authority = getAuthority(this.ACLs[0], operation,
	// cacheGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// authority = hasAuthority(this.ACLs, operation,
	// cacheGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return cacheGroup.define.accessControlDefine.defaultAuthority;
	// }
	// }
	// return authority != Authority.DENY;
	// }
	//
	// @Override
	// final boolean internalHasAuthority(final OperationEntry operation,
	// final AccessControlCacheHolder<?, ?, ?> holder,
	// final boolean defaultAuthority) {
	// Authority authority = getAuthority(this.ACLs[0], operation,
	// holder.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// authority = hasAuthority(this.ACLs, operation,
	// holder.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return defaultAuthority;
	// }
	// }
	// return authority != Authority.DENY;
	// }
	//
	// @Override
	// final AccessControlPolicy getPolicy() {
	// throw new UnsupportedOperationException();
	// }
	//
	// private final Authority internalHasAuthority(
	// final OperationEntry lastOperation,
	// final AccessControlCacheHolder<?, ?, ?> lastHolder,
	// final AccessControlCacheHolder<?, ?, ?>[] nextHolders,
	// boolean allowed) {
	// for (int index = 0, endIndex = nextHolders.length; index < endIndex;
	// index++) {
	// final AccessControlCacheHolder<?, ?, ?> nextHolder = nextHolders[index];
	// final OperationEntry currentOperation;
	// final AccessControlCacheHolder<?, ?, ?> currentHolder;
	// final OperationEntry nextOperation = lastHolder
	// .tryGetMappingOperationEntry(lastOperation,
	// nextHolder.ownGroup.define);
	// if (nextOperation == null) {
	// currentOperation = lastOperation;
	// currentHolder = lastHolder;
	// } else {
	// retu: {
	// Authority authority = getAuthority(this.ACLs[0],
	// nextOperation, nextHolder.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// authority = hasAuthority(this.ACLs, nextOperation,
	// nextHolder.ACLongIdentifier);
	// if (authority != Authority.DENY) {
	// final AccessControlCacheHolder<?, ?, ?>[] nextNextHolders = nextHolder
	// .getNextHolderInAuthorityInheritPath(super.transaction);
	// allowed = allowed
	// || authority == Authority.ALLOW;
	// if (nextNextHolders != null) {
	// authority = this.internalHasAuthority(
	// nextOperation, nextHolder,
	// nextNextHolders, allowed);
	// if (authority == null) {
	// continue;
	// }
	// } else {
	// currentOperation = nextOperation;
	// currentHolder = nextHolder;
	// break retu;
	// }
	// }
	// }
	// return authority;
	// }
	// }
	// // 返回组的权限或默认权限
	// final Authority authority = getAuthority(
	// this.ACLs[0],
	// currentOperation,
	// currentHolder.ownGroup.accessControlInformation.ACLongIdentifier);
	// if (authority != Authority.UNDEFINE) {
	// return authority;
	// } else {
	// switch (hasAuthority(
	// this.ACLs,
	// currentOperation,
	// currentHolder.ownGroup.accessControlInformation.ACLongIdentifier)) {
	// case DENY:
	// return Authority.DENY;
	// case UNDEFINE:
	// if (!(allowed || currentHolder.getDefaultAuthority())) {
	// return Authority.DENY;
	// }
	// }
	// }
	// }
	// return null;
	// }
	//
	// final User user;
	//
	// private final long[][] ACLs;
	//
	// }
	//
	// @SuppressWarnings("unused")
	// private static final class ActorRefuseFirst_MultiInherit_Role extends
	// RoleAccessController {
	//
	// private ActorRefuseFirst_MultiInherit_Role(final Role role,
	// final long[] ACL, final GUID accessControlVersion,
	// final Transaction transaction) {
	// super(accessControlVersion, transaction);
	// this.role = role;
	// this.ACL = ACL;
	// }
	//
	// public final Role getRole() {
	// return this.role;
	// }
	//
	// @Override
	// final Authority internalGetAuthority(OperationEntry operation,
	// AccessControlCacheHolder<?, ?, ?> holder) {
	// return getAuthority(this.ACL, operation, holder.ACLongIdentifier);
	// }
	//
	// @Override
	// final Authority internalGetAuthority(final OperationEntry operation,
	// final AccessControlCacheHolderOfGroup group) {
	// return getAuthority(this.ACL, operation,
	// group.cacheGroup.accessControlInformation.ACLongIdentifier);
	// }
	//
	// @Override
	// final boolean internalHasAuthority(OperationEntry operation,
	// AccessControlCacheHolder<?, ?, ?> holder) {
	// Authority authority = getAuthority(this.ACL, operation,
	// holder.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// final AccessControlCacheHolder<?, ?, ?>[] nextHolders = holder
	// .getNextHolderInAuthorityInheritPath(super.transaction);
	// if (nextHolders != null) {
	// authority = this.internalHasAuthority(operation, holder,
	// nextHolders);
	// } else {
	// authority = getAuthority(
	// this.ACL,
	// operation,
	// holder.ownGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return holder.getDefaultAuthority();
	// }
	// }
	// }
	// return authority != Authority.DENY;
	// }
	//
	// @Override
	// final boolean internalHasAuthority(final OperationEntry operation,
	// final AccessControlCacheHolderOfGroup group) {
	// final CacheGroup<?, ?, ?> cacheGroup = group.cacheGroup;
	// final Authority authority = getAuthority(this.ACL, operation,
	// cacheGroup.accessControlInformation.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return cacheGroup.define.accessControlDefine.defaultAuthority;
	// } else {
	// return authority != Authority.DENY;
	// }
	// }
	//
	// @Override
	// final boolean internalHasAuthority(final OperationEntry operation,
	// final AccessControlCacheHolder<?, ?, ?> holder,
	// final boolean defaultAuthority) {
	// final Authority authority = getAuthority(this.ACL, operation,
	// holder.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// return defaultAuthority;
	// } else {
	// return authority != Authority.DENY;
	// }
	// }
	//
	// @Override
	// final AccessControlPolicy getPolicy() {
	// throw new UnsupportedOperationException();
	// }
	//
	// private final Authority internalHasAuthority(
	// final OperationEntry lastOperation,
	// final AccessControlCacheHolder<?, ?, ?> lastHolder,
	// final AccessControlCacheHolder<?, ?, ?>[] nextHolders) {
	// for (int index = 0, endIndex = nextHolders.length; index < endIndex;
	// index++) {
	// final AccessControlCacheHolder<?, ?, ?> nextHolder = nextHolders[index];
	// final OperationEntry currentOperation;
	// final AccessControlCacheHolder<?, ?, ?> currentHolder;
	// final OperationEntry nextOperation = lastHolder
	// .tryGetMappingOperationEntry(lastOperation,
	// nextHolder.ownGroup.define);
	// if (nextOperation == null) {
	// currentOperation = lastOperation;
	// currentHolder = lastHolder;
	// } else {
	// retu: {
	// Authority authority = getAuthority(this.ACL,
	// nextOperation, nextHolder.ACLongIdentifier);
	// if (authority == Authority.UNDEFINE) {
	// final AccessControlCacheHolder<?, ?, ?>[] nextNextHolders = nextHolder
	// .getNextHolderInAuthorityInheritPath(super.transaction);
	// if (nextNextHolders != null) {
	// authority = this.internalHasAuthority(
	// nextOperation, nextHolder,
	// nextNextHolders);
	// if (authority == null) {
	// continue;
	// }
	// } else {
	// currentOperation = nextOperation;
	// currentHolder = nextHolder;
	// break retu;
	// }
	// }
	// return authority;
	// }
	// }
	// // 返回组的权限或默认权限
	// final Authority authority = getAuthority(
	// this.ACL,
	// currentOperation,
	// currentHolder.ownGroup.accessControlInformation.ACLongIdentifier);
	// if (authority != Authority.UNDEFINE) {
	// return authority;
	// } else if (!currentHolder.getDefaultAuthority()) {
	// return Authority.DENY;
	// }
	// }
	// return null;
	// }
	//
	// final Role role;
	//
	// private final long[] ACL;
	//
	// }

	private AccessControllerFactory() {
		// do nothing
	}

}
