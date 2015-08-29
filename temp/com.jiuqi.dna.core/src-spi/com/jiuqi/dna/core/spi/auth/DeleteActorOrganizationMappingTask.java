package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * ɾ����������֯����ӳ������
 * 
 * @author LiuZhi 2010-01
 */
public abstract class DeleteActorOrganizationMappingTask extends SimpleTask {

	/**
	 * ������ID
	 */
	public final GUID actorID;

	/**
	 * ��֯����ID
	 */
	public final GUID orgID;

	/**
	 * �½�ɾ����������֯����ӳ������
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID������Ϊ��
	 */
	protected DeleteActorOrganizationMappingTask(GUID actorID, GUID orgID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		if (orgID == null) {
			throw new NullArgumentException("orgID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
	}

}
