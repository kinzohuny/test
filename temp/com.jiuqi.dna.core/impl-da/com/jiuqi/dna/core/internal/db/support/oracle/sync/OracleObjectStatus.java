package com.jiuqi.dna.core.internal.db.support.oracle.sync;

public enum OracleObjectStatus {

	VALID("VALID"), INVALID("INVALID"), NA("N/A");

	public final String value;

	OracleObjectStatus(String value) {
		this.value = value;
	}
}