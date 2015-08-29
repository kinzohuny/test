package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.DataObjectTranslator;

final class DOT_Object implements DataObjectTranslator<Object, Object> {
	final static short VERSION = 0x0100;

	private DOT_Object() {

	}

	public final boolean supportAssign() {
		return false;
	}

	public Object toDelegateObject(Object date) {
		return null;
	}

	public short getVersion() {
		return VERSION;
	}

	public final Object resolveInstance(Object destHint, Object delegate,
			short version, boolean forSerial) {
		return destHint != null && destHint.getClass() == Object.class ? destHint : new Object();
	}

	public final void recoverData(Object dest, Object delegate, short version,
			boolean forSerial) {
		// do nothing
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
