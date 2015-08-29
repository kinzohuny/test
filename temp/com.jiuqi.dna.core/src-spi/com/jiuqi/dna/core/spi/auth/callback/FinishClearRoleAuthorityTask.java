package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.type.GUID;

public final class FinishClearRoleAuthorityTask extends FinishClearActorAuthorityTask {

	public FinishClearRoleAuthorityTask(final GUID roleIdentifier,
			final GUID ACVersion,
			final boolean operationAuthority) {
		super(roleIdentifier, ACVersion, operationAuthority);
	}

}
