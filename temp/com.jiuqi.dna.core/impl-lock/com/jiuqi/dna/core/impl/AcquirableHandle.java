package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.GUID;

final class AcquirableHandle extends Acquirer implements ITransactionMessage {

	final Transaction transaction;
	/**
	 * 请求加锁的操作
	 */
	final AcquireFor operation;

	AcquirableHandle(final Acquirable acquirable,
			final Transaction transaction, final AcquireFor operation) {
		super(acquirable);
		this.transaction = transaction;
		this.operation = operation;
	}

	@Override
	final Transaction getOwner() {
		return this.transaction;
	}

	// ====================== 发起者 ========================

	@Override
	protected void broadcastAcquire() {
		final Acquirable a = this.acquirable;
		if (a instanceof CacheHolder<?, ?, ?>) {
			switch (((CacheHolder<?, ?, ?>) a).getHolderState()) {
			case CacheHolder.STATE_CREATED:
			case CacheHolder.STATE_DISPOSED:
				this.state |= MASK_NODE;
				return;
			}
		} else if (a instanceof CacheGroup<?, ?, ?>) {
			switch (((CacheGroup<?, ?, ?>) a).getState()) {
			case CacheGroup.STATE_CREATED:
			case CacheGroup.STATE_DISPOSED:
				this.state |= MASK_NODE;
				return;
			}
		} else if (!(a instanceof Cache)) {
			throw new IllegalStateException("集群：不支持对类型为[" + a.getClass() + "]的资源[" + a + "]加分布式锁");
		}
		final int clock = a.clock;
		final byte operation;
		switch (this.operation) {
		case ADD:
			operation = PARAM_METHOD_ADD;
			break;
		case COMMIT:
			operation = PARAM_METHOD_COMMIT;
			break;
		case MODIFY:
			operation = PARAM_METHOD_MODIFY;
			break;
		case MODIFY_ITEMS:
			operation = PARAM_METHOD_MODIFY_ITEMS;
			break;
		case READ:
			operation = PARAM_METHOD_READ;
			break;
		case REMOVE:
			operation = PARAM_METHOD_REMOVE;
			break;
		default:
			throw new IllegalStateException();
		}
		this.broadcast(new LockPackage() {
			@Override
			void build(DataOutputFragment fragment, NetNodeImpl attachment)
					throws Throwable {
				fragment.writeByte(MSG_ACQUIRE);
				this.writeResource(fragment, a, clock);
				fragment.writeByte(operation);
			}

			@Override
			public void onFragmentOutError(NetNodeImpl attachment) {
				AcquirableHandle.this.transaction.accessor().onAcquireResult(AcquirableHandle.this, clock, true, attachment.channel.getRemoteNodeIndex());
			}
		});
	}

	@Override
	protected void broadcastUpgrade() {
		final int clock = this.acquirable.clock;
		this.broadcast(new LockPackage() {
			@Override
			void build(DataOutputFragment fragment, NetNodeImpl attachment)
					throws Throwable {
				fragment.writeByte(MSG_UPGRADE);
				this.writeResource(fragment, AcquirableHandle.this.acquirable, clock);
			}

			@Override
			public void onFragmentOutError(NetNodeImpl attachment) {
				AcquirableHandle.this.transaction.accessor().onAcquireResult(AcquirableHandle.this, clock, true, attachment.channel.getRemoteNodeIndex());
			}
		});
	}

	@Override
	protected void broadcastRelease() {
		final int clock = this.acquirable.clock;
		this.broadcast(new LockPackage() {
			@Override
			void build(DataOutputFragment fragment, NetNodeImpl attachment)
					throws Throwable {
				fragment.writeByte(MSG_RELEASE);
				this.writeResource(fragment, AcquirableHandle.this.acquirable, clock);
			}
		});
	}

	// ======================= 参与者 ======================

	@Override
	protected void postAcquireResult() {
		final int clock = this.acquirable.clock;
		final byte result;
		switch ((int) this.state & MASK_LOCK) {
		case LOCK_N:
			result = RESULT_FAIL;
			// 不发送失败消息
			return;
		case LOCK_RU:
		case LOCK_RX:
			result = RESULT_SUCCEED;
			break;
		default:
			throw new IllegalStateException();
		}
		NetNodeImpl node = this.transaction.ownerNode;
		node.channel.startSendingPackage(new LockPackage() {
			@Override
			void build(DataOutputFragment fragment, NetNodeImpl attachment)
					throws Throwable {
				fragment.writeByte(MSG_ACQUIRE_RESULT);
				this.writeResource(fragment, AcquirableHandle.this.acquirable, clock);
				fragment.writeByte(result);
			}
		}, node);
	}

	private final void broadcast(DataFragmentBuilder<NetNodeImpl> builder) {
		if (!this.transaction.site.shared) {
			this.state |= (-1 << LOCK_LEN);
			return;
		}
		final NetClusterImpl c = this.transaction.site.getNetCluster();
		synchronized (c) {
			int mask = -1;
			for (NetNodeImpl node = c.getFirstNetNode(); node != null; node = node.getNextNodeInCluster()) {
				if (node.getState() == NetNodeImpl.STATE_READY) {
					mask ^= 1 << node.channel.getRemoteNodeIndex();
				}
			}
			this.state = (this.state & MASK_LOCK) | (mask << LOCK_LEN);
			for (NetNodeImpl node = c.getFirstNetNode(); node != null; node = node.getNextNodeInCluster()) {
				if (node.getState() == NetNodeImpl.STATE_READY) {
					node.channel.startSendingPackage(builder, node);
				}
			}
		}
	}

	private abstract class LockPackage implements
			DataFragmentBuilder<NetNodeImpl> {

		public boolean buildFragment(DataOutputFragment fragment,
				NetNodeImpl attachment) throws Throwable {
			fragment.writeByte(INetPackageSign.TRANSACTION_PACKAGE);
			// siteID
			GUID siteID = AcquirableHandle.this.transaction.site.id;
			fragment.writeLong(siteID.getMostSigBits());
			fragment.writeLong(siteID.getLeastSigBits());
			// transactionID
			fragment.writeInt(AcquirableHandle.this.transaction.id);
			this.build(fragment, attachment);
			return true;
		}

		abstract void build(DataOutputFragment fragment, NetNodeImpl attachment)
				throws Throwable;

		public void onFragmentOutError(NetNodeImpl attachment) {
		}

		public void onFragmentOutFinished(NetNodeImpl attachment) {
		}

		public boolean tryResetPackage(NetNodeImpl attachment) {
			return true;
		}

		final void writeResource(DataOutputFragment fragment, Acquirable res,
				int clock) {
			if (res instanceof CacheHolder<?, ?, ?>) {
				fragment.writeByte(PARAM_TYPE_ITEM);
				fragment.writeLong(((CacheHolder<?, ?, ?>) res).longIdentifier);
			} else if (res instanceof CacheGroup<?, ?, ?>) {
				fragment.writeByte(PARAM_TYPE_GROUP);
				fragment.writeLong(((CacheGroup<?, ?, ?>) res).longIdentifier);
			} else if (res instanceof Cache) {
				fragment.writeByte(PARAM_TYPE_CACHE);
			}
			fragment.writeInt(clock);
		}
	}

	@Override
	public final String toString() {
		String queue;
		final Acquirer tail = this.acquirable.acquirer;
		if (tail == null) {
			queue = "";
		} else {
			final Acquirer next = tail.next;
			queue = (next == null ? "(empty)" : next.self()) + "<-" + tail.self();
			Acquirer prev = tail.prev;
			while (prev != null) {
				queue = queue + "->" + prev.self();
				prev = prev.prev;
			}
		}
		return String.format("Acquirer%s\nAcquirable[%s]\nQueue:%s", this.self(), this.acquirable.toString(), queue);
	}
}