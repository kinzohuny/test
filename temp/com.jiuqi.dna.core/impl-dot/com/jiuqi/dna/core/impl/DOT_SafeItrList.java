package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SafeItrList;

@SuppressWarnings("unchecked")
final class DOT_SafeItrList extends DOT_ListBase<SafeItrList> {

	@Override
	protected final SafeItrList newList(int cap) {
		return new SafeItrList(cap);
	}
}
