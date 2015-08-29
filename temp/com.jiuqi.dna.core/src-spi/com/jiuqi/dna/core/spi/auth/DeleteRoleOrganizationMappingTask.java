package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 不支持角色的组织机构映射<br>
 * 
 * 删除角色组织机构映射任务
 * 
 * <pre>
 * 使用示例：
 * task = new DeleteRoleOrganizationMappingTask(roleID, orgID);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.DeleteActorOrganizationMappingTask
 * @author LiuZhi 2010-01
 */
@Deprecated
public final class DeleteRoleOrganizationMappingTask extends
		DeleteActorOrganizationMappingTask {

	/**
	 * 不支持角色的组织机构映射<br>
	 * 
	 * 新建删除角色组织机构映射任务
	 * 
	 * @param actorID
	 *            角色ID，不能为空
	 * @param orgID
	 *            组织机构ID，不能为空
	 */
	@Deprecated
	public DeleteRoleOrganizationMappingTask(GUID roleID, GUID orgID) {
		super(roleID, orgID);
	}

}
