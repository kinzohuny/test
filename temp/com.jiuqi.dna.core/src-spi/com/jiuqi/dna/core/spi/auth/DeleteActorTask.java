package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * ɾ������������
 * 
 * @see com.jiuqi.dna.core.invoke.SimpleTask
 * @author LiuZhi 2009-11
 */
public abstract class DeleteActorTask extends SimpleTask {

	/**
	 * ������ID
	 */
	public final GUID actorID;

	/**
	 * �½�ɾ������������
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 */
	protected DeleteActorTask(GUID actorID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
	}

}
