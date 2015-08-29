package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * �����û���������
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new UpdateUserPasswordTask(userID, newPassword);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.invoke.SimpleTask
 * @author LiuZhi 2009-11
 */
public final class UpdateUserPasswordTask extends SimpleTask {

	/**
	 * �û�ID
	 */
	public final GUID userID;

	/**
	 * ������
	 */
	public final String newPassword;

	public boolean passwordNeedEncrypt;

	/**
	 * �½������û���������
	 * 
	 * @param userID
	 *            �û�ID������Ϊ��
	 * @param newPassword
	 *            �����룬����Ϊ��
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
