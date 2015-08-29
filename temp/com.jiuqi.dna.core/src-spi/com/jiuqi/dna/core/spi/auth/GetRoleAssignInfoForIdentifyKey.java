package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class GetRoleAssignInfoForIdentifyKey extends SimpleTask {

	/**
	 * �û�ID
	 */
	public final GUID userID;

	public final GUID identifyID;

	/**
	 * �½���ȡָ���û���ɫ������Ϣ�ļ�
	 * 
	 * @param userID
	 *            �û�ID������Ϊ��
	 */
	public GetRoleAssignInfoForIdentifyKey(final GUID userID,
			final GUID identifyID) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
		this.identifyID = identifyID;
	}

}
