package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 更新角色授权任务
 * 
 * <pre>
 * 使用示例：
 * task = new UpdateRoleAuthorityTask(roleID, orgID, resourceCategoryID);
 * task.authorityResourceTable.add(authorizedResourceItem);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.UpdateActorAuthorityTask
 * @author LiuZhi 2009-11
 */
@Deprecated
public final class UpdateRoleAuthorityTask extends UpdateActorAuthorityTask {

	/**
	 * 新建更新角色授权任务
	 * 
	 * @param roleID
	 *            角色ID，不能为空
	 * @param orgID
	 *            组织机构ID，可为空，为空代表默认关联的组织机构ID
	 * @param resourceCategoryID
	 *            资源类别ID，不能为空
	 */
	public UpdateRoleAuthorityTask(GUID roleID, GUID orgID,
			GUID resourceCategoryID) {
		super(roleID, orgID, resourceCategoryID);
	}

}
