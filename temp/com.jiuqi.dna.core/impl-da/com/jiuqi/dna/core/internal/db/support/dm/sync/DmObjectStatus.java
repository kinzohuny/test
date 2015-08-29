package com.jiuqi.dna.core.internal.db.support.dm.sync;

public enum DmObjectStatus {

	VALID("VALID"), INVALID("INVALID"), NA("N/A");

	public final String value;

	DmObjectStatus(String value) {
		this.value = value;
	}
}
