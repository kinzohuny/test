package com.jiuqi.dna.core.impl;

import java.util.Set;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.type.DataObjectTranslator;

abstract class DOT_SetBase<TSet extends Set<?>> implements
		DataObjectTranslator<TSet, Object[]> {
	final static short VERSION = 0x0100;

	public final boolean supportAssign() {
		return true;
	}

	public final Object[] toDelegateObject(TSet set) {
		final int size = set.size();
		if (size > 0) {
			return set.toArray();
		} else {
			return Utils.emptyObjectArray;
		}
	}

	public short getVersion() {
		return VERSION;
	}

	protected abstract TSet newSet(int cap);

	public final TSet resolveInstance(TSet destHint, Object[] objects,
			short version, boolean forSerial) {
		if (destHint != null) {
			destHint.clear();
			return destHint;
		} else {
			return this.newSet(objects.length);
		}
	};

	@SuppressWarnings("unchecked")
	public final void recoverData(TSet dest, Object[] objects, short version,
			boolean forSerial) {
		final Set s = dest;
		for (Object o : objects) {
			s.add(o);
		}
	};

	@SuppressWarnings("unchecked")
	public final TSet recoverObject(TSet destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final int size = objects.length;
		Set dest;
		if (destHint != null) {
			destHint.clear();
			dest = destHint;
		} else {
			dest = this.newSet(size);
		}
		for (int i = 0; i < size; i++) {
			dest.add(objects[i]);
		}
		return (TSet) dest;
	}

	public final short supportedVerionMin() {
		return VERSION;
	}

}
