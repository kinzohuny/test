package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * ɾ���û���֯����ӳ������
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new DeleteUserOrganizationMappingTask(userID, orgID);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.DeleteActorOrganizationMappingTask
 * @author LiuZhi 2010-01
 */
public final class DeleteUserOrganizationMappingTask extends
		DeleteActorOrganizationMappingTask {

	/**
	 * �½�ɾ���û���֯����ӳ������
	 * 
	 * @param actorID
	 *            �û�ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID������Ϊ��
	 */
	public DeleteUserOrganizationMappingTask(GUID userID, GUID orgID) {
		super(userID, orgID);
	}

}
