package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.type.GUID;

public final class IdentifyMapEntry {
	
	public IdentifyMapEntry(final GUID identifier, final GUID userIdentifier,
			final GUID identifyIdentifier) {
		this.identifier = identifier;
		this.userIdentifier = userIdentifier;
		this.identifyIdentifier = identifyIdentifier;
	}
	
	public final GUID identifier;

	public final GUID userIdentifier;

	public final GUID identifyIdentifier;

}
