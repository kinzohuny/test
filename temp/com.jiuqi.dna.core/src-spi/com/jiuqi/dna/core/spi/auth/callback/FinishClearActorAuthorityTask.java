package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public abstract class FinishClearActorAuthorityTask extends SimpleTask {

	FinishClearActorAuthorityTask(final GUID actorIdentifier, final GUID ACVersion,
			final boolean operationAuthority) {
		if (actorIdentifier == null) {
			throw new IllegalArgumentException("����[actorIdentifier]����Ϊnull��");
		}
		this.actorIdentifier = actorIdentifier;
		this.ACVersion = ACVersion;
		this.operationAuthority = operationAuthority;
	}

	public final GUID actorIdentifier;

	public final GUID ACVersion;

	public final boolean operationAuthority;

}
