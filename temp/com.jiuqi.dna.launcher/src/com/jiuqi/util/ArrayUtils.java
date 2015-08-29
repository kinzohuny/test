/**
 * 
 */
package com.jiuqi.util;

/**
 * ���鹤����
 * @author �ƿ���
 */
public class ArrayUtils {

	/**
	 * �ж��������Ƿ����ָ���Ķ���
	 * @param <T> ��������
	 * @param array ����
	 * @param obj ����
	 * @return ��������д���ָ���Ķ��󣬻����Ϊ<code>null</code>���������а���<code>null</code>���򷵻�<tt>true</tt>�����򷵻�<tt>false</tt>��
	 */
	public static <T> boolean contains(T[] array, T obj) {
		if (array == null) {
			throw new IllegalArgumentException("���� array ����Ϊ null ��");
		}
		for (T elem : array) {
			if ((elem == null && obj == null)
					|| (elem != null && elem.equals(obj))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ��������fromIndex��ʼ����length��Ԫ�أ��ƶ���toIndex��ָ����Ԫ��֮ǰ
	 * @param <T> ��������
	 * @param array ����
	 * @param fromIndex 
	 * @param toIndex Ŀ��λ��
	 * @param length �ƶ����еĳ���
	 */
	@SuppressWarnings("unchecked")
	public static <T> void move(T[] array, int fromIndex, int toIndex, int length) {
		if (array == null) {
			throw new IllegalArgumentException("���� array ����Ϊ null ��");
		}
		if (fromIndex <= -1 || toIndex <= -1 || fromIndex >= array.length
				|| toIndex > array.length || length < 0) {
			throw new IllegalArgumentException();
		}
		if (length == 0) {
			// û�ж�����Ҫ�ƶ�
			return;
		}
		if (fromIndex + length > array.length) {
			throw new IllegalArgumentException();
		}
		if (fromIndex <= toIndex && fromIndex + length > toIndex) {
			// ����Ҫ�ƶ�
			return;
		}
		T[] temp = (T[]) new Object[length];
		System.arraycopy(array, fromIndex, temp, 0, length);
		if (fromIndex > toIndex) {
			System.arraycopy(array, toIndex, array, toIndex + length, fromIndex - toIndex);
			System.arraycopy(temp, 0, array, toIndex, length);
		} else {
			System.arraycopy(array, fromIndex + length, array, fromIndex, toIndex - fromIndex - length);
			System.arraycopy(temp, 0, array, toIndex - length, length);
		}
		return;
	}

}
