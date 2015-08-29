package com.jiuqi.dna.core.invoke;

/**
 * �첽���������״̬
 * 
 * @author gaojingxin
 * 
 */
public enum AsyncState {
	/**
	 * �ύ���첽���󣨱��ص��ã�
	 */
	POSTING(false, false),
	/**
	 * �ȴ������̵߳ĵ��ȣ����ص��ã�
	 */
	SCHEDULING(false, false),
	/**
	 * ��Ϊ�������Ƶ�ԭ�򣬽��벢�������ж���
	 */
	QUEUING(false, false),
	/**
	 * �Ѿ�����ִ���жӣ�ֻҪ�п��е��߳̾ͻῪʼ����
	 */
	STARTING(false, false),
	/**
	 * ���ڴ���
	 */
	PROCESSING(false, false),
	/**
	 * ����ȡ����
	 */
	CANCELING(false, true),
	/**
	 * ����첽����
	 */
	FINISHED(true, false),
	/**
	 * �������
	 */
	ERROR(true, false),
	/**
	 * ȡ����ֹ
	 */
	CANCELED(true, true),
	/**
	 * ����ȡ���У��ұ��ȴ��������Ѿ����ϣ���״̬��Զ�������
	 */
	@Deprecated
	CANCELING_WAITED(false, true),
	/**
	 * ���ڴ����ұ��ȴ��������Ѿ����ϣ���״̬��Զ�������
	 */
	@Deprecated
	PROCESSING_WAITED(false, false),
	/**
	 * �Ѿ�����ִ���жӣ��ұ��ȴ��������Ѿ����ϣ���״̬��Զ�������
	 */
	@Deprecated
	STARTING_WAITED(false, false),
	/**
	 * ���벢�������ж��У��ұ��ȴ��������Ѿ����ϣ���״̬��Զ�������
	 */
	@Deprecated
	QUEUING_WAITED(false, false);
	/**
	 * �Ƿ��Ѿ�ֹͣ
	 */
	public final boolean stopped;
	/**
	 * �Ƿ���ȡ������
	 */
	public final boolean canceling;

	AsyncState(boolean stopped, boolean canceling) {
		this.stopped = stopped;
		this.canceling = canceling;
	}

	/**
	 * �Ƿ������ȴ����Ѿ����ϣ���Զ����false
	 */
	@Deprecated
	public final boolean waited = false;
}
