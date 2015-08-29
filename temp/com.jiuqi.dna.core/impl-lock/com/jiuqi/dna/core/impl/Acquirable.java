package com.jiuqi.dna.core.impl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 待请求资源
 * 
 * @author gaojingxin
 * 
 */
class Acquirable {

	final static class IDGenerator extends AtomicLong {

		private static final long serialVersionUID = -8411706326830518965L;

		IDGenerator(final long markBits, final int markBitCount) {
			this.markBits = markBits;
			this.counterMask = -1L >>> markBitCount;
		}

		final long next() {
			long counter;
			for (;;) {
				counter = this.incrementAndGet();
				if (counter <= this.counterMask) {
					break;
				}
				this.compareAndSet(counter, 0);
			}
			return this.markBits | counter;
		}

		private final long markBits;

		private final long counterMask;

	}

	/**
	 * 表示版本的时钟
	 */
	volatile int clock;
	/**
	 * 请求者列队的队尾
	 */
	volatile Acquirer acquirer;

	/**
	 * 返回是否需要在集群中同步
	 */
	boolean needSynchronizeInCluster() {
		return false;
	}

	/**
	 * 返回是否允许在当前线程上修改资源，即是否拥有U锁或X锁
	 */
	final boolean isModifiableOnCurrentThread() {
		return this.isModifiableOnTransaction(null);
	}

	/**
	 * 返回指定事务是否可以修改资源，即是否拥有U锁或X锁
	 */
	synchronized final boolean isModifiableOnTransaction(Transaction transaction) {
		Acquirer a = this.acquirer;
		if (a == null) {
			return false;
		}
		a = a.next;
		if (a == null) {
			return false;
		}
		switch ((int) a.state & IAcquirerState.MASK_LOCK) {
		case IAcquirerState.LOCK_LU:
		case IAcquirerState.LOCK_LX:
		case IAcquirerState.LOCK_GU:
		case IAcquirerState.LOCK_GX:
		case IAcquirerState.LOCK_RU:
		case IAcquirerState.LOCK_RX:
			if (transaction == null) {
				return a.getOwner().isOwnerThread(Thread.currentThread());
			}
			return a.getOwner() == transaction;
		default:
			return false;
		}
	}

	void onTransactionCommit(final Transaction transaction) {
	}

	void onTransactionRollback(final Transaction transaction) {
	}
}