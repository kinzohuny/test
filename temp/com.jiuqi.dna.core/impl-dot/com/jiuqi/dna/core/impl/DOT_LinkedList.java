package com.jiuqi.dna.core.impl;

import java.util.LinkedList;

@SuppressWarnings("unchecked")
final class DOT_LinkedList extends DOT_ListBase<LinkedList> {

	@Override
	protected final LinkedList newList(int cap) {
		return new LinkedList();
	}
}
