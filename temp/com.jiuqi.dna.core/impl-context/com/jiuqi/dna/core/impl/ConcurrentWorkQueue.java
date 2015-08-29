package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.spi.work.Work;

/**
 * 并发列队
 * 
 * @author gaojingxin
 * 
 */
final class ConcurrentWorkQueue {

	/**
	 * 列队队尾，列队成环状
	 */
	private Work tail;

	/**
	 * 大小
	 */
	private int size;

	/**
	 * 放入列队
	 * 
	 * <p>
	 * 调用该方法前需要同步本对象。
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
	 * 从列队中取出
	 * 
	 * <p>
	 * 调用该方法前需要同步本对象。
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