package com.jiuqi.dna.core.impl;

import java.util.LinkedList;

import com.jiuqi.dna.core.impl.DataPackageReceiver.NetPackageReceivingStarter;

final class NetPackageReceivingEntry<TAttachment> implements
		AsyncIOStub<TAttachment>, NetPackageReceivingStarter {
	/**
	 * 等待片段送达
	 */
	static final byte STATE_WAITING = 0;
	/**
	 * 片段已送达，排队处理中
	 */
	static final byte STATE_QUEUING = 1;
	/**
	 * 正在处理
	 */
	static final byte STATE_RESOLVING = 2;
	/**
	 * 处理完成
	 */
	static final byte STATE_COMPLETE = 3;
	/**
	 * 挂起处理过程
	 */
	static final byte STATE_SUSPEND = 4;
	/**
	 * 中断接收
	 */
	static final byte STATE_BREAK = 5;

	final NetChannelImpl channel;
	final int packageID;

	private DataFragmentResolver<? super TAttachment> resolver;
	private TAttachment attachment;
	private final Object lock = new Object();
	/**
	 * 解析过程的状态
	 */
	private byte state;
	/**
	 * 待还原的片断
	 */
	private final LinkedList<DataFragment> waitingResolveFragments = new LinkedList<DataFragment>();

	/**
	 * 接收者代
	 */
	int receiverGeneration;
	/**
	 * 指示是否接收到全部片段
	 */
	private boolean receivingComplete;

	NetPackageReceivingEntry(NetChannelImpl channel, int packageID) {
		this.channel = channel;
		this.packageID = packageID;
	}

	public void cancel() {
		this.channel.breakReceive(this.packageID);
		this.channel.postBreakSendPackageCtrl(this.packageID);
	}

	public void suspend() {
		if (NetDebug.TRACE_THREAD()) {
			this.channel.trace("网络通信：挂起正在接收的数据包[" + this.packageID + "]");
		}
		synchronized (this.lock) {
			switch (this.state) {
			case STATE_RESOLVING:
			case STATE_BREAK:
				break;
			default:
				throw new IllegalStateException("无法suspend任务包，状态" + this.state);
			}
			if (NetDebug.TRACE_INTERNAL()) {
				this.channel.trace("网络通信：正在接收的数据包[" + this.packageID + "]状态转换为[挂起]");
			}
			this.state = STATE_SUSPEND;
		}
	}

	public void resume() {
		if (NetDebug.TRACE_THREAD()) {
			this.channel.trace("网络通信：恢复正在接收的数据包[" + this.packageID + "]");
		}
		synchronized (this.lock) {
			if (this.state != STATE_SUSPEND) {
				throw new IllegalStateException("无法resume任务包，状态" + this.state);
			}
			if (this.waitingResolveFragments.isEmpty()) {
				if (NetDebug.TRACE_INTERNAL()) {
					this.channel.trace("网络通信：正在接收的数据包[" + this.packageID + "]状态转换为[等待]");
				}
				this.state = STATE_WAITING;
				return;
			} else {
				if (NetDebug.TRACE_INTERNAL()) {
					this.channel.trace("网络通信：正在接收的数据包[" + this.packageID + "]状态转换为[排队]");
				}
				this.state = STATE_QUEUING;
			}
		}
		try {
			this.channel.offerFragmentResolve(this);
		} catch (InterruptedException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final TAttachment getAttachment() {
		return this.attachment;
	}

	@SuppressWarnings("unchecked")
	public final <TAttachment2> AsyncIOStub<TAttachment2> startReceivingPackage(
			DataFragmentResolver<? super TAttachment2> resolver,
			TAttachment2 attachment) {
		this.resolver = (DataFragmentResolver<? super TAttachment>) resolver;
		this.attachment = (TAttachment) attachment;
		return (AsyncIOStub<TAttachment2>) this;
	}

	final boolean resolverValid() {
		return this.resolver != null;
	}

	final boolean isReceivingComplete() {
		synchronized (this.lock) {
			return this.receivingComplete;
		}
	}

	final void resolveDataFragment() throws Throwable {
		try {
			DataFragment fragment;
			DEQUEUE: {
				synchronized (this.lock) {
					switch (this.state) {
					case STATE_QUEUING:
						// 开始解析片段
						if (NetDebug.TRACE_INTERNAL()) {
							this.channel.trace("网络通信：正在接收的数据包[" + this.packageID + "]状态转换为[解析]");
						}
						this.state = STATE_RESOLVING;
						fragment = this.waitingResolveFragments.peek();
						break DEQUEUE;
					case STATE_BREAK:
						// 接收到了break receive通知
						break;
					default:
						return;
					}
				}
				// 中断解析过程
				this.resolver.onFragmentInFailed(this.attachment);
				return;
			}
			// 解析片段
			boolean done = this.resolver.resolveFragment(fragment, this.attachment);
			RELEASE: {
				QUEUE: {
					synchronized (this.lock) {
						switch (this.state) {
						case STATE_SUSPEND: // suspend
						case STATE_QUEUING: // 从SUSPEND状态resume
							if (fragment.remain() == 0) {
								this.waitingResolveFragments.removeFirst();
							}
							return;
						case STATE_BREAK: // 外部调用breakResolve
							return;
						case STATE_RESOLVING:
							this.waitingResolveFragments.removeFirst();
							if (this.waitingResolveFragments.isEmpty()) {
								if (this.receivingComplete) {
									// 解析完成
									if (!done) {
										throw new IllegalStateException("远程调用：数据丢失，无法完成解析");
									}
									if (NetDebug.TRACE_INTERNAL()) {
										this.channel.trace("网络通信：正在接收的数据包[" + this.packageID + "]状态转换为[完成]");
									}
									this.state = STATE_COMPLETE;
									break QUEUE;
								} else {
									if (done) {
										throw new IllegalStateException("远程调用：解析完成但是存在未接收的数据");
									}
									if (NetDebug.TRACE_INTERNAL()) {
										this.channel.trace("网络通信：正在接收的数据包[" + this.packageID + "]状态转换为[等待]");
									}
									this.state = STATE_WAITING;
									// do nothing
									break RELEASE;
								}
							} else {
								if (done) {
									throw new IllegalStateException("远程调用：解析完成但是存在未接收的数据");
								}
								// 继续排队
								if (NetDebug.TRACE_INTERNAL()) {
									this.channel.trace("网络通信：正在接收的数据包[" + this.packageID + "]状态转换为[排队]");
								}
								this.state = STATE_QUEUING;
							}
							break;
						default:
							throw new IllegalStateException("错误的状态:" + this.state);
						}
					}
					// 重新放入接收队列
					this.channel.offerFragmentResolve(this);
					break RELEASE;
				}
				this.channel.packageResolved(this.packageID);
			}
			this.channel.releaseDataFragment(fragment);
		} catch (Throwable e) {
			this.channel.breakReceive(this.packageID);
			this.channel.postBreakSendPackageCtrl(this.packageID);
			throw e;
		}
	}

	final void queueToResolve(DataFragment toResolveFragment,
			boolean isLastResolveFragment) throws InterruptedException {
		synchronized (this.lock) {
			this.waitingResolveFragments.offer(toResolveFragment);
			this.receivingComplete = isLastResolveFragment;
			switch (this.state) {
			case STATE_WAITING:
				if (NetDebug.TRACE_INTERNAL()) {
					this.channel.trace("网络通信：正在接收的数据包[" + this.packageID + "]状态转换为[排队]");
				}
				this.state = STATE_QUEUING;
				break;
			case STATE_QUEUING:
			case STATE_RESOLVING:
			case STATE_SUSPEND:
				return;
			default:
				throw new IllegalStateException("错误的状态：" + this.state);
			}
		}
		// 启动解析线程
		this.channel.offerFragmentResolve(this);
	}

	/**
	 * 终止解析
	 * 
	 * @throws Throwable
	 */
	final void breakResolve() {
		if (NetDebug.TRACE_FAULT()) {
			this.channel.trace("网络通信：中断接收数据包[" + this.packageID + "]");
		}
		synchronized (this.lock) {
			switch (this.state) {
			case STATE_BREAK:
			case STATE_COMPLETE:
				return;
			default:
				this.state = STATE_BREAK;
				if (NetDebug.TRACE_INTERNAL()) {
					this.channel.trace("网络通信：正在接收的数据包[" + this.packageID + "]状态转换为[中断]");
				}
				// 删除待解析的片段
				for (DataFragment fragment : this.waitingResolveFragments) {
					this.channel.releaseDataFragment(fragment);
				}
				this.waitingResolveFragments.clear();
				break;
			}
		}
		// 通知上层解析失败
		try {
			this.resolver.onFragmentInFailed(this.attachment);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}