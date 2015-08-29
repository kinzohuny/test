package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

public final class ClearRoleAuthorityTask extends ClearActorAuthorityTask {

	public ClearRoleAuthorityTask(final GUID roleID,
			final boolean operationAuthority) {
		super(roleID, null, operationAuthority);
	}

	/**
	 * @param orgID
	 *            该参数已失效
	 */
	public ClearRoleAuthorityTask(final GUID roleID, final GUID orgID,
			final boolean operationAuthority) {
		super(roleID, orgID, operationAuthority);
	}

}
