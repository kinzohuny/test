package com.jiuqi.dna.core.spi.application;

/**
 * �Ự��ʼ���ӿڣ����ⲿ����
 * 
 * @author gaojingxin
 * 
 * @param <TUserData>
 */
public interface SessionIniter<TUserData> {
	/**
	 * �Ự��������ø÷������׳��쳣��Ự������
	 * 
	 */
	public void initSession(Session session, TUserData userData)
			throws Throwable;
}
