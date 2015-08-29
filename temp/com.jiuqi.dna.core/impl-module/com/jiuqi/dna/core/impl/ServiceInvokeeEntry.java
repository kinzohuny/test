package com.jiuqi.dna.core.impl;

/**
 * ģ���������
 * 
 * <p>
 * ����targetClass��hashֵ��������
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
	 * �������Ĵ�������������
	 */
	final Class<?> targetClass;
	final int hash;
	ServiceInvokeeEntry next;
	ServiceInvokeeBase first;
}
