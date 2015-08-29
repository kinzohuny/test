package com.jiuqi.dna.core.impl;

import java.util.Hashtable;

@SuppressWarnings("unchecked")
final class DOT_Hashtable extends DOT_MapBase<Hashtable> {
	DOT_Hashtable() {
		super(false, false);
	}

	@Override
	protected final Hashtable newMap(int cap) {
		return new Hashtable(cap);
	}
}