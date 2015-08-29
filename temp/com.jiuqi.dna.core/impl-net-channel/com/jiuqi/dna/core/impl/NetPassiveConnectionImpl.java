package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingThread;

class NetPassiveConnectionImpl extends NetConnectionImpl {
	/**
	 * 发送线程
	 */
	private Thread sendThread;
	/**
	 * 接收线程
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
						// 已连接
						break;
					case NONE:
					case CONNECTING:
					case CLOSING:
						// 等待连接完成
						if (left < 0) {
							throw new IOException("等待连接超时");
						}
						this.lock.wait(left);
						left -= System.currentTimeMillis() - start;
						continue;
					case DISPOSED:
						throw new IOException("连接已销毁");
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
			this.owner.trace("网络通信：接受来自远程主机的连接请求");
		}
		synchronized (this.lock) {
			for (;;) {
				switch (this.state) {
				case NONE:
				case DISPOSED:
					// 没有连接，建立新连接
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
								// 超时
								if (NetDebug.TRACE_CONNECT()) {
									this.owner.trace("网络通信：连接超时");
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
							throw new IllegalStateException("错误状态：" + this.state);
						}
						break;
					}
					this.output = out;
				}
				if (NetDebug.TRACE_THREAD()) {
					this.owner.trace("网络通信：启动数据发送线程");
				}
				this.owner.sendThreadRun(out);
			} catch (Throwable e) {
			} finally {
				curThread.setName(originalName);
				if (NetDebug.TRACE_THREAD()) {
					this.owner.trace("网络通信：终止数据发送线程");
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
					// 等待INPUT连接超时
				case STABLE:
				case RUNNING:
					g = this.generation;
					break;
				default:
					// NONE
					// DISPOSED
					throw new IllegalStateException("错误状态：" + this.state);
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
				// 接受连接
				this.receiveThread = Thread.currentThread();
				break;
			case CLOSING:
				// 退出
				if (NetDebug.TRACE_CONNECT()) {
					this.owner.trace("网络通信：拒绝来自远程主机的连接请求");
				}
				return;
			default:
				// NONE
				// STABLE
				// RUNNING
				// DISPOSED
				throw new IllegalStateException("错误状态：" + this.state);
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
						throw new IllegalStateException("错误状态：" + this.state);
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
							throw new IllegalStateException("错误状态：" + this.state);
						}
						break;
					}
				}
				if (NetDebug.TRACE_THREAD()) {
					this.owner.trace("网络通信：启动数据接收线程");
				}
				if (NetDebug.TRACE_CONNECT()) {
					this.owner.trace("网络通信：与远程主机建立连接");
				}
				this.owner.receiveThreadRun(in);
			} finally {
				curThread.setName(originalName);
				if (NetDebug.TRACE_THREAD()) {
					this.owner.trace("网络通信：终止数据接收线程");
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
					throw new IllegalStateException("错误状态：" + this.state);
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
					// 正在关闭连接
				case DISPOSED:
					// 已销毁
					return;
				case NONE:
					// 异常断开，等待新连接
					if (NetDebug.TRACE_INTERNAL()) {
						this.owner.trace("网络通信：等待接受来自远程主机的连接请求");
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
					// 超时
					if (NetDebug.TRACE_INTERNAL()) {
						this.owner.trace("网络通信：等待超时");
					}
					this.setDisposedNoSync();
					break;
				default:
					// CONNECTING
					// STABLE
					// RUNNING
					this.setStateNoSync(State.CLOSING);
					// 通知线程退出
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
					// 等待线程退出
					while (this.sendThread != null || this.receiveThread != null) {
						this.lock.wait();
					}
					switch (this.actionOnBreak) {
					case RETRY:
						this.setStateNoSync(State.NONE);
						continue;
					case CLOSE:
					case DISPOSE:
						// 重置状态
						this.actionOnBreak = BreakAction.RETRY;
						// 销毁连接
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
