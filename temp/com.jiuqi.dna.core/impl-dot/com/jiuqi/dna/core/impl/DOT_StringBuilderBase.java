package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.DataObjectTranslator;

abstract class DOT_StringBuilderBase<TStringBuilder> implements
		DataObjectTranslator<TStringBuilder, char[]> {

	final static short VERSION = 0x0100;

	public final short getVersion() {
		return VERSION;
	}

	public final boolean supportAssign() {
		return true;
	}

	public final short supportedVerionMin() {
		return VERSION;
	}

}
