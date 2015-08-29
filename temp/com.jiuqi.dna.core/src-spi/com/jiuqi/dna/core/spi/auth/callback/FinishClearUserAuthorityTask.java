package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.type.GUID;

public final class FinishClearUserAuthorityTask extends FinishClearActorAuthorityTask {

	public FinishClearUserAuthorityTask(final GUID userIdentifier, final GUID ACVersion,
			final boolean operationAuthority) {
		super(userIdentifier, ACVersion, operationAuthority);
	}

}
