package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

public final class GetUserOrgMapForOrgKey {

	public final GUID orgID;

	public GetUserOrgMapForOrgKey(final GUID orgID) {
		this.orgID = orgID;
	}

}
