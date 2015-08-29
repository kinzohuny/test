package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.exception.TimeoutException;

/**
 * ��Դ������
 * 
 * @author gaojingxin
 * 
 */
final class AcquirableAccessor implements IAcquirerState {

	final static int CLOCK_MAX = Integer.MAX_VALUE;

	private final static int CLOCK_OLDER_LIMIT = CLOCK_MAX >> 1;

	final void shared(Acquirer a) {
		synchronized (a.acquirable) {
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				this.sharedAsync(a);
				try {
					this.waitFor(a, 0);
				} catch (InterruptedException e) {
					this.dropInvalidAcquirer(a);
					throw Utils.tryThrowException(e);
				}
				break;
			case LOCK_LS:
			case LOCK_LU:
			case LOCK_LX:
			case LOCK_GU:
			case LOCK_GX:
				break;
			default:
				throw illegalState(a.state);
			}
		}
	}

	final void upgradable(Acquirer a) {
		synchronized (a.acquirable) {
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				this.upgradableAsync(a);
				try {
					this.waitFor(a, 0);
				} catch (InterruptedException e) {
					this.dropInvalidAcquirer(a);
					throw Utils.tryThrowException(e);
				}
				break;
			case LOCK_LS:
				throw new UnsupportedOperationException("�޷���[����]��ת����[������]��");
			case LOCK_LU:
			case LOCK_LX:
			case LOCK_GU:
			case LOCK_GX:
				break;
			default:
				throw illegalState(a.state);
			}
		}
	}

	final void exclusive(Acquirer a) {
		synchronized (a.acquirable) {
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				this.exclusiveAsync(a);
				break;
			case LOCK_LS:
				throw new UnsupportedOperationException("�޷���[����]��ת����[��ռ]��");
			case LOCK_LU:
			case LOCK_GU:
			case LOCK_RU:
				this.upgradeAsync(a);
				break;
			case LOCK_LX:
			case LOCK_GX:
				return;
			default:
				throw illegalState(a.state);
			}
			try {
				this.waitFor(a, 0);
			} catch (InterruptedException e) {
				this.dropInvalidAcquirer(a);
				throw Utils.tryThrowException(e);
			}
		}
	}

	final void exclusiveOnNew(Acquirer a) {
		synchronized (a.acquirable) {
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				Acquirable acquirable = a.acquirable;
				acquirable.acquirer = a;
				a.next = a;
				a.state = acquirable.needSynchronizeInCluster() ? LOCK_GX : LOCK_LX;
				break;
			default:
				throw illegalState(a.state);
			}
		}
	}

	private final static boolean verOlderThan(int a, int b) {
		int f = a - b;
		return f > 0 && f < CLOCK_OLDER_LIMIT || f < CLOCK_OLDER_LIMIT - CLOCK_MAX;
	}

	private final static boolean checkVer(Acquirable a, int clock,
			boolean release) {
		if (clock < 0) {
			return true;
		}
		if (verOlderThan(clock, a.clock)) {
			// ��ǰ�汾������汾��
			if (release && a.acquirer != null) {
				Acquirer hold = a.acquirer.next;
				if (hold == null) {
					throw new IllegalStateException();
				}
				switch ((int) hold.state & MASK_LOCK) {
				case LOCK_RU:
				case LOCK_RX:
					if (hold == a.acquirer) {
						remove(a, hold);
						if (a.acquirer != null) {
							a.acquirer.next = a.acquirer;
						}
					} else {
						Acquirer prev = hold.prev;
						remove(a, hold);
						a.acquirer.next = prev;
					}
					hold.state = LOCK_N;
					break;
				}
			}
			// ���°汾
			a.clock = clock;
			return true;
		}
		return clock == a.clock;
	}

	final boolean remoteUpgradable(Acquirer a, int clock) {
		synchronized (a.acquirable) {
			if (!checkVer(a.acquirable, clock, true)) {
				System.err.println(String.format("��Ⱥ������[%x]�յ���Ч�ļ�[������]�����󣬰汾[%d]Ŀ��[%s]", a.getOwner().id, clock, a.acquirable));
				return false;
			}
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				this.remoteUpgradableAsync(a);
				break;
			case LOCK_LS:
				throw new UnsupportedOperationException("�޷���[����]��ת����[������]��");
			case LOCK_RU:
			case LOCK_RX:
				break;
			default:
				throw illegalState(a.state);
			}
		}
		return true;
	}

	final boolean remoteExclusive(Acquirer a, int clock) {
		synchronized (a.acquirable) {
			if (!checkVer(a.acquirable, clock, true)) {
				System.err.println(String.format("��Ⱥ������[%x]�յ���Ч�ļ�[��ռ]�����󣬰汾[%d]Ŀ��[%s]", a.getOwner().id, clock, a.acquirable));
				return false;
			}
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				this.remoteExclusiveAsync(a);
				break;
			case LOCK_LS:
				throw new UnsupportedOperationException("�޷���[����]��ת����[��ռ]��");
			case LOCK_RU:
				this.upgradeAsync(a);
				break;
			case LOCK_RXW:
			case LOCK_RX:
				break;
			default:
				throw illegalState(a.state);
			}
		}
		return true;
	}

	final void remoteExclusiveOnNew(Acquirer a) {
		synchronized (a.acquirable) {
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				Acquirable acquirable = a.acquirable;
				acquirable.acquirer = a;
				a.next = a;
				a.state = LOCK_RX;
				break;
			default:
				throw illegalState(a.state);
			}
		}
	}

	final boolean remoteRelease(Acquirer a, int clock) {
		if (a == null) {
			throw new NullArgumentException("a");
		}
		final Acquirable acquirable = a.acquirable;
		synchronized (acquirable) {
			if (!checkVer(acquirable, clock, true)) {
				return false;
			}
			Acquirer next = a.next;
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				break;
			case LOCK_RU:
			case LOCK_RX:
				remove(acquirable, a);
				a.state = LOCK_N;
				// ���Ӽ���
				clockInc(acquirable);
				if (next != a) {
					this.doAcquire(acquirable, next);
				}
				break;
			default:
				throw illegalState(a.state);
			}
		}
		return true;
	}

	/**
	 * ���������
	 */
	final void release(Acquirer a) {
		if (a == null) {
			throw new NullArgumentException("a");
		}
		final Acquirable acquirable = a.acquirable;
		synchronized (acquirable) {
			Acquirer next = a.next;
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				return;
			case LOCK_LS:
			case LOCK_LU:
			case LOCK_LX:
				remove(acquirable, a);
				break;
			case LOCK_GU:
			case LOCK_GX:
				remove(acquirable, a);
				// ���Ӽ���
				clockInc(acquirable);
				break;
			default:
				throw illegalState(a.state);
			}
			a.state = LOCK_N;
			if (next != a) {
				this.doAcquire(acquirable, next);
			}
		}
	}

	final void updateVer(Acquirable a, int clock) {
		if (clock < 0) {
			return;
		}
		synchronized (a) {
			if (verOlderThan(clock, a.clock)) {
				// ��ǰ�汾������汾�ɣ����°汾
				a.clock = clock;
			}
		}
	}

	private final void dropInvalidAcquirer(Acquirer a) {
		try {
			final Acquirable acquirable = a.acquirable;
			final Acquirer tail = acquirable.acquirer;
			Acquirer hold = tail.next;
			if (a == hold) {
				hold = a.prev;
			}
			final Acquirer next = a.next;
			remove(acquirable, a);
			if (acquirable.acquirer != null) {
				acquirable.acquirer.next = hold;
			}
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
			case LOCK_LUW:
			case LOCK_LXW:
			case LOCK_LSW:
			case LOCK_GUW:
			case LOCK_GXW:
				return;
			case LOCK_LS:
			case LOCK_LU:
			case LOCK_LX:
				break;
			case LOCK_GU:
			case LOCK_GX:
			case LOCK_GUR:
			case LOCK_GXR:
				a.broadcastRelease();
				// ���Ӽ���
				clockInc(acquirable);
				break;
			default:
				throw illegalState(a.state);
			}
			if (hold == null && next != a) {
				this.doAcquire(acquirable, next);
			}
		} finally {
			a.state = LOCK_N;
		}
	}

	/**
	 * ��ȡ���ع�����
	 */
	private final void sharedAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		final Acquirer tail = acquirable.acquirer;
		if (tail == null) {// û������������
			insertBefore(acquirable, a, null);
			a.next = a;
			a.state = LOCK_LS;
			return;
		}
		Acquirer acq = tail.next;
		// ���Ҽ��ݵ���
		while (acq != null) {
			if ((acq.state & LOCK_MASK_TYPE) != LOCK_TYPE_X) {// ����
				insertBefore(acquirable, a, acq);
				a.state = acq == tail.next ? LOCK_LS : LOCK_LSW;
				return;
			}
			if (acq == tail) {
				break;
			}
			acq = acq.next;
		}
		// û�м������������β
		a.next = tail.next;
		insertBefore(acquirable, a, null);
		a.state = LOCK_LSW;
	}

	/**
	 * ��ȡ��������
	 */
	private final void upgradableAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		final Acquirer tail = acquirable.acquirer;
		// ���������󣬷��ڶ�β
		if (tail == null // û������������
				|| (tail.state & MASK_LOCK) == LOCK_LS) {// ����
			// �����ɹ�
			insertBefore(acquirable, a, null);
			a.next = a;
			if (acquirable.needSynchronizeInCluster()) {
				a.state = LOCK_GUR;
				a.broadcastAcquire();
				if ((a.state & MASK_NODE) == MASK_NODE) {
					a.state = LOCK_GU;
				}
			} else {
				a.state = LOCK_LU;
			}
		} else { // �������ߣ��ȴ����߲�����
			a.next = tail.next;
			insertBefore(acquirable, a, null);
			a.state = acquirable.needSynchronizeInCluster() ? LOCK_GUW : LOCK_LUW;
		}
	}

	/**
	 * ��ȡ��ռ��
	 */
	private final void exclusiveAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		// ���������󣬷��ڶ�β
		final Acquirer tail = acquirable.acquirer;
		if (tail == null) {// û�������ߣ������ɹ�
			insertBefore(acquirable, a, null);
			a.next = a;
			if (acquirable.needSynchronizeInCluster()) {
				a.state = LOCK_GXR;
				a.broadcastAcquire();
				if ((a.state & MASK_NODE) == MASK_NODE) {
					a.state = LOCK_GX;
				}
			} else {
				a.state = LOCK_LX;
			}
		} else {// �����������ߣ��ȴ�
			a.next = tail.next;
			insertBefore(acquirable, a, null);
			a.state = acquirable.needSynchronizeInCluster() ? LOCK_GXW : LOCK_LXW;
		}
	}

	private final static boolean compareTo(Acquirer a, Acquirer b) {
		int aI = a.getOwner().getNodeIndex();
		int bI = b.getOwner().getNodeIndex();
		if (bI == aI) {
			// ���Ѿ�����
			throw illegalState((byte) -1);
		}
		int factor = a.acquirable.clock % NetClusterImpl.MAX_NODE_COUNT;
		return (aI > bI) ? (factor >= aI || factor < bI) : (factor >= aI && factor < bI);
	}

	/**
	 * ����Զ��U��ռλ��
	 */
	private final void remoteUpgradableAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		final Acquirer tail = acquirable.acquirer;
		if (tail == null) {
			// û�����������ߣ������ɹ�
			insertBefore(acquirable, a, null);
			a.next = a;
			a.state = LOCK_RU;
		} else if (tail.next == null) {
			Acquirer acq = tail;
			while (acq.prev != null) {
				acq = acq.prev;
			}
			insertBefore(acquirable, a, acq);
			tail.next = a;
			a.state = LOCK_RU;
		} else {
			// �������������߳���
			final Acquirer acq = tail.next;
			switch ((int) acq.state & MASK_LOCK) {
			case LOCK_LS:// ����
				if (acq == tail) {
					insertBefore(acquirable, a, null);
					a.next = a;
				} else {
					insertBefore(acquirable, a, acq.next);
					tail.next = a;
				}
				a.state = LOCK_RU;
				break;
			case LOCK_GUR:
			case LOCK_GXR:
				// �����ݣ�����
				if (compareTo(a, acq)) {
					// ������ʤ�����ȼ���
					insertBefore(acquirable, a, acq);
					tail.next = a;
					a.state = LOCK_RU;
					// ȫ��������ʧ�ܣ��˻ص��ȴ�״̬
					acq.state = (acq.state & MASK_LOCK) ^ LOCK_STATE_REQUEST ^ LOCK_STATE_WAITING;
					acquirable.notifyAll();
				} else {
					a.state = LOCK_N;
				}
				break;
			case LOCK_RU:
			case LOCK_RX:
			case LOCK_RXW:
				// �����ݣ�����
				if (compareTo(a, acq)) {
					// ������ʤ
					replace(acquirable, acq, a);
					if (acq != tail) {
						tail.next = a;
					} else {
						a.next = a;
					}
					a.state = LOCK_RU;
					acq.state = LOCK_N;
					acquirable.notifyAll();
				} else {
					a.state = LOCK_N;
				}
				break;
			case LOCK_GU:
			case LOCK_GX:
				// ����ʧ�ܣ�������������ڼ�Ⱥ��������ͬ�ڵ�ͬʱ�����������󣬾����ɹ���һ�����յ������ɹ��ظ����յ���������
				a.state = LOCK_N;
				break;
			default:
				throw illegalState(acq.state);
			}
		}
		a.postAcquireResult();
	}

	/**
	 * ����Զ��X��ռλ��
	 */
	private final void remoteExclusiveAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		final Acquirer tail = acquirable.acquirer;
		if (tail == null) {
			// û�����������ߣ������ɹ�
			insertBefore(acquirable, a, null);
			a.next = a;
			a.state = LOCK_RX;
		} else if (tail.next == null) {
			Acquirer acq = tail;
			while (acq.prev != null) {
				acq = acq.prev;
			}
			insertBefore(acquirable, a, acq);
			tail.next = a;
			a.state = LOCK_RX;
		} else {
			final Acquirer hold = tail.next;
			// �������������߳���
			switch ((int) hold.state & MASK_LOCK) {
			case LOCK_LS:
				// û��ȫ�������󣬼�R�����ȴ�
				if (hold == tail) {
					insertBefore(acquirable, a, null);
					a.next = a;
				} else {
					insertBefore(acquirable, a, hold.next);
					tail.next = a;
				}
				a.state = LOCK_RXW;
				return;
			case LOCK_GUR:
				// ���������ڼ�ȫ����������
				if (compareTo(a, hold)) {
					// ������ʤ
					insertBefore(acquirable, a, hold);
					tail.next = a;
					// �������˻صȴ�״̬
					hold.state = LOCK_GUW;
					acquirable.notifyAll();
					if (a.prev != null) {
						a.state = LOCK_RXW;
						return;
					}
					a.state = LOCK_RX;
				} else {
					a.state = LOCK_N;
				}
				break;
			case LOCK_GXR:
				// �����ݣ�����
				if (compareTo(a, hold)) {
					// ������ʤ�����ȼ���
					insertBefore(acquirable, a, hold);
					tail.next = a;
					a.state = LOCK_RX;
					// ȫ��������ʧ�ܣ��˻ص��ȴ�״̬
					hold.state = LOCK_GXW;
					acquirable.notifyAll();
				} else {
					a.state = LOCK_N;
				}
				break;
			case LOCK_RU:
			case LOCK_RX:
			case LOCK_RXW:
				// ����
				if (compareTo(a, hold)) {
					// ������ʤ
					replace(acquirable, hold, a);
					if (hold != tail) {
						tail.next = a;
					} else {
						a.next = a;
					}
					// �����߽��������
					hold.state = LOCK_N;
					acquirable.notifyAll();
					if (a.prev != null) {
						a.state = LOCK_RXW;
						return;
					}
					a.state = LOCK_RX;
				} else {
					a.state = LOCK_N;
				}
				break;
			case LOCK_GU:
			case LOCK_GX:
				// ����ʧ�ܣ�������������ڼ�Ⱥ��������ͬ�ڵ�ͬʱ�����������󣬾����ɹ���һ�����յ������ɹ��ظ����յ���������
				a.state = LOCK_N;
				break;
			default:
				throw illegalState(hold.state);
			}
		}
		a.postAcquireResult();
	}

	/**
	 * ��U������ΪX��
	 */
	private final void upgradeAsync(Acquirer a) {
		switch ((int) a.state & MASK_LOCK) {
		case LOCK_LU:
			a.state = a.prev == null ? LOCK_LX : LOCK_LXW;
			break;
		case LOCK_GU:
			if (a.prev == null) {
				a.state = LOCK_GXR;
				a.broadcastUpgrade();
				if ((a.state & MASK_NODE) == MASK_NODE) {
					a.state = LOCK_GX;
				}
			} else {
				a.state = LOCK_GXW;
			}
			break;
		case LOCK_RU:
			if (a.prev == null) {
				a.state = LOCK_RX;
				a.postAcquireResult();
			} else {
				a.state = LOCK_RXW;
			}
			break;
		default:
			throw illegalState(a.state);
		}
	}

	/**
	 * ������Ȩ�ƽ���ָ����������
	 */
	private void doAcquire(Acquirable acquirable, Acquirer a) {
		if (a == null) {
			throw new NullArgumentException("a");
		}
		if (a.prev != null || (a.state & LOCK_MASK_STATE) != LOCK_STATE_WAITING) {
			return;
		}
		switch ((int) a.state & MASK_LOCK) {
		case LOCK_LSW:
			a.state = LOCK_LS;
			for (;;) {
				Acquirer next = a.next;
				if (next != null) {
					switch ((int) next.state & MASK_LOCK) {
					case LOCK_LSW:
						next.state = LOCK_LS;
						a = next;
						continue;
					case LOCK_LUW:
						next.state = LOCK_LU;
						a = next;
						break;
					case LOCK_GUW:
						next.state = LOCK_GUR;
						a = next;
						a.broadcastAcquire();
						if ((a.state & MASK_NODE) == MASK_NODE) {
							a.state = LOCK_GU;
						}
						break;
					}
				}
				break;
			}
			break;
		case LOCK_LUW:
			a.state = LOCK_LU;
			break;
		case LOCK_LXW:
			a.state = LOCK_LX;
			break;
		case LOCK_GUW:
		case LOCK_GXW:
			a.state ^= LOCK_STATE_WAITING ^ LOCK_STATE_REQUEST;
			a.broadcastAcquire();
			if ((a.state & MASK_NODE) == MASK_NODE) {
				a.state = (a.state & MASK_LOCK) ^ LOCK_STATE_REQUEST ^ LOCK_STATE_ACQUIRED;
			}
			break;
		case LOCK_RXW:
			a.state = LOCK_RX;
			a.postAcquireResult();
			break;
		default:
			throw illegalState(a.state);
		}
		acquirable.acquirer.next = a;
		acquirable.notifyAll();
	}

	private final boolean waitFor(Acquirer a, long timeout)
			throws TimeoutException, InterruptedException {
		for (;;) {
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				return false;
			case LOCK_LS:
			case LOCK_LU:
			case LOCK_LX:
			case LOCK_GU:
			case LOCK_GX:
			case LOCK_RU:
			case LOCK_RX:
			case LOCK_RXW:
				return true;
			}
			timeout = this.waitOnce(a.acquirable, timeout);
		}
	}

	private final static void clockInc(Acquirable a) {
		a.clock = (a.clock + 1) & CLOCK_MAX;
	}

	private final long waitOnce(Acquirable a, long timeout)
			throws TimeoutException, InterruptedException {
		if (timeout == 0) {
			a.wait();
		} else if (timeout > 0) {
			long now = System.currentTimeMillis();
			a.wait(timeout);
			timeout -= System.currentTimeMillis() - now;
			if (timeout == 0) {
				timeout = -1;
			}
		} else {
			throw new TimeoutException();
		}
		return timeout;
	}

	// TODO ����ȥ��acquirable�������Ѿ�������toInsert���ˡ�
	/**
	 * ��ָ�����������ڲο�ָ�������ߵ�prevλ��
	 * 
	 * <p>
	 * ���ο���Ϊ�գ�������acquirable�Ķ�β��
	 * 
	 * @param acquirable
	 * @param toInsert
	 *            ָ����
	 * @param before
	 *            �ο���
	 */
	private final static void insertBefore(Acquirable acquirable,
			Acquirer toInsert, Acquirer before) {
		if (before == null) {
			// ���뵽��β
			final Acquirer tail = acquirable.acquirer;
			toInsert.prev = tail;
			if (tail != null) {
				tail.next = toInsert;
			}
			acquirable.acquirer = toInsert;
		} else {
			// �вο��Ľڵ�
			final Acquirer beforePrev = before.prev;
			toInsert.prev = beforePrev;
			toInsert.next = before;
			before.prev = toInsert;
			if (beforePrev != null) {
				beforePrev.next = toInsert;
			}
		}
	}

	private final static void replace(Acquirable acquirable, Acquirer acquirer,
			Acquirer newAcquirer) {
		final Acquirer prev = acquirer.prev;
		newAcquirer.prev = prev;
		if (prev != null) {
			prev.next = newAcquirer;
		}
		if (acquirer == acquirable.acquirer) {
			newAcquirer.next = acquirer.next == acquirer ? newAcquirer : acquirer.next;
			acquirable.acquirer = newAcquirer;
		} else {
			final Acquirer next = acquirer.next;
			next.prev = newAcquirer;
			newAcquirer.next = next;
		}
		acquirer.next = acquirer.prev = null;
	}

	private final static void remove(Acquirable acquirable, Acquirer acquirer) {
		final Acquirer prev = acquirer.prev;
		if (acquirer == acquirable.acquirer) {
			acquirable.acquirer = prev;
			if (prev != null) {
				prev.next = null;
			}
		} else {
			final Acquirer next = acquirer.next;
			next.prev = prev;
			if (prev != null) {
				prev.next = next;
			}
		}
	}

	final void onAcquireResult(Acquirer a, int clock, boolean success,
			int nodeIndex) {
		Acquirable acquirable = a.acquirable;
		synchronized (acquirable) {
			// ���clock
			if (!checkVer(acquirable, clock, false)) {
				return;
			}
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_GUR:
			case LOCK_GXR:
				if (success) {
					a.state |= 1L << (nodeIndex + LOCK_LEN);
					if ((a.state & MASK_NODE) == MASK_NODE) {
						a.state = (a.state & MASK_LOCK) ^ LOCK_STATE_REQUEST ^ LOCK_STATE_ACQUIRED;
						a.acquirable.notifyAll();
					}
				} else {
					// �����յ�������Ϣ
					// �˻صȴ�״̬
					a.state = (a.state & MASK_LOCK) ^ LOCK_STATE_REQUEST ^ LOCK_STATE_WAITING;
					acquirable.notifyAll();
				}
				break;
			}
		}
	}

	/**
	 * �׳�״̬�쳣
	 */
	private final static IllegalStateException illegalState(long state) {
		return new IllegalStateException("��Ч��״̬:" + getStateText(state));
	}

	final static String getStateText(long state) {
		String stateText;
		switch ((int) state & MASK_LOCK) {
		case LOCK_N:
			stateText = "N";
			break;
		case LOCK_LSW:
			stateText = "LSW";
			break;
		case LOCK_LS:
			stateText = "LS";
			break;
		case LOCK_LXW:
			stateText = "LXW";
			break;
		case LOCK_LX:
			stateText = "LX";
			break;
		case LOCK_LUW:
			stateText = "LUW";
			break;
		case LOCK_LU:
			stateText = "LU";
			break;
		case LOCK_GUW:
			stateText = "GUW";
			break;
		case LOCK_GUR:
			stateText = "GUR";
			break;
		case LOCK_GU:
			stateText = "GU";
			break;
		case LOCK_GXW:
			stateText = "GXW";
			break;
		case LOCK_GXR:
			stateText = "GXR";
			break;
		case LOCK_GX:
			stateText = "GX";
			break;
		case LOCK_RU:
			stateText = "RU";
			break;
		case LOCK_RXW:
			stateText = "RXW";
			break;
		case LOCK_RX:
			stateText = "RX";
			break;
		default:
			stateText = "E";
			break;
		}
		return stateText;
	}
}