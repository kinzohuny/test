package com.jiuqi.dna.core.impl;

interface SQLNameResolver {
	public <T> T findProvider(Class<T> cls, String name);
}
