package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.spi.work.Work;

/**
 * �����ж�
 * 
 * @author gaojingxin
 * 
 */
final class ConcurrentWorkQueue {

	/**
	 * �жӶ�β���жӳɻ�״
	 */
	private Work tail;

	/**
	 * ��С
	 */
	private int size;

	/**
	 * �����ж�
	 * 
	 * <p>
	 * ���ø÷���ǰ��Ҫͬ��������
	 */
	final boolean put(Work work) {
		if (work.putToCuncerringQ(this.tail)) {
			this.tail = work;
			this.size++;
			return true;
		}
		return false;
	}

	final boolean isEmpty() {
		return this.tail == null;
	}

	/**
	 * ���ж���ȡ��
	 * 
	 * <p>
	 * ���ø÷���ǰ��Ҫͬ��������
	 */
	final Work poll() {
		Work tail = this.tail;
		if (tail != null) {
			Work work = tail.removeNext();
			if (work == tail) {
				this.tail = null;
			}
			this.size--;
			return work;
		}
		return null;
	}
}