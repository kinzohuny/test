package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.GUID;

final class NoSuchDefineException extends RuntimeException {

	private static final long serialVersionUID = 1812737042383295571L;

	NoSuchDefineException(final GUID GUIDIdentifier) {
		super("没有标识为[" + GUIDIdentifier + "]的缓存定义。");
	}

	NoSuchDefineException(final Class<?> facadeClass) {
		super("没有外观类型为[" + facadeClass + "]的缓存定义。");
	}
}