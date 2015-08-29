package com.jiuqi.dna.core.impl;

import java.util.TreeMap;

@SuppressWarnings("unchecked")
final class DOT_TreeMap extends DOT_MapBase<TreeMap> {
	DOT_TreeMap() {
		super(false, true);
	}

	@Override
	protected final TreeMap newMap(int cap) {
		return new TreeMap();
	}
}
