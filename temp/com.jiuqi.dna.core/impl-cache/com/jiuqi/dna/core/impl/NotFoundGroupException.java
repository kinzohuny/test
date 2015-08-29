package com.jiuqi.dna.core.impl;

final class NotFoundGroupException extends RuntimeException {

	private static final long serialVersionUID = -48763467593197085L;

	NotFoundGroupException(final long identifier) {
		super("找不到标识为[" + identifier + "]的缓存组。");
	}

	NotFoundGroupException(final Class<?> facadeClass, final Object identifier) {
		super("找不到外观类型为[" + facadeClass + "]，组空间标识为[" + identifier + "]的缓存组。");
	}
}