package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * �����û���Ȩ����<br>
 * �����û��ڲ�ͬ����֯�����£�Ȩ��Ҳ����ͬ�������ڸ����û���Ȩʱ������ָ����֯������
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new UpdateUserAuthorityTask(roleID, orgID, resourceCategoryID);
 * task.authorityResourceTable.add(authorizedResourceItem);
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.UpdateActorAuthorityTask
 * @author LiuZhi 2009-11
 */
@Deprecated
public final class UpdateUserAuthorityTask extends UpdateActorAuthorityTask {

	/**
	 * �½������û���Ȩ����
	 * 
	 * @param userID
	 *            �û�ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 * @param resourceCategoryID
	 *            ��Դ���ID������Ϊ��
	 */
	public UpdateUserAuthorityTask(GUID userID, GUID orgID,
			GUID resourceCategoryID) {
		super(userID, orgID, resourceCategoryID);
	}

}
