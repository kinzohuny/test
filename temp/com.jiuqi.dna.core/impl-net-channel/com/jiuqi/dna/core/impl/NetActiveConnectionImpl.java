package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URL;

import sun.net.www.http.HttpClient;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingThread;

class NetActiveConnectionImpl extends NetConnectionImpl {
	/**
	 * ���ӳ������Ե�������
	 */
	private static final int CONNECT_MAX_RETRY = 3;
	/**
	 * ����ʱ����
	 */
	private static final int SLEEP_BEFORE_RECONNECT = 2000;

	private final NetNodeToken remoteNodeInfo;
	private final String remoteHost;
	private HttpClient httpIn;
	private HttpClient httpOut;
	/**
	 * �����߳�
	 */
	private Thread sendThread;
	/**
	 * �����߳�
	 */
	private Thread receiveThread;
	/**
	 * ��ʶ�����߳��Ƿ���������׼������
	 */
	private boolean sendThreadStart;
	/**
	 * ��ʶ�����߳��Ƿ���������׼������
	 */
	private boolean receiveThreadStart;
	/**
	 * ���ӳ��Դ��������ӳɹ�һ�ξ͹���
	 */
	private int tryCount;

	NetActiveConnectionImpl(NetChannelImpl owner, NetNodeToken remoteNodeInfo) {
		super(owner);
		if (remoteNodeInfo == null) {
			throw new NullArgumentException("remoteNodeInfo");
		}
		this.remoteNodeInfo = remoteNodeInfo;
		URL url = remoteNodeInfo.ncl;
		InetAddress addr;
		try {
			addr = InetAddress.getByName(url.getHost());
		} catch (Throwable e) {
			addr = null;
		}
		if (addr != null) {
			this.remoteHost = addr.toString();
		} else {
			this.remoteHost = url.getHost();
		}
	}

	@Override
	public void connect() {
		try {
			synchronized (this.lock) {
				switch (this.state) {
				case NONE:
				case DISPOSED:
					// û�����ӣ�����������
					this.setStateNoSync(State.CONNECTING);
					break;
				case CONNECTING:
				case STABLE:
				case RUNNING:
				case CLOSING:
					return;
				}
			}
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
		this.owner.getApplication().overlappedManager.startWork(new Work() {
			@Override
			protected void doWork(WorkingThread thread) throws Throwable {
				NetActiveConnectionImpl.this.internalConnect();
			}
		});
	}

	@Override
	public void connectAndWait() {
		try {
			synchronized (this.lock) {
				for (;;) {
					switch (this.state) {
					case NONE:
						// û�����ӣ�����������
						this.setStateNoSync(State.CONNECTING);
						break;
					case CONNECTING:
					case CLOSING:
						this.lock.wait();
						continue;
					case STABLE:
					case RUNNING:
						return;
					case DISPOSED:
						throw new IOException("����������");
					}
					break;
				}
			}
			this.internalConnect();
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final void internalConnect() throws Throwable {
		try {
			if (NetDebug.TRACE_CONNECT()) {
				this.owner.trace("����ͨ�ţ���������Զ������");
			}
			NetNodeToken remoteNodeInfo = this.remoteNodeInfo;
			URL url = remoteNodeInfo.ncl;
			Proxy proxy = remoteNodeInfo.proxy;
			ApplicationImpl app = this.owner.getApplication();
			int localNodeIndex = app.netNodeManager.thisCluster.thisClusterNodeIndex;
			DnaHttpClient httpIn = new DnaHttpClient(url, proxy, localNodeIndex, app.localNodeID);
			InputStream input = httpIn.openInput(remoteNodeInfo);
			DnaHttpClient httpOut = new DnaHttpClient(url, proxy, localNodeIndex, app.localNodeID);
			OutputStream output = httpOut.openOutput(remoteNodeInfo);
			try {
				this.initializeInputStream(input);
			} catch (Throwable e) {
				if (input != null) {
					input.close();
				}
				httpIn.closeServer();
				throw e;
			}
			try {
				this.initializeOutputStream(output);
			} catch (Throwable e) {
				if (output != null) {
					output.close();
				}
				httpOut.closeServer();
				throw e;
			}
			synchronized (this.lock) {
				switch (this.state) {
				case CONNECTING:
					// �������ӳɹ�״̬
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
				this.httpIn = httpIn;
				this.httpOut = httpOut;
				this.input = input;
				this.output = output;
				// �������Ӵ���
				this.tryCount = 0;
			}
			this.owner.onConnectionStable(this);
			if (NetDebug.TRACE_CONNECT()) {
				this.owner.trace("����ͨ�ţ���Զ��������������");
			}
		} catch (Throwable e) {
			if (NetDebug.TRACE_EXCEPTION()) {
				e.printStackTrace();
			}
			int g;
			RETRY: {
				synchronized (this.lock) {
					if (++this.tryCount >= CONNECT_MAX_RETRY) {
						// ����ʧ��
						this.setDisposedNoSync();
						break RETRY;
					}
					g = this.generation;
				}
				this.exitAndRetry(g);
				return;
			}
			try {
				this.owner.onConnectionDisposed(this);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
			if (NetDebug.TRACE_EXCEPTION()) {
				throw new IOException("����ʧ�ܣ��������Ӵ�������������");
			}
		}
	}

	@Override
	public void start() {
		synchronized (this.lock) {
			switch (this.state) {
			case STABLE:
				this.setStateNoSync(State.RUNNING);
				this.receiveThreadStart = this.sendThreadStart = true;
				break;
			default:
				return;
			}
		}
		// ���������߳�
		this.owner.getApplication().overlappedManager.startWork(new SendFragmentWork());
		// ���������߳�
		this.owner.getApplication().overlappedManager.startWork(new ReceiveFragmentWork());
	}

	private final class SendFragmentWork extends Work {
		@Override
		protected final void doWork(WorkingThread thread) throws Throwable {
			try {
				OutputStream out;
				String originalName = thread.getName();
				thread.setName("dna-NetChannel - send via httpclient - " + NetActiveConnectionImpl.this.remoteHost);
				try {
					if (NetDebug.TRACE_THREAD()) {
						NetActiveConnectionImpl.this.owner.trace("����ͨ�ţ��������ݷ����߳�");
					}
					synchronized (NetActiveConnectionImpl.this.lock) {
						switch (NetActiveConnectionImpl.this.state) {
						case RUNNING:
							break;
						case CLOSING:
							return;
						default:
							throw new IllegalStateException("����״̬��" + NetActiveConnectionImpl.this.state);
						}
						out = NetActiveConnectionImpl.this.output;
						NetActiveConnectionImpl.this.sendThread = thread;
					}
					NetActiveConnectionImpl.this.owner.sendThreadRun(out);
				} finally {
					thread.setName(originalName);
					if (NetDebug.TRACE_THREAD()) {
						NetActiveConnectionImpl.this.owner.trace("����ͨ�ţ���ֹ���ݷ����߳�");
					}
				}
			} finally {
				int g;
				synchronized (NetActiveConnectionImpl.this.lock) {
					NetActiveConnectionImpl.this.sendThread = null;
					NetActiveConnectionImpl.this.sendThreadStart = false;
					NetActiveConnectionImpl.this.lock.notifyAll();
					if (NetActiveConnectionImpl.this.state == State.CLOSING) {
						return;
					}
					g = NetActiveConnectionImpl.this.generation;
				}
				NetActiveConnectionImpl.this.exitAndRetry(g);
			}
		}
	}

	private final class ReceiveFragmentWork extends Work {
		@Override
		protected final void doWork(WorkingThread thread) throws Throwable {
			try {
				InputStream in;
				String originalName = thread.getName();
				thread.setName("dna-NetChannel - receive via httpclient - " + NetActiveConnectionImpl.this.remoteHost);
				try {
					if (NetDebug.TRACE_THREAD()) {
						NetActiveConnectionImpl.this.owner.trace("����ͨ�ţ��������ݽ����߳�");
					}
					synchronized (NetActiveConnectionImpl.this.lock) {
						switch (NetActiveConnectionImpl.this.state) {
						case RUNNING:
							break;
						case CLOSING:
							return;
						default:
							throw new IllegalStateException("����״̬��" + NetActiveConnectionImpl.this.state);
						}
						in = NetActiveConnectionImpl.this.input;
						NetActiveConnectionImpl.this.receiveThread = thread;
					}
					NetActiveConnectionImpl.this.owner.receiveThreadRun(in);
				} finally {
					thread.setName(originalName);
					if (NetDebug.TRACE_THREAD()) {
						NetActiveConnectionImpl.this.owner.trace("����ͨ�ţ���ֹ���ݽ����߳�");
					}
				}
			} finally {
				int g;
				synchronized (NetActiveConnectionImpl.this.lock) {
					NetActiveConnectionImpl.this.receiveThread = null;
					NetActiveConnectionImpl.this.receiveThreadStart = false;
					NetActiveConnectionImpl.this.lock.notifyAll();
					if (NetActiveConnectionImpl.this.state == State.CLOSING) {
						return;
					}
					g = NetActiveConnectionImpl.this.generation;
				}
				NetActiveConnectionImpl.this.exitAndRetry(g);
			}
		}
	}

	@Override
	protected void exitAndRetry(int generation) throws Throwable {
		RECONNECT: {
			synchronized (this.lock) {
				if (this.generation != generation) {
					return;
				}
				switch (this.state) {
				case NONE:
					// ��������
					break;
				case CLOSING:
					// �����˳�
				case DISPOSED:
					// ����������
					return;
				default:
					// CONNECTING
					// STABLE
					// RUNNING
					this.setStateNoSync(State.CLOSING);
					this.generation++;
					if (this.sendThread != null) {
						this.sendThread.interrupt();
					}
					if (this.receiveThread != null) {
						this.receiveThread.interrupt();
					}
					// ����״̬
					if (this.input != null) {
						this.input.close();
						this.input = null;
					}
					if (this.output != null) {
						this.output.close();
						this.output = null;
					}
					if (this.httpIn != null) {
						this.httpIn.closeServer();
						this.httpIn = null;
					}
					if (this.httpOut != null) {
						this.httpOut.closeServer();
						this.httpOut = null;
					}
					// �ȴ��߳��˳�
					while (this.sendThreadStart || this.receiveThreadStart) {
						try {
							this.lock.wait();
						} catch (InterruptedException e) {
						}
						if (this.sendThread != null && !this.sendThread.isInterrupted()) {
							this.sendThread.interrupt();
						}
						if (this.receiveThread != null && !this.receiveThread.isInterrupted()) {
							this.receiveThread.interrupt();
						}
					}
					if (NetDebug.TRACE_CONNECT()) {
						this.owner.trace("����ͨ�ţ���Զ�������Ͽ�����");
					}
					switch (this.actionOnBreak) {
					case RETRY:
						this.setStateNoSync(State.NONE);
						break;
					case CLOSE:
					case DISPOSE:
						// ����״̬
						this.actionOnBreak = BreakAction.RETRY;
						// ��������
						this.setDisposedNoSync();
						break RECONNECT;
					}
					break;
				}
			}
			Thread.sleep(SLEEP_BEFORE_RECONNECT);
			this.connect();
			return;
		}
		this.owner.onConnectionDisposed(this);
	}
}
