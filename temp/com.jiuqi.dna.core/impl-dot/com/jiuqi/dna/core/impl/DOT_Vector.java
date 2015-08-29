package com.jiuqi.dna.core.impl;

import java.util.Vector;

@SuppressWarnings("unchecked")
final class DOT_Vector extends DOT_ListBase<Vector> {

	@Override
	protected final Vector newList(int cap) {
		return new Vector(cap);
	}
}
