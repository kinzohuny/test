package com.jiuqi.dna.core.impl;

interface ITransactionMessage {
	/**
	 * ������Ϣ��ʶ
	 */
	final static byte MSG_MASK_TRANS = 0x10;
	/**
	 * ����Ϣ��ʶ
	 */
	final static byte MSG_MASK_LOCK = 0x20;
	/**
	 * ͬ����Ϣ��ʶ
	 */
	final static byte MSG_MASK_SYNC = 0x30;
	/**
	 * ��Ϣ���ύ
	 */
	final static byte MSG_COMMIT = MSG_MASK_TRANS | 0x01;
	/**
	 * ��Ϣ���ع�
	 */
	final static byte MSG_ROLLBACK = MSG_MASK_TRANS | 0x02;
	/**
	 * ��Ϣ����������״̬
	 */
	final static byte MSG_RESET = MSG_MASK_TRANS | 0x03;
	/**
	 * ��Ϣ������
	 */
	final static byte MSG_DISPOSE = MSG_MASK_TRANS | 0x04;
	/**
	 * ��Ϣ����ѯ�����״̬
	 */
	final static byte MSG_LOOKUP = MSG_MASK_TRANS | 0x05;
	/**
	 * �ظ�������״̬
	 */
	final static byte MSG_LOOKUP_RESULT = MSG_MASK_TRANS | 0x06;
	/**
	 * ������δ֪״̬
	 */
	final static byte RESULT_UNKNOWN = 0;
	/**
	 * ���������ύ
	 */
	final static byte RESULT_COMMIT = 0x01;
	/**
	 * �������ѻع�
	 */
	final static byte RESULT_ROLLBACK = 0x02;
	/**
	 * ��Ϣ������
	 */
	final static byte MSG_ACQUIRE = MSG_MASK_LOCK | 0x01;
	/**
	 * ��������Դ���ͣ���Դ����
	 */
	final static byte PARAM_TYPE_CACHE = 0x01;
	/**
	 * ��������Դ���ͣ���Դ��
	 */
	final static byte PARAM_TYPE_GROUP = 0x02;
	/**
	 * ��������Դ���ͣ���Դ��
	 */
	final static byte PARAM_TYPE_ITEM = 0x03;
	/**
	 * ������������ʽ�����
	 */
	final static byte PARAM_METHOD_ADD = 0x01;
	/**
	 * ������������ʽ����
	 */
	final static byte PARAM_METHOD_READ = 0x02;
	/**
	 * ������������ʽ���޸�
	 */
	final static byte PARAM_METHOD_MODIFY = 0x03;
	/**
	 * ������������ʽ���޸�����������
	 */
	final static byte PARAM_METHOD_MODIFY_ITEMS = 0x04;
	/**
	 * ������������ʽ��ɾ��
	 */
	final static byte PARAM_METHOD_REMOVE = 0x05;
	/**
	 * ������������ʽ���ύ
	 */
	final static byte PARAM_METHOD_COMMIT = 0x06;
	/**
	 * �ظ�������
	 */
	final static byte MSG_ACQUIRE_RESULT = MSG_MASK_LOCK | 0x02;
	/**
	 * �����������ɹ�
	 */
	final static byte RESULT_SUCCEED = 0x01;
	/**
	 * ����������ʧ��
	 */
	final static byte RESULT_FAIL = 0x02;
	/**
	 * ��Ϣ��������
	 */
	final static byte MSG_UPGRADE = MSG_MASK_LOCK | 0x03;
	/**
	 * ��Ϣ������
	 */
	final static byte MSG_RELEASE = MSG_MASK_LOCK | 0x04;
}
