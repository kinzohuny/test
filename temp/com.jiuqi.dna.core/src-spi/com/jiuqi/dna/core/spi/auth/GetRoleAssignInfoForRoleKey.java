package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.GUID;

/**
 * ��ȡָ����ɫ�Ľ�ɫ������Ϣ�ļ�<br>
 * ���ؼ̳���ָ����ɫ���û��б�
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetRoleAssignInfoForRoleKey(roleID);
 * context.getList(User.class, key);
 * </pre>
 * 
 * @author LiuZhi 2009-11
 */
public final class GetRoleAssignInfoForRoleKey {

	/**
	 * ��ɫID
	 */
	public final GUID roleID;

	/**
	 * �½���ȡָ����ɫ�Ľ�ɫ������Ϣ�ļ�
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 */
	public GetRoleAssignInfoForRoleKey(GUID roleID) {
		if (roleID == null) {
			throw new NullArgumentException("roleID");
		}
		this.roleID = roleID;
	}

}
