package com.jiuqi.dna.core.impl;

/**
 * IntKeyMap访问器过滤器接口
 * 
 * @author niuhaifeng
 * 
 * @param <TValue>
 */
interface ValueVisitorFilter<TValue> {
	/**
	 * 检查值是否要被访问。返回true表示要被访问。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	boolean canVisit(int key, TValue value);
}
