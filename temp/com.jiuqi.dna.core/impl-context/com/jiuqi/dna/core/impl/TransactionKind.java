package com.jiuqi.dna.core.impl;

/**
 * ��������
 * 
 * @author niuhaifeng
 * 
 */
enum TransactionKind {
	/**
	 * ϵͳ��ʼ������
	 */
	SYSTEM_INIT,
	/**
	 * ��Դ��ʼ������
	 */
	CACHE_INIT,
	/**
	 * ��������
	 */
	NORMAL,
	/**
	 * �����첽����/Զ�̵��ò����ı�������
	 */
	TRANSIENT,
	/**
	 * Զ������
	 */
	REMOTE,
	/**
	 * ģ����;�������絥Ԫ���ԡ�
	 */
	SIMULATION,
}
