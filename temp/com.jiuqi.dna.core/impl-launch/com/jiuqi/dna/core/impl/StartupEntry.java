package com.jiuqi.dna.core.impl;

/**
 * ���������
 * 
 * @author gaojingxin
 * 
 */
public abstract class StartupEntry {

	/**
	 * ����ʱʹ�ã�ͬһ���е���һ��Ԫ�أ�������
	 */
	StartupEntry nextInStep;

	/**
	 * �����Ŀ�����ȼ�
	 */
	float getPriority(StartupStep<?> step) {
		return 0.0f;
	}
}
