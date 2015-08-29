package com.jiuqi.dna.core.impl;

final class NotFoundItemException extends RuntimeException {

	private static final long serialVersionUID = 333143711599293975L;

	NotFoundItemException(final long identifier) {
		super("找不到标识为[" + identifier + "]的缓存项。");
	}

	NotFoundItemException(final Class<?> facadeClass) {
		super("找不到外观类型为[" + facadeClass + "]的单例缓存项。");
	}

	NotFoundItemException(final Class<?> facadeClass, final Object key) {
		super("找不到外观类型为[" + facadeClass + "]，键值为[" + key + "]的缓存项。");
	}

	NotFoundItemException(final Class<?> facadeClass, final Object key1,
			final Object key2) {
		super("找不到外观类型为[" + facadeClass + "]，键值为[" + key1 + "，" + key2 + "]的缓存项。");
	}

	NotFoundItemException(final Class<?> facadeClass, final Object key1,
			final Object key2, final Object key3) {
		super("找不到外观类型为[" + facadeClass + "]，键值为[" + key1 + "，" + key2 + "，" + key3 + "]的缓存项。");
	}

}
