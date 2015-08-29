package com.jiuqi.dna.core.impl;

/**
 * IntKeyMap�������������ӿ�
 * 
 * @author niuhaifeng
 * 
 * @param <TValue>
 */
interface ValueVisitorFilter<TValue> {
	/**
	 * ���ֵ�Ƿ�Ҫ�����ʡ�����true��ʾҪ�����ʡ�
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	boolean canVisit(int key, TValue value);
}
