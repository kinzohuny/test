package com.jiuqi.dna.core.misc;

/**
 * 边界值
 * 
 * @author houchunlei
 * 
 * @param <T>
 *            值类型，要求可比较。
 */
public final class Boundary<T extends Comparable<T>> {

	/**
	 * 值
	 */
	public final T value;

	/**
	 * 是否包括边界值（开区间or闭区间）
	 */
	public final boolean include;

	public Boundary(T value, boolean include) {
		this.value = value;
		this.include = include;
	}
}