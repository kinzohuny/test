package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

public final class ClearUserAuthorityTask extends ClearActorAuthorityTask {

	public ClearUserAuthorityTask(final GUID userID, final GUID orgID,
			final boolean operationAuthority) {
		super(userID, orgID, operationAuthority);
	}

}
