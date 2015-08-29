package com.jiuqi.dna.core.misc;

/**
 * �߽�ֵ
 * 
 * @author houchunlei
 * 
 * @param <T>
 *            ֵ���ͣ�Ҫ��ɱȽϡ�
 */
public final class Boundary<T extends Comparable<T>> {

	/**
	 * ֵ
	 */
	public final T value;

	/**
	 * �Ƿ�����߽�ֵ��������or�����䣩
	 */
	public final boolean include;

	public Boundary(T value, boolean include) {
		this.value = value;
		this.include = include;
	}
}