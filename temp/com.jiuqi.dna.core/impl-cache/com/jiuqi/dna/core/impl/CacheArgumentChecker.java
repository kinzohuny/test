package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;

final class CacheArgumentChecker {

	static final void check(final Class<?> facadeClass) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
	}

	static final void check(final Class<?> facadeClass, final Object key) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
	}

	static final void check(final Class<?> facadeClass, final Object key1,
			final Object key2) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
	}

	static final void check(final Class<?> facadeClass, final Object key1,
			final Object key2, final Object key3) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
	}

	static final void check(final Object key) {
		if (key == null) {
			throw new NullArgumentException("key");
		}
	}

	static final void check(final Object key1, final Object key2) {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
	}

	static final void check(final Object key1, final Object key2,
			final Object key3) {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
	}

	private CacheArgumentChecker() {
	}
}