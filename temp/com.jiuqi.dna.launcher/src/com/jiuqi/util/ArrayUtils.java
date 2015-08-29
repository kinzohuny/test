/**
 * 
 */
package com.jiuqi.util;

/**
 * 数组工具类
 * @author 黄凯斌
 */
public class ArrayUtils {

	/**
	 * 判断数组中是否存在指定的对象
	 * @param <T> 数组类型
	 * @param array 数组
	 * @param obj 对象
	 * @return 如果数组中存在指定的对象，或对象为<code>null</code>，且数组中包含<code>null</code>，则返回<tt>true</tt>，否则返回<tt>false</tt>。
	 */
	public static <T> boolean contains(T[] array, T obj) {
		if (array == null) {
			throw new IllegalArgumentException("参数 array 不能为 null ！");
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
	 * 把数组中fromIndex开始连续length个元素，移动到toIndex所指定的元素之前
	 * @param <T> 数组类型
	 * @param array 数组
	 * @param fromIndex 
	 * @param toIndex 目标位置
	 * @param length 移动数列的长度
	 */
	@SuppressWarnings("unchecked")
	public static <T> void move(T[] array, int fromIndex, int toIndex, int length) {
		if (array == null) {
			throw new IllegalArgumentException("参数 array 不能为 null ！");
		}
		if (fromIndex <= -1 || toIndex <= -1 || fromIndex >= array.length
				|| toIndex > array.length || length < 0) {
			throw new IllegalArgumentException();
		}
		if (length == 0) {
			// 没有东西需要移动
			return;
		}
		if (fromIndex + length > array.length) {
			throw new IllegalArgumentException();
		}
		if (fromIndex <= toIndex && fromIndex + length > toIndex) {
			// 不需要移动
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
