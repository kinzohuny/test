/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.DataObjectTranslator;

public final class DOT_SingletonTranslator<TSingleton> implements
		DataObjectTranslator<TSingleton, Object> {
	final static short VERSION = 0x0100;
	final TSingleton singleton;

	public final boolean supportAssign() {
		return false;
	}

	public DOT_SingletonTranslator(TSingleton singleton) {
		this.singleton = singleton;
	}

	public final Object toDelegateObject(TSingleton singleton) {
		return null;
	}

	public final short getVersion() {
		return VERSION;
	}

	public final TSingleton resolveInstance(TSingleton destHint,
			Object delegate, short version, boolean forSerial) {
		return this.singleton;
	};

	public final void recoverData(TSingleton dest, Object delegate,
			short version, boolean forSerial) {
		// do nothing
	};

	public short supportedVerionMin() {
		return VERSION;
	}
}