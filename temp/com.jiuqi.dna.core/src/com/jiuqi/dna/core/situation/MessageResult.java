package com.jiuqi.dna.core.situation;

/**
 * ��Ϣ���
 * 
 * @author gaojingxin
 * 
 * @param <TMessage>
 */
public interface MessageResult<TMessage> {

	/**
	 * ��õ�ǰ��Ϣ����
	 */
	public TMessage getMessage();

	/**
	 * ��ȡ����Ϣ�˴δ��䴫�ݵ���ǰ������ǰ�Ѿ��������������Ĵ���
	 */
	public int getListeneds();

	/**
	 * �Ƿ���ֹ
	 */
	public boolean isTerminated();
}
