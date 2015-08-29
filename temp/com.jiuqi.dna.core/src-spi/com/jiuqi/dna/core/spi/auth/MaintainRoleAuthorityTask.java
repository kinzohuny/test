package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

public final class MaintainRoleAuthorityTask extends MaintainActorAuthorityTask {

	public MaintainRoleAuthorityTask(GUID roleID) {
		super(roleID, null, true);
	}

	public MaintainRoleAuthorityTask(GUID roleID, boolean operationAuthority) {
		super(roleID, null, operationAuthority);
	}

	/**
	 * @param orgID
	 *            该参数已失效
	 */
	@Deprecated
	public MaintainRoleAuthorityTask(GUID roleID, GUID orgID) {
		super(roleID, orgID, true);
	}

	/**
	 * @param orgID
	 *            该参数已失效
	 */
	@Deprecated
	public MaintainRoleAuthorityTask(GUID roleID, GUID orgID,
			boolean operationAuthority) {
		super(roleID, orgID, operationAuthority);
	}

}
