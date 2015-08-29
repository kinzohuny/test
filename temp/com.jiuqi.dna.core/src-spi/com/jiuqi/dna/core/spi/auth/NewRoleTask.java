package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 新建角色任务
 * 
 * <pre>
 * 使用示例：
 * task = new NewRoleTask(roleID, roleName);
 * task.title = &quot;role title&quot;;
 * task.state = ActorState.DISABLE;
 * task.description = &quot;description string&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.NewActorTask
 * @author LiuZhi 2009-11
 */
public final class NewRoleTask extends NewActorTask {

	/**
	 * 创建新建角色任务
	 * 
	 * @param id
	 *            角色ID，不能为空
	 * @param name
	 *            角色名，不能为空
	 */
	public NewRoleTask(GUID id, String name) {
		super(id, name);
	}

}
