package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

public final class ClearRoleAuthorityTask extends ClearActorAuthorityTask {

	public ClearRoleAuthorityTask(final GUID roleID,
			final boolean operationAuthority) {
		super(roleID, null, operationAuthority);
	}

	/**
	 * @param orgID
	 *            �ò�����ʧЧ
	 */
	public ClearRoleAuthorityTask(final GUID roleID, final GUID orgID,
			final boolean operationAuthority) {
		super(roleID, orgID, operationAuthority);
	}

}
