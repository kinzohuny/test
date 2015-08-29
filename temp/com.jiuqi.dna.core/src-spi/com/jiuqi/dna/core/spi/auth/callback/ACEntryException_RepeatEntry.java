package com.jiuqi.dna.core.spi.auth.callback;

public final class ACEntryException_RepeatEntry extends RuntimeException {

	public static final ACEntryException_RepeatEntry INSTANCE;

	private static final long serialVersionUID = -2167152685126423113L;

	static {
		INSTANCE = new ACEntryException_RepeatEntry();
	}

	private ACEntryException_RepeatEntry() {
		// do nothing
	}

}
