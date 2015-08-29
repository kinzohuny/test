package com.jiuqi.dna.core.impl;

import java.util.HashSet;

@SuppressWarnings("unchecked")
final class DOT_HashSet extends DOT_SetBase<HashSet> {
	@Override
	protected final HashSet newSet(int cap) {
		return new HashSet(cap);
	}
}
