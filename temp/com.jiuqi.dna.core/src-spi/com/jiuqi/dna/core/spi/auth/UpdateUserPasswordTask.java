package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * 更新用户密码任务
 * 
 * <pre>
 * 使用示例：
 * task = new UpdateUserPasswordTask(userID, newPassword);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.invoke.SimpleTask
 * @author LiuZhi 2009-11
 */
public final class UpdateUserPasswordTask extends SimpleTask {

	/**
	 * 用户ID
	 */
	public final GUID userID;

	/**
	 * 新密码
	 */
	public final String newPassword;

	public boolean passwordNeedEncrypt;

	/**
	 * 新建更新用户密码任务
	 * 
	 * @param userID
	 *            用户ID，不能为空
	 * @param newPassword
	 *            新密码，不能为空
	 */
	public UpdateUserPasswordTask(GUID userID, String newPassword) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
		if (newPassword == null || newPassword.length() == 0) {
			this.newPassword = "";
		} else {
			this.newPassword = newPassword;
		}
		this.passwordNeedEncrypt = true;
	}

}
