package com.jiuqi.dna.core.impl;

import java.util.LinkedList;

import com.jiuqi.dna.core.impl.DataPackageReceiver.NetPackageReceivingStarter;

final class NetPackageReceivingEntry<TAttachment> implements
		AsyncIOStub<TAttachment>, NetPackageReceivingStarter {
	/**
	 * �ȴ�Ƭ���ʹ�
	 */
	static final byte STATE_WAITING = 0;
	/**
	 * Ƭ�����ʹ�ŶӴ�����
	 */
	static final byte STATE_QUEUING = 1;
	/**
	 * ���ڴ���
	 */
	static final byte STATE_RESOLVING = 2;
	/**
	 * �������
	 */
	static final byte STATE_COMPLETE = 3;
	/**
	 * ���������
	 */
	static final byte STATE_SUSPEND = 4;
	/**
	 * �жϽ���
	 */
	static final byte STATE_BREAK = 5;

	final NetChannelImpl channel;
	final int packageID;

	private DataFragmentResolver<? super TAttachment> resolver;
	private TAttachment attachment;
	private final Object lock = new Object();
	/**
	 * �������̵�״̬
	 */
	private byte state;
	/**
	 * ����ԭ��Ƭ��
	 */
	private final LinkedList<DataFragment> waitingResolveFragments = new LinkedList<DataFragment>();

	/**
	 * �����ߴ�
	 */
	int receiverGeneration;
	/**
	 * ָʾ�Ƿ���յ�ȫ��Ƭ��
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
			this.channel.trace("����ͨ�ţ��������ڽ��յ����ݰ�[" + this.packageID + "]");
		}
		synchronized (this.lock) {
			switch (this.state) {
			case STATE_RESOLVING:
			case STATE_BREAK:
				break;
			default:
				throw new IllegalStateException("�޷�suspend�������״̬" + this.state);
			}
			if (NetDebug.TRACE_INTERNAL()) {
				this.channel.trace("����ͨ�ţ����ڽ��յ����ݰ�[" + this.packageID + "]״̬ת��Ϊ[����]");
			}
			this.state = STATE_SUSPEND;
		}
	}

	public void resume() {
		if (NetDebug.TRACE_THREAD()) {
			this.channel.trace("����ͨ�ţ��ָ����ڽ��յ����ݰ�[" + this.packageID + "]");
		}
		synchronized (this.lock) {
			if (this.state != STATE_SUSPEND) {
				throw new IllegalStateException("�޷�resume�������״̬" + this.state);
			}
			if (this.waitingResolveFragments.isEmpty()) {
				if (NetDebug.TRACE_INTERNAL()) {
					this.channel.trace("����ͨ�ţ����ڽ��յ����ݰ�[" + this.packageID + "]״̬ת��Ϊ[�ȴ�]");
				}
				this.state = STATE_WAITING;
				return;
			} else {
				if (NetDebug.TRACE_INTERNAL()) {
					this.channel.trace("����ͨ�ţ����ڽ��յ����ݰ�[" + this.packageID + "]״̬ת��Ϊ[�Ŷ�]");
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
						// ��ʼ����Ƭ��
						if (NetDebug.TRACE_INTERNAL()) {
							this.channel.trace("����ͨ�ţ����ڽ��յ����ݰ�[" + this.packageID + "]״̬ת��Ϊ[����]");
						}
						this.state = STATE_RESOLVING;
						fragment = this.waitingResolveFragments.peek();
						break DEQUEUE;
					case STATE_BREAK:
						// ���յ���break receive֪ͨ
						break;
					default:
						return;
					}
				}
				// �жϽ�������
				this.resolver.onFragmentInFailed(this.attachment);
				return;
			}
			// ����Ƭ��
			boolean done = this.resolver.resolveFragment(fragment, this.attachment);
			RELEASE: {
				QUEUE: {
					synchronized (this.lock) {
						switch (this.state) {
						case STATE_SUSPEND: // suspend
						case STATE_QUEUING: // ��SUSPEND״̬resume
							if (fragment.remain() == 0) {
								this.waitingResolveFragments.removeFirst();
							}
							return;
						case STATE_BREAK: // �ⲿ����breakResolve
							return;
						case STATE_RESOLVING:
							this.waitingResolveFragments.removeFirst();
							if (this.waitingResolveFragments.isEmpty()) {
								if (this.receivingComplete) {
									// �������
									if (!done) {
										throw new IllegalStateException("Զ�̵��ã����ݶ�ʧ���޷���ɽ���");
									}
									if (NetDebug.TRACE_INTERNAL()) {
										this.channel.trace("����ͨ�ţ����ڽ��յ����ݰ�[" + this.packageID + "]״̬ת��Ϊ[���]");
									}
									this.state = STATE_COMPLETE;
									break QUEUE;
								} else {
									if (done) {
										throw new IllegalStateException("Զ�̵��ã�������ɵ��Ǵ���δ���յ�����");
									}
									if (NetDebug.TRACE_INTERNAL()) {
										this.channel.trace("����ͨ�ţ����ڽ��յ����ݰ�[" + this.packageID + "]״̬ת��Ϊ[�ȴ�]");
									}
									this.state = STATE_WAITING;
									// do nothing
									break RELEASE;
								}
							} else {
								if (done) {
									throw new IllegalStateException("Զ�̵��ã�������ɵ��Ǵ���δ���յ�����");
								}
								// �����Ŷ�
								if (NetDebug.TRACE_INTERNAL()) {
									this.channel.trace("����ͨ�ţ����ڽ��յ����ݰ�[" + this.packageID + "]״̬ת��Ϊ[�Ŷ�]");
								}
								this.state = STATE_QUEUING;
							}
							break;
						default:
							throw new IllegalStateException("�����״̬:" + this.state);
						}
					}
					// ���·�����ն���
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
					this.channel.trace("����ͨ�ţ����ڽ��յ����ݰ�[" + this.packageID + "]״̬ת��Ϊ[�Ŷ�]");
				}
				this.state = STATE_QUEUING;
				break;
			case STATE_QUEUING:
			case STATE_RESOLVING:
			case STATE_SUSPEND:
				return;
			default:
				throw new IllegalStateException("�����״̬��" + this.state);
			}
		}
		// ���������߳�
		this.channel.offerFragmentResolve(this);
	}

	/**
	 * ��ֹ����
	 * 
	 * @throws Throwable
	 */
	final void breakResolve() {
		if (NetDebug.TRACE_FAULT()) {
			this.channel.trace("����ͨ�ţ��жϽ������ݰ�[" + this.packageID + "]");
		}
		synchronized (this.lock) {
			switch (this.state) {
			case STATE_BREAK:
			case STATE_COMPLETE:
				return;
			default:
				this.state = STATE_BREAK;
				if (NetDebug.TRACE_INTERNAL()) {
					this.channel.trace("����ͨ�ţ����ڽ��յ����ݰ�[" + this.packageID + "]״̬ת��Ϊ[�ж�]");
				}
				// ɾ����������Ƭ��
				for (DataFragment fragment : this.waitingResolveFragments) {
					this.channel.releaseDataFragment(fragment);
				}
				this.waitingResolveFragments.clear();
				break;
			}
		}
		// ֪ͨ�ϲ����ʧ��
		try {
			this.resolver.onFragmentInFailed(this.attachment);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}