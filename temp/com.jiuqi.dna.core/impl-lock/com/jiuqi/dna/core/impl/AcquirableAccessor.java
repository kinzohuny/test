package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.exception.TimeoutException;

/**
 * 资源请求者
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
				throw new UnsupportedOperationException("无法将[共享]锁转换成[可升级]锁");
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
				throw new UnsupportedOperationException("无法将[共享]锁转换成[独占]锁");
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
			// 当前版本比请求版本旧
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
			// 更新版本
			a.clock = clock;
			return true;
		}
		return clock == a.clock;
	}

	final boolean remoteUpgradable(Acquirer a, int clock) {
		synchronized (a.acquirable) {
			if (!checkVer(a.acquirable, clock, true)) {
				System.err.println(String.format("集群：事务[%x]收到无效的加[可升级]锁请求，版本[%d]目标[%s]", a.getOwner().id, clock, a.acquirable));
				return false;
			}
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				this.remoteUpgradableAsync(a);
				break;
			case LOCK_LS:
				throw new UnsupportedOperationException("无法将[共享]锁转换成[可升级]锁");
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
				System.err.println(String.format("集群：事务[%x]收到无效的加[独占]锁请求，版本[%d]目标[%s]", a.getOwner().id, clock, a.acquirable));
				return false;
			}
			switch ((int) a.state & MASK_LOCK) {
			case LOCK_N:
				this.remoteExclusiveAsync(a);
				break;
			case LOCK_LS:
				throw new UnsupportedOperationException("无法将[共享]锁转换成[独占]锁");
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
				// 增加计数
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
	 * 解除本地锁
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
				// 增加计数
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
				// 当前版本比请求版本旧，更新版本
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
				// 增加计数
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
	 * 获取本地共享锁
	 */
	private final void sharedAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		final Acquirer tail = acquirable.acquirer;
		if (tail == null) {// 没有其他请求者
			insertBefore(acquirable, a, null);
			a.next = a;
			a.state = LOCK_LS;
			return;
		}
		Acquirer acq = tail.next;
		// 查找兼容的锁
		while (acq != null) {
			if ((acq.state & LOCK_MASK_TYPE) != LOCK_TYPE_X) {// 兼容
				insertBefore(acquirable, a, acq);
				a.state = acq == tail.next ? LOCK_LS : LOCK_LSW;
				return;
			}
			if (acq == tail) {
				break;
			}
			acq = acq.next;
		}
		// 没有兼容锁，插入队尾
		a.next = tail.next;
		insertBefore(acquirable, a, null);
		a.state = LOCK_LSW;
	}

	/**
	 * 获取可升级锁
	 */
	private final void upgradableAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		final Acquirer tail = acquirable.acquirer;
		// 插入锁对象，放在队尾
		if (tail == null // 没有其他请求者
				|| (tail.state & MASK_LOCK) == LOCK_LS) {// 兼容
			// 加锁成功
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
		} else { // 有请求者，等待或者不兼容
			a.next = tail.next;
			insertBefore(acquirable, a, null);
			a.state = acquirable.needSynchronizeInCluster() ? LOCK_GUW : LOCK_LUW;
		}
	}

	/**
	 * 获取独占锁
	 */
	private final void exclusiveAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		// 插入锁对象，放在队尾
		final Acquirer tail = acquirable.acquirer;
		if (tail == null) {// 没有请求者，加锁成功
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
		} else {// 有其他请求者，等待
			a.next = tail.next;
			insertBefore(acquirable, a, null);
			a.state = acquirable.needSynchronizeInCluster() ? LOCK_GXW : LOCK_LXW;
		}
	}

	private final static boolean compareTo(Acquirer a, Acquirer b) {
		int aI = a.getOwner().getNodeIndex();
		int bI = b.getOwner().getNodeIndex();
		if (bI == aI) {
			// 锁已经存在
			throw illegalState((byte) -1);
		}
		int factor = a.acquirable.clock % NetClusterImpl.MAX_NODE_COUNT;
		return (aI > bI) ? (factor >= aI || factor < bI) : (factor >= aI && factor < bI);
	}

	/**
	 * 竞争远程U锁占位符
	 */
	private final void remoteUpgradableAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		final Acquirer tail = acquirable.acquirer;
		if (tail == null) {
			// 没有其他请求者，加锁成功
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
			// 锁被其他请求者持有
			final Acquirer acq = tail.next;
			switch ((int) acq.state & MASK_LOCK) {
			case LOCK_LS:// 兼容
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
				// 不兼容，竞争
				if (compareTo(a, acq)) {
					// 竞争获胜，优先加锁
					insertBefore(acquirable, a, acq);
					tail.next = a;
					a.state = LOCK_RU;
					// 全局锁竞争失败，退回到等待状态
					acq.state = (acq.state & MASK_LOCK) ^ LOCK_STATE_REQUEST ^ LOCK_STATE_WAITING;
					acquirable.notifyAll();
				} else {
					a.state = LOCK_N;
				}
				break;
			case LOCK_RU:
			case LOCK_RX:
			case LOCK_RXW:
				// 不兼容，竞争
				if (compareTo(a, acq)) {
					// 竞争获胜
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
				// 加锁失败，这种情况是由于集群中两个不同节点同时发出加锁请求，竞争成功的一方先收到加锁成功回复再收到加锁请求
				a.state = LOCK_N;
				break;
			default:
				throw illegalState(acq.state);
			}
		}
		a.postAcquireResult();
	}

	/**
	 * 竞争远程X锁占位符
	 */
	private final void remoteExclusiveAsync(Acquirer a) {
		final Acquirable acquirable = a.acquirable;
		final Acquirer tail = acquirable.acquirer;
		if (tail == null) {
			// 没有其他请求者，加锁成功
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
			// 锁被其他请求者持有
			switch ((int) hold.state & MASK_LOCK) {
			case LOCK_LS:
				// 没有全局锁请求，加R锁并等待
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
				// 请求者正在加全局锁，竞争
				if (compareTo(a, hold)) {
					// 竞争获胜
					insertBefore(acquirable, a, hold);
					tail.next = a;
					// 请求者退回等待状态
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
				// 不兼容，竞争
				if (compareTo(a, hold)) {
					// 竞争获胜，优先加锁
					insertBefore(acquirable, a, hold);
					tail.next = a;
					a.state = LOCK_RX;
					// 全局锁竞争失败，退回到等待状态
					hold.state = LOCK_GXW;
					acquirable.notifyAll();
				} else {
					a.state = LOCK_N;
				}
				break;
			case LOCK_RU:
			case LOCK_RX:
			case LOCK_RXW:
				// 竞争
				if (compareTo(a, hold)) {
					// 竞争获胜
					replace(acquirable, hold, a);
					if (hold != tail) {
						tail.next = a;
					} else {
						a.next = a;
					}
					// 请求者解除锁请求
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
				// 加锁失败，这种情况是由于集群中两个不同节点同时发出加锁请求，竞争成功的一方先收到加锁成功回复再收到加锁请求
				a.state = LOCK_N;
				break;
			default:
				throw illegalState(hold.state);
			}
		}
		a.postAcquireResult();
	}

	/**
	 * 将U锁升级为X锁
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
	 * 将控制权移交给指定的请求者
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

	// TODO 可以去掉acquirable参数，已经包含在toInsert里了。
	/**
	 * 将指定请求者置于参考指定请求者的prev位置
	 * 
	 * <p>
	 * 若参考者为空，则置于acquirable的队尾。
	 * 
	 * @param acquirable
	 * @param toInsert
	 *            指定者
	 * @param before
	 *            参考者
	 */
	private final static void insertBefore(Acquirable acquirable,
			Acquirer toInsert, Acquirer before) {
		if (before == null) {
			// 插入到队尾
			final Acquirer tail = acquirable.acquirer;
			toInsert.prev = tail;
			if (tail != null) {
				tail.next = toInsert;
			}
			acquirable.acquirer = toInsert;
		} else {
			// 有参考的节点
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
			// 检查clock
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
					// 不会收到此类消息
					// 退回等待状态
					a.state = (a.state & MASK_LOCK) ^ LOCK_STATE_REQUEST ^ LOCK_STATE_WAITING;
					acquirable.notifyAll();
				}
				break;
			}
		}
	}

	/**
	 * 抛出状态异常
	 */
	private final static IllegalStateException illegalState(long state) {
		return new IllegalStateException("无效锁状态:" + getStateText(state));
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