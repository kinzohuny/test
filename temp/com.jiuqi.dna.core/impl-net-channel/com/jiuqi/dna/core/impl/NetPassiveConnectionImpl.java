package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingThread;

class NetPassiveConnectionImpl extends NetConnectionImpl {
	/**
	 * �����߳�
	 */
	private Thread sendThread;
	/**
	 * �����߳�
	 */
	private Thread receiveThread;

	private final String remoteInfo;

	public NetPassiveConnectionImpl(NetChannelImpl owner, String remoteInfo) {
		super(owner);
		this.remoteInfo = remoteInfo;
	}

	@Override
	public void connect() {
		this.owner.getApplication().overlappedManager.startWork(new Work() {
			@Override
			protected void doWork(WorkingThread thread) throws Throwable {
			}
		});
	}

	@Override
	public void connectAndWait() {
		try {
			synchronized (this.lock) {
				long left = CONNECTION_TIMEOUT;
				long start = System.currentTimeMillis();
				for (;;) {
					switch (this.state) {
					case STABLE:
					case RUNNING:
						// ������
						break;
					case NONE:
					case CONNECTING:
					case CLOSING:
						// �ȴ��������
						if (left < 0) {
							throw new IOException("�ȴ����ӳ�ʱ");
						}
						this.lock.wait(left);
						left -= System.currentTimeMillis() - start;
						continue;
					case DISPOSED:
						throw new IOException("����������");
					}
					break;
				}
			}
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	final void servletOutputThreadEntry(OutputStream out) throws Throwable {
		if (NetDebug.TRACE_CONNECT()) {
			this.owner.trace("����ͨ�ţ���������Զ����������������");
		}
		synchronized (this.lock) {
			for (;;) {
				switch (this.state) {
				case NONE:
				case DISPOSED:
					// û�����ӣ�����������
					this.setStateNoSync(State.CONNECTING);
					this.sendThread = Thread.currentThread();
					break;
				case CLOSING:
					this.lock.wait();
					continue;
				case CONNECTING:
				case STABLE:
				case RUNNING:
					this.reset(this.generation);
					for (;;) {
						switch (this.state) {
						case CONNECTING:
						case STABLE:
						case RUNNING:
							this.lock.wait();
							continue;
						}
						break;
					}
					continue;
				}
				break;
			}
		}
		try {
			Thread curThread = Thread.currentThread();
			String originalName = curThread.getName();
			curThread.setName("dna-NetChannel - send via servlet - " + this.remoteInfo);
			try {
				this.initializeOutputStream(out);
				synchronized (this.lock) {
					long start = System.currentTimeMillis();
					long left = CONNECTION_TIMEOUT;
					for (;;) {
						switch (this.state) {
						case CONNECTING:
							if (left < 0) {
								// ��ʱ
								if (NetDebug.TRACE_CONNECT()) {
									this.owner.trace("����ͨ�ţ����ӳ�ʱ");
								}
								return;
							}
							this.lock.wait(left);
							left = CONNECTION_TIMEOUT + start - System.currentTimeMillis();
							continue;
						case STABLE:
							this.lock.wait();
							continue;
						case RUNNING:
							break;
						case CLOSING:
							return;
						default:
							// NONE
							// DISPOSED
							throw new IllegalStateException("����״̬��" + this.state);
						}
						break;
					}
					this.output = out;
				}
				if (NetDebug.TRACE_THREAD()) {
					this.owner.trace("����ͨ�ţ��������ݷ����߳�");
				}
				this.owner.sendThreadRun(out);
			} catch (Throwable e) {
			} finally {
				curThread.setName(originalName);
				if (NetDebug.TRACE_THREAD()) {
					this.owner.trace("����ͨ�ţ���ֹ���ݷ����߳�");
				}
			}
		} finally {
			final int g;
			synchronized (this.lock) {
				this.sendThread = null;
				this.lock.notifyAll();
				switch (this.state) {
				case CLOSING:
					return;
				case CONNECTING:
					// �ȴ�INPUT���ӳ�ʱ
				case STABLE:
				case RUNNING:
					g = this.generation;
					break;
				default:
					// NONE
					// DISPOSED
					throw new IllegalStateException("����״̬��" + this.state);
				}
			}
			this.reset(g);
		}
	}

	final void servletInputThreadEntry(InputStream in) throws Throwable {
		synchronized (this.lock) {
			switch (this.state) {
			case CONNECTING:
				if (this.sendThread == null) {
					throw new IllegalStateException();
				}
				// ��������
				this.receiveThread = Thread.currentThread();
				break;
			case CLOSING:
				// �˳�
				if (NetDebug.TRACE_CONNECT()) {
					this.owner.trace("����ͨ�ţ��ܾ�����Զ����������������");
				}
				return;
			default:
				// NONE
				// STABLE
				// RUNNING
				// DISPOSED
				throw new IllegalStateException("����״̬��" + this.state);
			}
		}
		try {
			Thread curThread = Thread.currentThread();
			String originalName = curThread.getName();
			curThread.setName("dna-NetChannel - receive via servlet - " + this.remoteInfo);
			try {
				this.initializeInputStream(in);
				synchronized (this.lock) {
					switch (this.state) {
					case CONNECTING:
						this.setStateNoSync(State.STABLE);
						break;
					case CLOSING:
						return;
					default:
						// NONE
						// STABLE
						// RUNNING
						// DISPOSED
						throw new IllegalStateException("����״̬��" + this.state);
					}
					this.input = in;
				}
				this.owner.onConnectionStable(this);
				synchronized (this.lock) {
					for (;;) {
						switch (this.state) {
						case STABLE:
							this.lock.wait();
							continue;
						case RUNNING:
							break;
						case CLOSING:
							return;
						default:
							// NONE
							// CONNECTING
							// DISPOSED
							throw new IllegalStateException("����״̬��" + this.state);
						}
						break;
					}
				}
				if (NetDebug.TRACE_THREAD()) {
					this.owner.trace("����ͨ�ţ��������ݽ����߳�");
				}
				if (NetDebug.TRACE_CONNECT()) {
					this.owner.trace("����ͨ�ţ���Զ��������������");
				}
				this.owner.receiveThreadRun(in);
			} finally {
				curThread.setName(originalName);
				if (NetDebug.TRACE_THREAD()) {
					this.owner.trace("����ͨ�ţ���ֹ���ݽ����߳�");
				}
			}
		} finally {
			int g;
			synchronized (this.lock) {
				this.receiveThread = null;
				this.lock.notifyAll();
				switch (this.state) {
				case CLOSING:
					return;
				case STABLE:
				case RUNNING:
					g = this.generation;
					break;
				default:
					// NONE
					// CONNECTING
					// DISPOSED
					throw new IllegalStateException("����״̬��" + this.state);
				}
			}
			this.reset(g);
		}
	}

	private void reset(final int generation) {
		this.owner.getApplication().overlappedManager.startWork(new Work() {
			@Override
			protected void doWork(WorkingThread thread) throws Throwable {
				NetPassiveConnectionImpl.this.exitAndRetry(generation);
			}
		});
	}

	@Override
	public void start() {
		synchronized (this.lock) {
			switch (this.state) {
			case STABLE:
				this.setStateNoSync(State.RUNNING);
			default:
				return;
			}
		}
	}

	@Override
	protected void exitAndRetry(int generation) throws Throwable {
		synchronized (this.lock) {
			if (this.generation != generation) {
				return;
			}
			for (;;) {
				switch (this.state) {
				case CLOSING:
					// ���ڹر�����
				case DISPOSED:
					// ������
					return;
				case NONE:
					// �쳣�Ͽ����ȴ�������
					if (NetDebug.TRACE_INTERNAL()) {
						this.owner.trace("����ͨ�ţ��ȴ���������Զ����������������");
					}
					long start = System.currentTimeMillis();
					long left = CONNECTION_TIMEOUT;
					while (left > 0) {
						this.lock.wait(left);
						if (this.state != State.NONE) {
							return;
						}
						left = CONNECTION_TIMEOUT + start - System.currentTimeMillis();
					}
					// ��ʱ
					if (NetDebug.TRACE_INTERNAL()) {
						this.owner.trace("����ͨ�ţ��ȴ���ʱ");
					}
					this.setDisposedNoSync();
					break;
				default:
					// CONNECTING
					// STABLE
					// RUNNING
					this.setStateNoSync(State.CLOSING);
					// ֪ͨ�߳��˳�
					this.generation++;
					if (this.sendThread != null) {
						this.sendThread.interrupt();
					}
					if (this.receiveThread != null) {
						this.receiveThread.interrupt();
					}
					if (this.output != null) {
						try {
							this.output.close();
						} catch (Throwable e) {
						}
					}
					if (this.input != null) {
						try {
							this.input.close();
						} catch (Throwable e) {
						}
					}
					// �ȴ��߳��˳�
					while (this.sendThread != null || this.receiveThread != null) {
						this.lock.wait();
					}
					switch (this.actionOnBreak) {
					case RETRY:
						this.setStateNoSync(State.NONE);
						continue;
					case CLOSE:
					case DISPOSE:
						// ����״̬
						this.actionOnBreak = BreakAction.RETRY;
						// ��������
						this.setDisposedNoSync();
						break;
					}
					break;
				}
				break;
			}
		}
		this.owner.onConnectionDisposed(this);
	}
}
