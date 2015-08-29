package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.User;

final class UserCacheDefine extends
		CacheDefine<User, UserImplement, UserImplement> {

	protected UserCacheDefine(final Cache ownCache,
			final UserResourceService userCacheService) {
		super(ownCache, userCacheService);
	}

	@Override
	final CacheGroup<User, UserImplement, UserImplement> newGroup(
			final CacheGroupSpace ownSpace, final String title,
			final Long fixLongIdentifier, final Byte fixInitializeState,
			final Throwable initializeException) {
		return new UserCacheGroup(ownSpace, this, super.title, fixLongIdentifier, fixInitializeState, initializeException);
	}

	private static final class UserCacheGroup extends
			CacheGroup<User, UserImplement, UserImplement> {

		UserCacheGroup(final CacheGroupSpace ownSpace,
				final CacheDefine<User, UserImplement, UserImplement> define,
				final String title, final Long fixLongIdentifier,
				final Byte fixInitializeState,
				final Throwable initializeException) {
			super(ownSpace, define, title, fixLongIdentifier, fixInitializeState, initializeException);
		}

		@Override
		final CacheHolder<User, UserImplement, UserImplement> newHolder(
				final UserImplement value, final UserImplement keysHolder,
				final Long fixLongIdentifier) {
			final UserCacheHolder holder = new UserCacheHolder(this, value, keysHolder, fixLongIdentifier);
			value.userHolder = holder;
			return holder;
		}
	}
}