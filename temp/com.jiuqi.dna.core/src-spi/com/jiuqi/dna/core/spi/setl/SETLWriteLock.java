package com.jiuqi.dna.core.spi.setl;

import com.jiuqi.dna.core.Context;

/**
 * SETLд���ӿ�
 * <p>
 * ͨ������ֹ������ҵ��ģ�鲢��ʱ��������
 * </p>
 * 
 * @author niuhaifeng
 * 
 */
public interface SETLWriteLock {
	/**
	 * ���Լ����������������ؼ����Ƿ�ɹ�
	 * 
	 * @return true�ɹ���falseʧ��
	 */
	public abstract boolean tryLock(Context context);

	/**
	 * ����������
	 */
	public abstract void lock(Context context);

	/**
	 * ����
	 */
	public abstract void release(Context context);
}