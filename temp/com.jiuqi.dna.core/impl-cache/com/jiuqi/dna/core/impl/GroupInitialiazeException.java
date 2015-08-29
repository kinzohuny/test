package com.jiuqi.dna.core.impl;

final class GroupInitialiazeException extends RuntimeException {

	private static final long serialVersionUID = -5530222222870583443L;

	GroupInitialiazeException(final Class<?> facadeClass,
			final Object spaceIdentifier, final Throwable e) {
		super("缓存组初始化的过程中出现异常。外观类型为[" + facadeClass + "]，组空间标识[" + spaceIdentifier + "]", e);
	}

	GroupInitialiazeException(final Class<?> facadeClass,
			final Object spaceIdentifier) {
		super("缓存组初始化的过程中出现异常。外观类型为[" + facadeClass + "]，组空间标识[" + spaceIdentifier + "]");
	}

	GroupInitialiazeException(final Class<?> facadeClass,
			final Object spaceIdentifier, final String message) {
		super(message + "外观类型为[" + facadeClass + "]，组空间标识[" + spaceIdentifier + "]");
	}

}
