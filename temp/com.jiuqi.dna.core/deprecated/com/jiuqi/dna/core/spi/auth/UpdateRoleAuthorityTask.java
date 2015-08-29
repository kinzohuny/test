package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * ���½�ɫ��Ȩ����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new UpdateRoleAuthorityTask(roleID, orgID, resourceCategoryID);
 * task.authorityResourceTable.add(authorizedResourceItem);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.UpdateActorAuthorityTask
 * @author LiuZhi 2009-11
 */
@Deprecated
public final class UpdateRoleAuthorityTask extends UpdateActorAuthorityTask {

	/**
	 * �½����½�ɫ��Ȩ����
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 * @param resourceCategoryID
	 *            ��Դ���ID������Ϊ��
	 */
	public UpdateRoleAuthorityTask(GUID roleID, GUID orgID,
			GUID resourceCategoryID) {
		super(roleID, orgID, resourceCategoryID);
	}

}
