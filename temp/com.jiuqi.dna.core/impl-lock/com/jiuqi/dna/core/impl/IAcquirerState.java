package com.jiuqi.dna.core.impl;

interface IAcquirerState {

	/**
	 * ��״̬����
	 */
	final static int MASK_LOCK = 0xff;
	/**
	 * �ڵ�״̬����
	 */
	final static int MASK_NODE = -1 ^ MASK_LOCK;
	/**
	 * ��״̬�ĳ���
	 */
	final static int LOCK_LEN = 8;
	/**
	 * ����������
	 */
	final static byte LOCK_TYPE_S = 1;
	/**
	 * ������������
	 */
	final static byte LOCK_TYPE_U = 2;
	/**
	 * ����������
	 */
	final static byte LOCK_TYPE_X = 3;
	/**
	 * ������
	 */
	final static byte LOCK_MASK_TYPE = LOCK_TYPE_S | LOCK_TYPE_U | LOCK_TYPE_X;
	/**
	 * ������
	 */
	final static byte LOCK_SCOPE_L = 1 << 2;
	/**
	 * ȫ����
	 */
	final static byte LOCK_SCOPE_G = 2 << 2;
	/**
	 * Զ����
	 */
	final static byte LOCK_SCOPE_R = 3 << 2;
	/**
	 * ����Χ
	 */
	final static byte LOCK_MASK_SCOPE = LOCK_SCOPE_L | LOCK_SCOPE_G | LOCK_SCOPE_R;
	/**
	 * �ȴ�
	 */
	final static byte LOCK_STATE_WAITING = 1 << 4;
	/**
	 * �ȴ�Զ������
	 */
	final static byte LOCK_STATE_REQUEST = 1 << 5;
	/**
	 * ׼����
	 */
	final static byte LOCK_STATE_ACQUIRED = 1 << 6;
	/**
	 * ��״̬
	 */
	final static byte LOCK_MASK_STATE = LOCK_STATE_WAITING | LOCK_STATE_REQUEST | LOCK_STATE_ACQUIRED;

	/**
	 * û����������״̬��
	 */
	final static byte LOCK_N = 0;

	/**
	 * ���ع�����(�ȴ���)
	 */
	final static byte LOCK_LSW = LOCK_SCOPE_L | LOCK_TYPE_S | LOCK_STATE_WAITING;
	/**
	 * ���ع�����
	 */
	final static byte LOCK_LS = LOCK_SCOPE_L | LOCK_TYPE_S | LOCK_STATE_ACQUIRED;
	/**
	 * ���ؿ�������(�ȴ���)
	 */
	final static byte LOCK_LUW = LOCK_SCOPE_L | LOCK_TYPE_U | LOCK_STATE_WAITING;
	/**
	 * ���ؿ�������
	 */
	final static byte LOCK_LU = LOCK_SCOPE_L | LOCK_TYPE_U | LOCK_STATE_ACQUIRED;
	/**
	 * ���ض�ռ��(�ȴ���)
	 */
	final static byte LOCK_LXW = LOCK_SCOPE_L | LOCK_TYPE_X | LOCK_STATE_WAITING;
	/**
	 * ���ض�ռ��
	 */
	final static byte LOCK_LX = LOCK_SCOPE_L | LOCK_TYPE_X | LOCK_STATE_ACQUIRED;
	/**
	 * ȫ���޸���(�ȴ���)
	 */
	final static byte LOCK_GUW = LOCK_SCOPE_G | LOCK_TYPE_U | LOCK_STATE_WAITING;
	/**
	 * ȫ�ֿ�������(�ȴ�Զ����)
	 */
	final static byte LOCK_GUR = LOCK_SCOPE_G | LOCK_TYPE_U | LOCK_STATE_REQUEST;
	/**
	 * ȫ�ֿ�������
	 */
	final static byte LOCK_GU = LOCK_SCOPE_G | LOCK_TYPE_U | LOCK_STATE_ACQUIRED;
	/**
	 * ȫ�ֶ�ռ��(�ȴ���)
	 */
	final static byte LOCK_GXW = LOCK_SCOPE_G | LOCK_TYPE_X | LOCK_STATE_WAITING;
	/**
	 * ȫ�ֶ�ռ��(�ȴ�Զ����)
	 */
	final static byte LOCK_GXR = LOCK_SCOPE_G | LOCK_TYPE_X | LOCK_STATE_REQUEST;
	/**
	 * ȫ�ֶ�ռ��
	 */
	final static byte LOCK_GX = LOCK_SCOPE_G | LOCK_TYPE_X | LOCK_STATE_ACQUIRED;
	/**
	 * Զ�̿�������
	 */
	final static byte LOCK_RU = LOCK_SCOPE_R | LOCK_TYPE_U | LOCK_STATE_ACQUIRED;
	/**
	 * Զ�̶�ռ��(�ȴ���)
	 */
	final static byte LOCK_RXW = LOCK_SCOPE_R | LOCK_TYPE_X | LOCK_STATE_WAITING;
	/**
	 * Զ�̶�ռ��
	 */
	final static byte LOCK_RX = LOCK_SCOPE_R | LOCK_TYPE_X | LOCK_STATE_ACQUIRED;
}