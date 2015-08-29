package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
final class DOT_ArrayList extends DOT_ListBase<ArrayList> {

	@Override
	protected final ArrayList newList(int cap) {
		return new ArrayList(cap);
	}
}
