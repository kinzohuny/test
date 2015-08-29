package com.jiuqi.dna.core.impl;

final class IdentifyCacheDefine extends
		CacheDefine<Identify, Identify, Identify> {

	protected IdentifyCacheDefine(final Cache ownCache,
			final IdentifyResourceService identifyCacheService) {
		super(ownCache, identifyCacheService);
	}

	@Override
	final CacheGroup<Identify, Identify, Identify> newGroup(
			final CacheGroupSpace ownSpace, final String title,
			final Long fixLongIdentifier, final Byte fixInitializeState,
			final Throwable initializeException) {
		return new IdentifyCacheGroup(ownSpace, this, super.title, fixLongIdentifier, fixInitializeState, initializeException);
	}

	private static final class IdentifyCacheGroup extends
			CacheGroup<Identify, Identify, Identify> {

		IdentifyCacheGroup(final CacheGroupSpace ownSpace,
				final CacheDefine<Identify, Identify, Identify> define,
				final String title, final Long fixLongIdentifier,
				final Byte fixInitializeState,
				final Throwable initializeException) {
			super(ownSpace, define, title, fixLongIdentifier, fixInitializeState, initializeException);
		}

		@Override
		final CacheHolder<Identify, Identify, Identify> newHolder(
				final Identify value, final Identify keysHolder,
				final Long fixLongIdentifier) {
			final IdentifyCacheHolder holder = new IdentifyCacheHolder(this, value, keysHolder, fixLongIdentifier);
			return holder;
		}

	}

}
