package com.jiuqi.dna.core.impl;

final class CacheStateError extends Error {

	private static final long serialVersionUID = 2731280318002187077L;

	CacheStateError() {
		super("缓存状态错误。");
	}

	CacheStateError(final Object errorState) {
		super("此时状态不应该为[" + errorState + "]。");
	}
}