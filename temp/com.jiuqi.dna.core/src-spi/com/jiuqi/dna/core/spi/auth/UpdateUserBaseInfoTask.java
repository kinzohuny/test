package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 更新用户基本信息任务
 * 
 * <pre>
 * 使用示例：
 * task = new UpdateUserBaseInfoTask(userID);
 * task.title = &quot;update user title&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.UpdateActorBaseInfoTask
 * @author LiuZhi 2009-11
 */
public final class UpdateUserBaseInfoTask extends UpdateActorBaseInfoTask {

	/**
	 * 新建更新用户基本信息任务
	 * 
	 * @param roleID
	 *            用户ID，不能为空
	 */
	public UpdateUserBaseInfoTask(GUID userID) {
		super(userID);
	}
	
	/**
	 * 更新后的访问者级别，为空表示不更新
	 */
	public String level;

}
