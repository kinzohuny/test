package com.jiuqi.dna.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class UpdateIdentifyRoleAssignTask extends SimpleTask {

	/**
	 * �û�ID
	 */
	public final GUID userID;

	/**
	 * ���ID
	 */
	public final GUID identifyID;

	/**
	 * Ϊ��ݷ���Ľ�ɫID�б�<br>
	 * Խ�ȼ�������ȼ�Խ�ߡ�
	 */
	public final List<GUID> assignRoleIDList = new ArrayList<GUID>();

	/**
	 * �½������û���ɫ��������
	 * 
	 * @param userID
	 *            �û�ID
	 * @param identifyID
	 *            ���ID
	 */
	public UpdateIdentifyRoleAssignTask(final GUID userID, final GUID identifyID) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
		this.identifyID = identifyID;
	}

}
