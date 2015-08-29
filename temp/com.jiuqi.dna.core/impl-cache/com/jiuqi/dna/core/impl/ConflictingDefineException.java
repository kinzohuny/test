package com.jiuqi.dna.core.impl;

final class ConflictingDefineException extends RuntimeException {

	private static final long serialVersionUID = -6035497447764540849L;

	ConflictingDefineException(final Class<?> facadeClass) {
		super("重复定义了外观类型为[" + facadeClass + "]的缓存定义。");
	}
}