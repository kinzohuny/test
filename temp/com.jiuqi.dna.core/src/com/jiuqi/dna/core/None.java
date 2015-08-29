package com.jiuqi.dna.core;

/**
 * 表示空，无效，等意思的单例枚举
 * 
 * @author gaojingxin
 * 
 */
public enum None {
	/**
	 * 空
	 */
	NONE;
	/**
	 * 缓存hashCode
	 */
	public final static int hashcode = NONE.hashCode();
}
