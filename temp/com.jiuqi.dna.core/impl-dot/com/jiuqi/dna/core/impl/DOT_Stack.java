package com.jiuqi.dna.core.impl;

import java.util.Stack;

@SuppressWarnings("unchecked")
final class DOT_Stack extends DOT_ListBase<Stack> {

	@Override
	protected final Stack newList(int cap) {
		return new Stack();
	}
}
