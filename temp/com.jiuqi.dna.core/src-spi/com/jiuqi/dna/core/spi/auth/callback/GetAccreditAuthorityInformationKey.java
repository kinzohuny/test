package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.type.GUID;

public final class GetAccreditAuthorityInformationKey {

	public GetAccreditAuthorityInformationKey(// final boolean forUser,
			final GUID actorIdentifier, final GUID ACVersion) {
		// this.forUser = forUser;
		this.actorIdentifier = actorIdentifier;
		this.ACVersion = ACVersion;
	}

	// public final boolean forUser;

	public final GUID actorIdentifier;

	public final GUID ACVersion;

}
