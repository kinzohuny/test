package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 删除用户组织机构映射任务
 * 
 * <pre>
 * 使用示例：
 * task = new DeleteUserOrganizationMappingTask(userID, orgID);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.DeleteActorOrganizationMappingTask
 * @author LiuZhi 2010-01
 */
public final class DeleteUserOrganizationMappingTask extends
		DeleteActorOrganizationMappingTask {

	/**
	 * 新建删除用户组织机构映射任务
	 * 
	 * @param actorID
	 *            用户ID，不能为空
	 * @param orgID
	 *            组织机构ID，不能为空
	 */
	public DeleteUserOrganizationMappingTask(GUID userID, GUID orgID) {
		super(userID, orgID);
	}

}
