package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Role;

final class RoleCacheDefine extends
		CacheDefine<Role, RoleImplement, RoleImplement> {

	protected RoleCacheDefine(final Cache ownCache,
			final RoleResourceService roleCacheService) {
		super(ownCache, roleCacheService);
	}

	@Override
	final CacheGroup<Role, RoleImplement, RoleImplement> newGroup(
			final CacheGroupSpace ownSpace, final String title,
			final Long fixLongIdentifier, final Byte fixInitializeState,
			final Throwable initializeException) {
		return new RoleCacheGroup(ownSpace, this, super.title, fixLongIdentifier, fixInitializeState, initializeException);
	}

	private static final class RoleCacheGroup extends
			CacheGroup<Role, RoleImplement, RoleImplement> {

		RoleCacheGroup(final CacheGroupSpace ownSpace,
				final CacheDefine<Role, RoleImplement, RoleImplement> define,
				final String title, final Long fixLongIdentifier,
				final Byte fixInitializeState,
				final Throwable initializeException) {
			super(ownSpace, define, title, fixLongIdentifier, fixInitializeState, initializeException);
		}

		@Override
		final CacheHolder<Role, RoleImplement, RoleImplement> newHolder(
				final RoleImplement value, final RoleImplement keysHolder,
				final Long fixLongIdentifier) {
			final RoleCacheHolder holder = new RoleCacheHolder(this, value, keysHolder, fixLongIdentifier);
			value.roleHolder = holder;
			return holder;
		}

	}

}
