package com.jiuqi.dna.core.info;


/**
 * ���̴�����Ϣ
 * 
 * @author gaojingxin
 * 
 */
public interface ProcessInfo extends Info {
	/**
	 * ��ȡ����ʱ��
	 */
	public long getDuration();

	/**
	 * �Ƿ��д���
	 */
	public boolean hasError();

	/**
	 * �����Ƿ����
	 */
	public boolean isFinished();
}
