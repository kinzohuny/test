package com.jiuqi.dna.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class UpdateIdentifyRoleAssignTask extends SimpleTask {

	/**
	 * 用户ID
	 */
	public final GUID userID;

	/**
	 * 身份ID
	 */
	public final GUID identifyID;

	/**
	 * 为身份分配的角色ID列表<br>
	 * 越先加入的优先级越高。
	 */
	public final List<GUID> assignRoleIDList = new ArrayList<GUID>();

	/**
	 * 新建更新用户角色分配任务
	 * 
	 * @param userID
	 *            用户ID
	 * @param identifyID
	 *            身份ID
	 */
	public UpdateIdentifyRoleAssignTask(final GUID userID, final GUID identifyID) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
		this.identifyID = identifyID;
	}

}
