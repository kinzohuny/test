package com.jiuqi.dna.core.impl;

@SuppressWarnings("unchecked")
final class DOT_DnaArrayList extends DOT_ListBase<DnaArrayList> {

	@Override
	protected final DnaArrayList newList(int cap) {
		return new DnaArrayList(cap);
	}

}
