package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 删除角色任务
 * 
 * <pre>
 * 使用示例：
 * task = new DeleteRoleTask(roleID);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.DeleteActorTask
 * @author LiuZhi 2009-11
 */
public final class DeleteRoleTask extends DeleteActorTask {

	/**
	 * 新建删除角色任务
	 * 
	 * @param roleID
	 *            角色ID，不能为空
	 */
	public DeleteRoleTask(GUID roleID) {
		super(roleID);
	}

}
