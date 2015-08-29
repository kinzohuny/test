package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * ɾ���û�����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new DeleteUserTask(userID);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.DeleteActorTask
 * @author LiuZhi 2009-11
 */
public final class DeleteUserTask extends DeleteActorTask {

	/**
	 * �½�ɾ���û�����
	 * 
	 * @param roleID
	 *            �û�ID������Ϊ��
	 */
	public DeleteUserTask(GUID userID) {
		super(userID);
	}

}
