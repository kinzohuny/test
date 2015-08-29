package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * Ϊ�û���Ȩʱ����ȡ���п���Ȩ��Դ�����ļ�<br>
 * �����û��ڲ�ͬ����֯�����£�Ȩ��Ҳ����ͬ�����Ի�ȡ���п���Ȩ��Դ�����ʱ������ָ����֯������<br>
 * �������п���Ȩ��Դ����
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetAuthorizedResCategoryItemForUserKey(userID, orgID);
 * context.getList(AuthorityResourceCategoryItem.class, key);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.GetAuthorizedResCategoryItemForActorKey
 * @author LiuZhi 2009-11
 */
@Deprecated
public final class GetAuthorizedResCategoryItemForUserKey extends
		GetAuthorizedResCategoryItemForActorKey {

	/**
	 * �½���ȡ���п���Ȩ��Դ�����ļ�
	 * 
	 * @param userID
	 *            �û�ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 */
	public GetAuthorizedResCategoryItemForUserKey(GUID userID, GUID orgID) {
		super(userID, orgID);
	}

}
