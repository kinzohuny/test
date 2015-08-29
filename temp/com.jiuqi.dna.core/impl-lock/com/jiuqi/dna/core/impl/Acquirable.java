package com.jiuqi.dna.core.impl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * ��������Դ
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
	 * ��ʾ�汾��ʱ��
	 */
	volatile int clock;
	/**
	 * �������жӵĶ�β
	 */
	volatile Acquirer acquirer;

	/**
	 * �����Ƿ���Ҫ�ڼ�Ⱥ��ͬ��
	 */
	boolean needSynchronizeInCluster() {
		return false;
	}

	/**
	 * �����Ƿ������ڵ�ǰ�߳����޸���Դ�����Ƿ�ӵ��U����X��
	 */
	final boolean isModifiableOnCurrentThread() {
		return this.isModifiableOnTransaction(null);
	}

	/**
	 * ����ָ�������Ƿ�����޸���Դ�����Ƿ�ӵ��U����X��
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