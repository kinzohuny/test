package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * �л��Ự�û�����<br>
 * ͨ������£��û���ҵ���߼���ִ�����¼�߼�����ͨ���������û�����Ϊ�Ự�ĵ�ǰ�û���
 * 
 * <pre>
 * ʹ��ʾ����
 * context.handle(new ChangeSessionUserTask(user));
 * </pre>
 * 
 * @see com.jiuqi.dna.core.invoke.SimpleTask
 * @author LiuZhi 2009-12
 */
public final class ChangeSessionUserTask extends SimpleTask {

	/**
	 * �û�����
	 */
	public final User user;

	public User sessionUserBeforeChange;

	/**
	 * �½��л��Ự�û�����
	 * 
	 * @param user
	 *            �û�
	 */
	public ChangeSessionUserTask(User user) {
		if (user == null) {
			throw new NullArgumentException("user");
		}
		this.user = user;
	}

}
