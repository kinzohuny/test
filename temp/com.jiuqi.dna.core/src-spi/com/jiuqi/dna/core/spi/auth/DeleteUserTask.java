package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 删除用户任务
 * 
 * <pre>
 * 使用示例：
 * task = new DeleteUserTask(userID);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.DeleteActorTask
 * @author LiuZhi 2009-11
 */
public final class DeleteUserTask extends DeleteActorTask {

	/**
	 * 新建删除用户任务
	 * 
	 * @param roleID
	 *            用户ID，不能为空
	 */
	public DeleteUserTask(GUID userID) {
		super(userID);
	}

}
