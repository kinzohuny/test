package com.jiuqi.dna.core.spi.log;

/**
 * ��־�����
 * 
 * @author gaojingxin
 * 
 */
public enum LogEntryKind {
	/**
	 * ��ʾ<br>
	 */
	HINT,
	/**
	 * ����<br>
	 */
	WARNING,
	/**
	 * ����<br>
	 */
	ERROR,
	/**
	 * ���̿�ʼ<br>
	 */
	PROCESS_BEGIN,
	/**
	 * ���̳ɹ�����
	 */
	PROCESS_SUCCESS,
	/**
	 * ����ʧ�ܽ���
	 */
	PROCESS_FAIL
}
