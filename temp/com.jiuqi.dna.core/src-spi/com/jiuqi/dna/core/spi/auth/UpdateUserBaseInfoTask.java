package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * �����û�������Ϣ����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new UpdateUserBaseInfoTask(userID);
 * task.title = &quot;update user title&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.UpdateActorBaseInfoTask
 * @author LiuZhi 2009-11
 */
public final class UpdateUserBaseInfoTask extends UpdateActorBaseInfoTask {

	/**
	 * �½������û�������Ϣ����
	 * 
	 * @param roleID
	 *            �û�ID������Ϊ��
	 */
	public UpdateUserBaseInfoTask(GUID userID) {
		super(userID);
	}
	
	/**
	 * ���º�ķ����߼���Ϊ�ձ�ʾ������
	 */
	public String level;

}
