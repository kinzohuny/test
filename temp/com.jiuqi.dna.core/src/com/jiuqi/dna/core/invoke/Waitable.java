package com.jiuqi.dna.core.invoke;

/**
 * �ɵȴ��ӿ�
 * 
 * @author gaojingxin
 */
public interface Waitable {

	/**
	 * �ȴ�ֱ��������ʱ
	 * 
	 * @param timeout
	 *            ��ʱ��������0��ʾ����ʱ��
	 */
	public void waitStop(long timeout) throws InterruptedException;
}
