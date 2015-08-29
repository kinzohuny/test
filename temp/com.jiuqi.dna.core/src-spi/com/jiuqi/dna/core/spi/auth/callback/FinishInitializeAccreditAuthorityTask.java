package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishInitializeAccreditAuthorityTask extends SimpleTask {

	public FinishInitializeAccreditAuthorityTask(
			// final boolean forUser,
			final GUID actorIdentifier, final GUID ACVersion,
			final AccreditAuthorityInformation authorityInformation) {
		// this.forUser = forUser;
		this.actorIdentifier = actorIdentifier;
		this.ACVersion = ACVersion;
		this.authorityInformation = authorityInformation;
	}

	// public final boolean forUser;

	public final GUID actorIdentifier;

	public final GUID ACVersion;

	public final AccreditAuthorityInformation authorityInformation;

}
