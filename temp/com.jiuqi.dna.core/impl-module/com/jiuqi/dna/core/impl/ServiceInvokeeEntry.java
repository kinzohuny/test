package com.jiuqi.dna.core.impl;

/**
 * 模块调用器项
 * 
 * <p>
 * 根据targetClass的hash值来索引。
 * 
 * @author gaojingxin
 * 
 */
@SuppressWarnings("unchecked")
final class ServiceInvokeeEntry {

	ServiceInvokeeEntry(Class<?> targetClass, int hash,
			ServiceInvokeeEntry next, ServiceInvokeeBase first) {
		this.targetClass = targetClass;
		this.hash = hash;
		this.next = next;
		this.first = first;
	}

	final void put(ServiceInvokeeBase invokee) {
		invokee.next = this.first;
		this.first = invokee;
	}

	/**
	 * 调用器的传出参数的类型
	 */
	final Class<?> targetClass;
	final int hash;
	ServiceInvokeeEntry next;
	ServiceInvokeeBase first;
}
