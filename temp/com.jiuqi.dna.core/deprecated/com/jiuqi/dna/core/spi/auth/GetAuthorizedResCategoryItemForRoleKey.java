package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * Ϊ��ɫ��Ȩʱ����ȡ���п���Ȩ��Դ�����ļ�<br>
 * �������п���Ȩ��Դ����
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetAuthorizedResCategoryItemForRoleKey(roleID, orgID);
 * context.getList(AuthorityResourceCategoryItem.class, key);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.GetAuthorizedResCategoryItemForActorKey
 * @author LiuZhi 2009-11
 */
@Deprecated
public final class GetAuthorizedResCategoryItemForRoleKey extends
		GetAuthorizedResCategoryItemForActorKey {

	/**
	 * �½���ȡ���п���Ȩ��Դ�����ļ�
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 */
	public GetAuthorizedResCategoryItemForRoleKey(GUID roleID, GUID orgID) {
		super(roleID, orgID);
	}

}
