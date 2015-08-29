package com.jiuqi.dna.core;

/**
 * ����������
 * 
 * @author gaojingxin
 * 
 */
public enum ContextKind {
	/**
	 * ϵͳ�ĳ�ʼ�������ģ��ɿ�ܷ���
	 */
	INITER,
	/**
	 * �龰�����ģ��ɿ���ⷢ��
	 */
	SITUATION,
	/**
	 * һ�������ģ��ɿ���ⷢ��
	 */
	NORMAL,
	/**
	 * ��ʱ�����ģ�Ϊ�첽���ú�Զ�̵���׼�����ɿ�ܷ���
	 */
	TRANSIENT,
	/**
	 * �Ự���������ģ��ɿ�ܷ���
	 */
	DISPOSER,
	/**
	 * �ڲ�ʹ�õ������ģ��ɿ�ܷ���
	 */
	INTERNAL;

	/**
	 * �׳���Ч����쳣
	 */
	public static final void throwIllegalContextKind(ContextKind kind) {
		throw new IllegalStateException("��Ч�����������" + kind);
	}
}