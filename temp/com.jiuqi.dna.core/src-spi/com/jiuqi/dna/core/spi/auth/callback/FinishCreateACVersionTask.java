package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishCreateACVersionTask extends SimpleTask {

	public FinishCreateACVersionTask(final boolean forUser,
			final GUID identifier,
			final GUID actorIdentifier, final GUID ACVersion) {
		this.forUser = forUser;
		this.identifier = identifier;
		this.actorIdentifier = actorIdentifier;
		this.ACVersion = ACVersion;
	}

	public final boolean forUser;
	
	public final GUID identifier;

	public final GUID actorIdentifier;

	public final GUID ACVersion;

}
