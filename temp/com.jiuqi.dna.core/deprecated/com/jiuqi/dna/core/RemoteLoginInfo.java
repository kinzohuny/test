package com.jiuqi.dna.core;

/**
 * Զ�̵�¼��Ϣ
 * 
 * @author gaojingxin
 * 
 */
@Deprecated
public interface RemoteLoginInfo {
	/**
	 * ��ÿռ�����Ӧ���м��������
	 */
	String getHost();

	/**
	 * ��ÿռ�����Ӧ���м���Ķ˿�
	 */
	int getPort();

	/**
	 * ����Ƿ�ʹ�ð�ȫ��TLS/SSL��������
	 */
	boolean isSecure();

	/**
	 * ��ȡ�ÿռ����ӵĵ�½�û�
	 */
	public String getUser();

	/**
	 * ���Զ�̵�¼����������
	 */
	public RemoteLoginLife getLife();
}
