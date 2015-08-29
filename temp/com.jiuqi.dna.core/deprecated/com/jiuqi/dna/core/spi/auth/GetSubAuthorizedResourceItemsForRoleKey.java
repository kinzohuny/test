package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * Ϊ��ɫ��Ȩʱ����ȡָ������Ȩ��Դ����¼���Դ���б�ļ�<br>
 * ���ؿ���Ȩ��Դ���б�
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetSubAuthorizedResourceItemsForRoleKey(roleID, orgID, resourceCategoryID, currentResID);
 * context.getList(AuthorizedResourceItem.class, key);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.GetSubAuthorizedResourceItemsForActorKey
 * @author LiuZhi 2009-11
 */
@Deprecated
public final class GetSubAuthorizedResourceItemsForRoleKey extends
		GetSubAuthorizedResourceItemsForActorKey {

	/**
	 * �½���ȡָ������Ȩ��Դ����¼���Դ���б�ļ�
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�ձ�ʾȫ����֯����
	 * @param resourceCategoryID
	 *            ��Դ���ID������Ϊ��
	 * @param currentResID
	 *            ��ǰ��Ȩ��Դ���Ϊ��
	 */
	public GetSubAuthorizedResourceItemsForRoleKey(GUID roleID, GUID orgID,
			GUID resourceCategoryID, GUID currentResID) {
		super(roleID, orgID, resourceCategoryID, currentResID);
	}

}
