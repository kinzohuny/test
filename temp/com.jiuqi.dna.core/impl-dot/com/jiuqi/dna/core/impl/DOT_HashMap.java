package com.jiuqi.dna.core.impl;

import java.util.HashMap;

@SuppressWarnings("unchecked")
final class DOT_HashMap extends DOT_MapBase<HashMap> {

	DOT_HashMap() {
		super(true, true);
	}

	@Override
	protected final HashMap newMap(int cap) {
		return new HashMap(cap);
	}
}
