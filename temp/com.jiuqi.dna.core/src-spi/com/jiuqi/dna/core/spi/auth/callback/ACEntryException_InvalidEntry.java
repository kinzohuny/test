package com.jiuqi.dna.core.spi.auth.callback;

public final class ACEntryException_InvalidEntry extends RuntimeException {

	public static final ACEntryException_InvalidEntry INSTANCE;

	private static final long serialVersionUID = 4608652101215061321L;

	static {
		INSTANCE = new ACEntryException_InvalidEntry();
	}

	private ACEntryException_InvalidEntry() {
		// do nothing
	}

}
