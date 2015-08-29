package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.IdentifyAuthorityChecker;
import com.jiuqi.dna.core.type.GUID;

abstract class IdentifyAccessController extends UserAccessController implements
		IdentifyAuthorityChecker {

	static final class ActorFirstSingleInheritanceIAC extends
			IdentifyAccessController {

		private static final Authority getAuthority(final long[][] ACLs,
				final OperationEntry operation, final long holderIdentifier) {
			long authorityCode = AccessControlHelper.getAuthority(ACLs[0], holderIdentifier) & operation.authorityMask;
			if (authorityCode == 0L) {
				boolean allowed = false;
				for (int index = 1, endIndex = ACLs.length; index < endIndex; index++) {
					authorityCode = AccessControlHelper.getAuthority(ACLs[index], holderIdentifier) & operation.authorityMask;
					if (authorityCode == 0) {
						continue;
					} else if (authorityCode == operation.allowAuthorityCode) {
						allowed = true;
					} else {
						return Authority.DENY;
					}
				}
				if (allowed) {
					return Authority.ALLOW;
				} else {
					return Authority.UNDEFINE;
				}
			} else if (authorityCode == operation.allowAuthorityCode) {
				return Authority.ALLOW;
			} else {
				return Authority.DENY;
			}
		}

		ActorFirstSingleInheritanceIAC(final User user, final long[][] ACLs,
				final GUID accessControlVersion, final Transaction transaction) {
			super(accessControlVersion, transaction);
			this.user = user;
			this.ACLs = ACLs;
		}

		@Override
		final Authority internalGetAuthority(final OperationEntry operation,
				final AccessControlCacheHolder<?, ?, ?> holder) {
			return getAuthority(this.ACLs[0], operation, holder.ACLongIdentifier);
		}

		@Override
		final Authority internalGetAuthority(final OperationEntry operation,
				final AccessControlCacheHolderOfGroup group) {
			return getAuthority(this.ACLs[0], operation, group.cacheGroup.accessControlInformation.ACLongIdentifier);
		}

		@Override
		final boolean internalHasAuthority(OperationEntry operation,
				AccessControlCacheHolder<?, ?, ?> holder) {
			AccessControlCacheHolder<?, ?, ?> lastHolder;
			OperationEntry lastOperation;
			while (true) {
				final Authority authority = getAuthority(this.ACLs, operation, holder.ACLongIdentifier);
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
			final Authority authority = getAuthority(this.ACLs, lastOperation, lastHolder.ownGroup.accessControlInformation.ACLongIdentifier);
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
			final Authority authority = getAuthority(this.ACLs, operation, cacheGroup.accessControlInformation.ACLongIdentifier);
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
			final Authority authority = getAuthority(this.ACLs, operation, holder.ACLongIdentifier);
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

		final User user;

		private final long[][] ACLs;

		public User getUser() {
			return this.user;
		}

	}

	static final class ActorFirstMultipleInheritanceIAC extends
			IdentifyAccessController {

		private static final Authority getAuthority(final long[][] ACLs,
				final OperationEntry operation, final long holderIdentifier) {
			long authorityCode = AccessControlHelper.getAuthority(ACLs[0], holderIdentifier) & operation.authorityMask;
			if (authorityCode == 0L) {
				boolean allowed = false;
				for (int index = 1, endIndex = ACLs.length; index < endIndex; index++) {
					authorityCode = AccessControlHelper.getAuthority(ACLs[index], holderIdentifier) & operation.authorityMask;
					if (authorityCode == 0) {
						continue;
					} else if (authorityCode == operation.allowAuthorityCode) {
						allowed = true;
					} else {
						return Authority.DENY;
					}
				}
				if (allowed) {
					return Authority.ALLOW;
				} else {
					return Authority.UNDEFINE;
				}
			} else if (authorityCode == operation.allowAuthorityCode) {
				return Authority.ALLOW;
			} else {
				return Authority.DENY;
			}
		}

		ActorFirstMultipleInheritanceIAC(final User user, final long[][] ACLs,
				final GUID accessControlVersion, final Transaction transaction) {
			super(accessControlVersion, transaction);
			this.user = user;
			this.ACLs = ACLs;
		}

		@Override
		final Authority internalGetAuthority(OperationEntry operation,
				AccessControlCacheHolder<?, ?, ?> holder) {
			return getAuthority(this.ACLs[0], operation, holder.ACLongIdentifier);
		}

		@Override
		final Authority internalGetAuthority(final OperationEntry operation,
				final AccessControlCacheHolderOfGroup group) {
			return getAuthority(this.ACLs[0], operation, group.cacheGroup.accessControlInformation.ACLongIdentifier);
		}

		@Override
		final boolean internalHasAuthority(OperationEntry operation,
				AccessControlCacheHolder<?, ?, ?> holder) {
			Authority authority = getAuthority(this.ACLs, operation, holder.ACLongIdentifier);
			if (authority == Authority.UNDEFINE) {
				final AccessControlCacheHolder<?, ?, ?>[] nextHolders = holder.getNextHolderInAuthorityInheritPath(super.transaction);
				if (nextHolders != null) {
					authority = this.internalGetAuthority(operation, holder, nextHolders);
				} else {
					authority = getAuthority(this.ACLs, operation, holder.ownGroup.accessControlInformation.ACLongIdentifier);
					if (authority == Authority.UNDEFINE) {
						return holder.getDefaultAuthority();
					}
				}
			}
			return authority != Authority.DENY;
		}

		@Override
		final boolean internalHasAuthority(final OperationEntry operation,
				final AccessControlCacheHolderOfGroup group) {
			final CacheGroup<?, ?, ?> cacheGroup = group.cacheGroup;
			final Authority authority = getAuthority(this.ACLs, operation, cacheGroup.accessControlInformation.ACLongIdentifier);
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
			final Authority authority = getAuthority(this.ACLs, operation, holder.ACLongIdentifier);
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

		private final Authority internalGetAuthority(
				final OperationEntry lastOperation,
				final AccessControlCacheHolder<?, ?, ?> lastHolder,
				final AccessControlCacheHolder<?, ?, ?>[] nextHolders) {
			Authority allowAuthority = null;
			for (int index = 0, endIndex = nextHolders.length; index < endIndex; index++) {
				final AccessControlCacheHolder<?, ?, ?> nextHolder = nextHolders[index];
				final OperationEntry currentOperation;
				final AccessControlCacheHolder<?, ?, ?> currentHolder;
				final OperationEntry nextOperation = lastHolder.tryGetMappingOperationEntry(lastOperation, nextHolder.ownGroup.define);
				if (nextOperation == null) {
					currentOperation = lastOperation;
					currentHolder = lastHolder;
				} else {
					retu: {
						Authority authority = getAuthority(this.ACLs, nextOperation, nextHolder.ACLongIdentifier);
						switch (authority) {
						case ALLOW:
							allowAuthority = Authority.ALLOW;
							continue;
						case DENY:
							return Authority.DENY;
						case UNDEFINE:
							final AccessControlCacheHolder<?, ?, ?>[] nextNextHolders = nextHolder.getNextHolderInAuthorityInheritPath(super.transaction);
							if (nextNextHolders != null) {
								authority = this.internalGetAuthority(nextOperation, nextHolder, nextNextHolders);
								if (authority == Authority.ALLOW) {
									allowAuthority = Authority.ALLOW;
								} else if (authority == Authority.DENY) {
									return Authority.DENY;
								}
								continue;
							} else {
								currentOperation = nextOperation;
								currentHolder = nextHolder;
								break retu;
							}
						default:
							throw new UnsupportedOperationException();
						}
					}
				}
				// 返回组的权限或默认权限
				final Authority authority = getAuthority(this.ACLs, currentOperation, currentHolder.ownGroup.accessControlInformation.ACLongIdentifier);
				switch (authority) {
				case ALLOW:
					allowAuthority = Authority.ALLOW;
				case UNDEFINE:
					if (currentHolder.getDefaultAuthority()) {
						continue;
					}
				case DENY:
					return Authority.DENY;
				default:
					throw new UnsupportedOperationException();
				}
			}
			return allowAuthority;
		}

		final User user;

		private final long[][] ACLs;

		public final User getUser() {
			return this.user;
		}
	}

	IdentifyAccessController(final GUID accessControlVersion,
			final Transaction transaction) {
		super(accessControlVersion, transaction);
	}

}
