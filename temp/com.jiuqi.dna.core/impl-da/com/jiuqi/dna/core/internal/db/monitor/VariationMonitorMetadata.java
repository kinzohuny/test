package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.type.GUID;

final class VariationMonitorMetadata {

	VariationMonitorMetadata(GUID id, String name) {
		this.id = id;
		this.name = name;
	}

	final GUID id;
	final String name;
	long version;
	String target;
	String variation;
	String trigger;
	String setting;
}