package com.jiuqi.dna.core.spi.application;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * �����������Ⱥ�����ѡ��
 * 
 * @author gaojingxin
 * 
 */
public final class NumericOverflowOptionTask extends SimpleTask {
	/**
	 * ������ʱ��Ϊ0����null
	 */
	public static final int SET_NULL = 0;
	/**
	 * ������ʱ��Ϊ0����null���Ҵ�ӡ����
	 */
	public static final int SET_NULL_PRINT_ERROR = 1;
	/**
	 * ������ʱ�׳��쳣
	 */
	public static final int THROW_ERROR = 2;
	/**
	 * ģʽ���ο�����ģʽ
	 */
	public final int mode;

	public NumericOverflowOptionTask(int mode) {
		this.mode = mode;
	}
}
