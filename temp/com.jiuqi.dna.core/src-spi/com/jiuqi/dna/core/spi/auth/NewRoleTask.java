package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * �½���ɫ����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new NewRoleTask(roleID, roleName);
 * task.title = &quot;role title&quot;;
 * task.state = ActorState.DISABLE;
 * task.description = &quot;description string&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.NewActorTask
 * @author LiuZhi 2009-11
 */
public final class NewRoleTask extends NewActorTask {

	/**
	 * �����½���ɫ����
	 * 
	 * @param id
	 *            ��ɫID������Ϊ��
	 * @param name
	 *            ��ɫ��������Ϊ��
	 */
	public NewRoleTask(GUID id, String name) {
		super(id, name);
	}

}
