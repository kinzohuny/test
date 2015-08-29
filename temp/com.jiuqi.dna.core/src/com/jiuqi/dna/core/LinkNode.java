package com.jiuqi.dna.core;

/**
 * 链表节点
 * 
 * @author gaojingxin
 * 
 */
public interface LinkNode<E> {
	/**
	 * 获得链节点的元素
	 */
	public E getElement();

	/**
	 * 链的下一个节点
	 */
	public LinkNode<E> nextNode();
}
