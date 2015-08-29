package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * �½�����������
 * 
 * @see com.jiuqi.dna.core.invoke.SimpleTask
 * @author LiuZhi 2009-11
 */
public abstract class NewActorTask extends SimpleTask {

	/**
	 * ������ID
	 */
	public final GUID id;

	/**
	 * ����������
	 */
	public final String name;

	/**
	 * �����߱��⣬Ϊ��ʱĬ��Ϊ����������
	 */
	public String title;

	/**
	 * ������״̬��Ϊ��ʱĬ��Ϊ����״̬
	 */
	public ActorState state;

	/**
	 * ��������������Ϊ��
	 */
	public String description;

	/**
	 * �½�����������
	 * 
	 * @param id
	 *            ������ID������Ϊ��
	 * @param name
	 *            ���������ƣ�����Ϊ��
	 */
	protected NewActorTask(GUID id, String name) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		this.id = id;
		this.name = name;
	}

}
