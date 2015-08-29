package com.jiuqi.dna.core.impl;

import java.util.TreeSet;

@SuppressWarnings("unchecked")
final class DOT_TreeSet extends DOT_SetBase<TreeSet> {
	@Override
	protected final TreeSet newSet(int cap) {
		return new TreeSet();
	}

}
