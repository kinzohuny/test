package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * ɾ����ɫ����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new DeleteRoleTask(roleID);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.DeleteActorTask
 * @author LiuZhi 2009-11
 */
public final class DeleteRoleTask extends DeleteActorTask {

	/**
	 * �½�ɾ����ɫ����
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 */
	public DeleteRoleTask(GUID roleID) {
		super(roleID);
	}

}
