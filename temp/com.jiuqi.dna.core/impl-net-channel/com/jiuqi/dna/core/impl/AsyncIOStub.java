package com.jiuqi.dna.core.impl;

/**
 * �첽������
 * 
 * @author gaojingxin
 * 
 */
public interface AsyncIOStub<TAttachment> {
	/**
	 * ȡ���첽����
	 */
	public void cancel();

	/**
	 * ����
	 */
	public void suspend();

	/**
	 * �ָ�
	 */
	public void resume();

	/**
	 * ��ȡ����Ӧ�ĸ���
	 */
	public TAttachment getAttachment();
}
