package com.jiuqi.dna.core.impl;

final class UnsupportedAccessControlException extends RuntimeException {

	private static final long serialVersionUID = 1803296216555873270L;

	UnsupportedAccessControlException(final Class<?> facadeClass) {
		super("外观类型为[" + facadeClass + "]的缓存定义不支持访问控制相关的操作。");
	}
}