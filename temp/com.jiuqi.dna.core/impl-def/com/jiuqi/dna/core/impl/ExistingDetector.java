package com.jiuqi.dna.core.impl;

/**
 * ����̽����
 * 
 * @author gaojingxin
 * 
 * @param <TContainer>
 * @param <TElement>
 * @param <TKey>
 */
interface ExistingDetector<TContainer, TElement, TKey> {

	/**
	 * ��������Ƿ����
	 */
	boolean exists(TContainer container, TKey key, TElement ignore);
}