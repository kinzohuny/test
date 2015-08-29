package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.GUID;

/**
 * ��ȡָ���û���ɫ������Ϣ�ļ�<br>
 * ���ص���ָ�������û�����ĺ��û����̳еĽ�ɫ�б������ȼ�˳����������û�����Ҳ��������
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetRoleAssignInfoForUserKey(userID);
 * context.getList(Actor.class, key);
 * </pre>
 * 
 * @author LiuZhi 2009-11
 */
public final class GetRoleAssignInfoForUserKey {

	/**
	 * �û�ID
	 */
	public final GUID userID;

	/**
	 * �½���ȡָ���û���ɫ������Ϣ�ļ�
	 * 
	 * @param userID
	 *            �û�ID������Ϊ��
	 */
	public GetRoleAssignInfoForUserKey(GUID userID) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
	}

}
