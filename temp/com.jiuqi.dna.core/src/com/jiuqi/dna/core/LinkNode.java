package com.jiuqi.dna.core;

/**
 * ����ڵ�
 * 
 * @author gaojingxin
 * 
 */
public interface LinkNode<E> {
	/**
	 * ������ڵ��Ԫ��
	 */
	public E getElement();

	/**
	 * ������һ���ڵ�
	 */
	public LinkNode<E> nextNode();
}
