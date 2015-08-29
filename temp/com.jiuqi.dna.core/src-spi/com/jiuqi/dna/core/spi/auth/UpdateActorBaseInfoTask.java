package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * ���·����߻�����Ϣ����
 * 
 * @see com.jiuqi.dna.core.invoke.SimpleTask
 * @author LiuZhi 2009-11
 */
public abstract class UpdateActorBaseInfoTask extends SimpleTask {

	/**
	 * ������ID
	 */
	public final GUID actorID;

	/**
	 * ���º�ķ��������ƣ�Ϊ�ձ�ʾ�����£����������е�������
	 */
	public String name;
	
	/**
	 * ���º�ķ����߱��⣬Ϊ�ձ�ʾ������
	 */
	public String title;

	/**
	 * ���º�ķ�����״̬��Ϊ�ձ�ʾ������
	 */
	public ActorState state;

	/**
	 * ���º�ķ�����������Ϊ�ձ�ʾ������
	 */
	public String description;

	/**
	 * �½����·����߻�����Ϣ����
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 */
	protected UpdateActorBaseInfoTask(GUID actorID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
	}

}
