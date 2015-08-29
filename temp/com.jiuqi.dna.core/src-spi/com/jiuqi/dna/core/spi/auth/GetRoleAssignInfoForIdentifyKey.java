package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class GetRoleAssignInfoForIdentifyKey extends SimpleTask {

	/**
	 * 用户ID
	 */
	public final GUID userID;

	public final GUID identifyID;

	/**
	 * 新建获取指定用户角色分配信息的键
	 * 
	 * @param userID
	 *            用户ID，不能为空
	 */
	public GetRoleAssignInfoForIdentifyKey(final GUID userID,
			final GUID identifyID) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
		this.identifyID = identifyID;
	}

}
