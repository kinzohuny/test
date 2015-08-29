package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.type.DataObjectTranslator;

abstract class DOT_ListBase<TList extends List<?>> implements
		DataObjectTranslator<TList, Object[]> {
	final static short VERSION = 0x0100;

	public final boolean supportAssign() {
		return true;
	}

	public final Object[] toDelegateObject(TList arrayList) {
		final int size = arrayList.size();
		if (size > 0) {
			return arrayList.toArray();
		} else {
			return Utils.emptyObjectArray;
		}
	}

	public short getVersion() {
		return VERSION;
	}

	protected abstract TList newList(int cap);

	public final TList resolveInstance(TList destHint, Object[] objects,
			short version, boolean forSerial) {
		if (destHint != null) {
			destHint.clear();
			return destHint;
		} else {
			return this.newList(objects.length);
		}
	};

	@SuppressWarnings("unchecked")
	public final void recoverData(TList dest, Object[] objects, short version,
			boolean forSerial) {
		final List l = dest;
		for (Object object : objects) {
			l.add(object);
		}
	};

	public final short supportedVerionMin() {
		return VERSION;
	}

}
