package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * ��֧�ֽ�ɫ����֯����ӳ��<br>
 * 
 * ɾ����ɫ��֯����ӳ������
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new DeleteRoleOrganizationMappingTask(roleID, orgID);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.DeleteActorOrganizationMappingTask
 * @author LiuZhi 2010-01
 */
@Deprecated
public final class DeleteRoleOrganizationMappingTask extends
		DeleteActorOrganizationMappingTask {

	/**
	 * ��֧�ֽ�ɫ����֯����ӳ��<br>
	 * 
	 * �½�ɾ����ɫ��֯����ӳ������
	 * 
	 * @param actorID
	 *            ��ɫID������Ϊ��
	 * @param orgID
	 *            ��֯����ID������Ϊ��
	 */
	@Deprecated
	public DeleteRoleOrganizationMappingTask(GUID roleID, GUID orgID) {
		super(roleID, orgID);
	}

}
