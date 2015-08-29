package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * Ϊ�û���Ȩʱ����ȡָ������Ȩ��Դ����¼���Դ���б�ļ�<br>
 * �����û��ڲ�ͬ����֯�����£�Ȩ��Ҳ����ͬ�����Ի�ȡ�¼���Դ��ʱ������ָ����֯������<br>
 * ���ؿ���Ȩ��Դ���б�
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetSubAuthorizedResourceItemsForUserKey(userID, orgID, resourceCategoryID, currentResID);
 * context.getList(AuthorizedResourceItem.class, key);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.GetSubAuthorizedResourceItemsForActorKey
 * @author LiuZhi 2009-11
 */
@Deprecated
public final class GetSubAuthorizedResourceItemsForUserKey extends
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
	public GetSubAuthorizedResourceItemsForUserKey(GUID userID, GUID orgID,
			GUID resourceCategoryID, GUID currentResID) {
		super(userID, orgID, resourceCategoryID, currentResID);
	}

}
