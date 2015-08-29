package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishInitializeOperationAuthorityTask extends SimpleTask {

	public FinishInitializeOperationAuthorityTask(
			// final boolean forUser,
			final GUID actorIdentifier,
			final GUID ACVersion,
			final OperationAuthorityInformation authorityInformation) {
		// this.forUser = forUser;
		this.actorIdentifier = actorIdentifier;
		this.ACVersion = ACVersion;
		this.authorityInformation = authorityInformation;
	}

	// public final boolean forUser;

	public final GUID actorIdentifier;
	
	public final GUID ACVersion;

	public final OperationAuthorityInformation authorityInformation;

}
