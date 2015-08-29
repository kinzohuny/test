package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

public final class MaintainUserAuthorityTask extends MaintainActorAuthorityTask {

	public MaintainUserAuthorityTask(GUID userID, GUID orgID) {
		super(userID, orgID, true);
	}
	
	
	public MaintainUserAuthorityTask(GUID userID, GUID orgID, boolean operationAuthority) {
		super(userID, orgID, operationAuthority);
	}

}
