package com.jiuqi.dna.core.situation;

/**
 * �ж���Ϣ
 * 
 * @author gaojingxin
 * 
 * @param <TMessage>
 */
public interface PendingMessage<TMessage> {
	/**
	 * ��ȡ���ж���Ϣ�Ƿ���Ч
	 */
	public boolean isValid();

	/**
	 * ��÷���
	 */
	public MessageDirection getDirection();

	/**
	 * ���Եõ����
	 * 
	 * @return
	 */
	public MessageResult<TMessage> tryGetResult();
}
