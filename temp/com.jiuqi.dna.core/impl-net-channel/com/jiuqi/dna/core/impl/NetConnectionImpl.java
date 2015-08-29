package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingThread;
import com.jiuqi.dna.core.type.GUID;

abstract class NetConnectionImpl {
	/**
	 * ���ӵ�״̬
	 * 
	 * @author niuhaifeng
	 * 
	 */
	public enum State {
		/**
		 * δ����
		 */
		NONE,
		/**
		 * ��������
		 */
		CONNECTING,
		/**
		 * ���ӳɹ�
		 */
		STABLE,
		/**
		 * ���ͺͽ����߳̿�ʼ����
		 */
		RUNNING,
		/**
		 * �������ڹر�
		 */
		CLOSING,
		/**
		 * �����٣������ٽ�������
		 */
		DISPOSED
	}

	/**
	 * ���ӶϿ�ʱ�Ĵ���ʽ
	 * 
	 * @author niuhaifeng
	 * 
	 */
	protected enum BreakAction {
		/**
		 * ���緢�����󣬶Ͽ�ʱ�������½�������
		 */
		RETRY,
		/**
		 * ������г�ʱ���Ͽ�ʱ������
		 */
		CLOSE,
		/**
		 * �������ӣ��Ͽ�ʱ���������������Ӷ��󲻻���ʹ��
		 */
		DISPOSE
	}

	/**
	 * ���ӳ�ʱ
	 */
	static final int CONNECTION_TIMEOUT = 3000;

	final NetChannelImpl owner;
	private short remoteSerializeVersion;
	private long remoteAppInstanceVersion;
	private GUID remoteAppID;
	/**
	 * ���ӵĴ�
	 */
	protected int generation;
	/**
	 * ������
	 */
	protected final Object lock = new Object();
	/**
	 * ����״̬
	 */
	protected State state = State.NONE;
	/**
	 * ָʾ���ӶϿ�ʱ�Ĵ���ʽ
	 */
	protected BreakAction actionOnBreak = BreakAction.RETRY;

	/**
	 * ������
	 */
	protected InputStream input;
	/**
	 * �����
	 */
	protected OutputStream output;

	NetConnectionImpl(NetChannelImpl owner) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		this.owner = owner;
	}

	public final GUID getRemoteAppID() {
		this.connectAndWait();
		return this.remoteAppID;
	}

	public final long getRemoteAppInstanceVersion() {
		this.connectAndWait();
		return this.remoteAppInstanceVersion;
	}

	public final short getRemoteSerializeVersion() {
		this.connectAndWait();
		return this.remoteSerializeVersion;
	}

	/**
	 * �����Ƿ�����ر�����
	 * 
	 * @param allow
	 */
	public final void allowClose(boolean allow) {
		synchronized (this.lock) {
			switch (this.actionOnBreak) {
			case RETRY:
			case CLOSE:
				this.actionOnBreak = allow ? BreakAction.CLOSE : BreakAction.RETRY;
			case DISPOSE:
				break;
			}
		}
	}

	/**
	 * ��ȡ���ӵ�״̬
	 * 
	 * @return
	 */
	public final State getState() {
		synchronized (this.lock) {
			return this.state;
		}
	}

	/**
	 * ֪ͨ�������ӣ�������
	 */
	public abstract void connect();

	/**
	 * �������ͺͽ����߳�
	 */
	public abstract void start();

	/**
	 * ���Ӳ��ȴ��������
	 */
	protected abstract void connectAndWait();

	/**
	 * �˳������̲߳�������������
	 */
	protected abstract void exitAndRetry(int generation) throws Throwable;

	/**
	 * ֪ͨ���ӶϿ���������
	 */
	public final void disconnect() {
		final int g;
		DISPOSE: {
			synchronized (this.lock) {
				switch (this.state) {
				case NONE:
					this.setDisposedNoSync();
					break;
				case CLOSING:
					// ���ڹر�����
					this.actionOnBreak = BreakAction.DISPOSE;
				case DISPOSED:
					// ������
					return;
				default:
					// CONNECTING
					// STABLE
					// RUNNING
					this.actionOnBreak = BreakAction.DISPOSE;
					g = this.generation;
					break DISPOSE;
				}
			}
			// NONE
			this.owner.onConnectionDisposed(this);
			return;
		}
		this.owner.getApplication().overlappedManager.startWork(new Work() {
			@Override
			protected void doWork(WorkingThread thread) throws Throwable {
				NetConnectionImpl.this.exitAndRetry(g);
			}
		});
	}

	protected final void setStateNoSync(State state) {
		this.state = state;
		this.lock.notifyAll();
		// DebugHelper.trace("state: " + state, 5);
	}

	protected final void setDisposedNoSync() {
		this.remoteAppInstanceVersion = 0;
		this.state = State.DISPOSED;
		this.lock.notifyAll();
		// DebugHelper.trace("state: DISPOSED", 5);
	}

	protected final void initializeInputStream(InputStream in)
			throws IOException {
		DataFragment f = this.owner.allocDataFragment(48);
		// ��������
		int offset = f.getAvailableOffset();
		this.readBytes(f.getBytes(), offset, 4, in);
		f.setPosition(offset);
		this.readBytes(f.getBytes(), offset + 4, f.readInt(), in);
		// ��ȡ����ͨ������汾
		int ver = f.readInt();
		if (ver != this.owner.getComponentVersion()) {
			this.disconnect();
			throw new IOException(String.format("����ͨ�ţ�����Э�鲻���ݣ���������Э��汾[%d]Զ������Э��汾[%d]���ڵ�ID[%s]������[%d]", this.owner.getComponentVersion(), ver, this.owner.remoteNodeInfo.appID, this.owner.remoteNodeInfo.index));
		}
		// ��ȡtimestamp
		long remoteTimestamp = f.readLong();
		if (NetDebug.TRACE_INTERNAL()) {
			this.owner.trace("����ͨ�ţ���ȡԶ��ʱ���[" + remoteTimestamp + "]");
		}
		if (this.remoteAppInstanceVersion == 0L) {
			this.remoteAppInstanceVersion = remoteTimestamp;
		} else if (this.remoteAppInstanceVersion != remoteTimestamp) {
			this.disconnect();
			throw new ConnectException("����ͨ�ţ�Զ�̷���������");
		}
		// ��ȡԶ��appID
		this.remoteAppID = GUID.valueOf(f.getBytes(), f.getPosition());
		if (NetDebug.TRACE_INTERNAL()) {
			this.owner.trace("����ͨ�ţ���ȡԶ��appID[" + this.remoteAppID + "]");
		}
		f.skip(16);
		// ��ȡԶ��clusterNodeIndex
		int remoteNodeIndex = f.readInt();
		if (remoteNodeIndex != this.owner.remoteNodeInfo.index) {
			this.disconnect();
			throw new IOException(String.format("����ͨ�ţ�Զ�̽ڵ�������ŷ����仯�����������ӣ��ڵ�ID[%s]��������[%d]��������[%d]", this.owner.remoteNodeInfo.appID, this.owner.remoteNodeInfo.index, remoteNodeIndex));
		}
		// ��ȡserializeVersion
		this.remoteSerializeVersion = f.readShort();
		if (NetDebug.TRACE_INTERNAL()) {
			this.owner.trace("����ͨ�ţ���ȡԶ�����л����汾[" + this.remoteSerializeVersion + "]");
		}
		// ��ȡsenderGeneration
		int g = f.readInt();
		if (NetDebug.TRACE_INTERNAL()) {
			this.owner.trace("����ͨ�ţ���ȡԶ���ŵ��汾[" + g + "]");
		}
		if (this.generation < g) {
			this.generation = g;
		}
	}

	protected final void initializeOutputStream(OutputStream out)
			throws IOException {
		DataFragment f = this.owner.allocDataFragment(48);
		// д������ͨ������汾
		f.writeInt(this.owner.getComponentVersion());
		// д��timestamp
		f.writeLong(this.owner.getChannelVersion());
		// д��appID
		NetSelfClusterImpl c = this.owner.getApplication().netNodeManager.thisCluster;
		c.clusterID.toBytes(f.getBytes(), f.getPosition());
		f.skip(16);
		// д��clusterNodeIndex
		f.writeInt(c.thisClusterNodeIndex);
		// д��serializeVersion
		f.writeShort(NSerializer.getHighestSerializeVersion());
		// д��generation
		f.writeInt(this.generation);
		// ��������
		int offset = f.getAvailableOffset();
		int size = f.getPosition() - offset;
		f.setPosition(offset);
		f.writeInt(size - 4);
		out.write(f.getBytes(), offset, size);
		out.flush();
	}

	public final void send(OutputStream out, DataFragment fragment)
			throws IOException {
		int offset = fragment.getAvailableOffset();
		int size = fragment.getPosition() - offset;
		fragment.setPosition(offset);
		fragment.writeInt(size - 4);
		out.write(fragment.getBytes(), offset, size);
		out.flush();
	}

	public final DataFragment receive(InputStream in, DataFragment fragment)
			throws Throwable {
		byte[] buff = fragment.getBytes();
		int offset = fragment.getAvailableOffset();
		fragment.setPosition(offset);
		this.readBytes(buff, offset, 4, in);
		int size = fragment.readInt();
		if (size > 0) {
			if (size > fragment.getAvailableLength() - 4) {
				fragment = this.owner.allocDataFragment(size);
				fragment.setPosition(fragment.getAvailableOffset());
				fragment.writeInt(size);
			}
			offset = fragment.getPosition();
			this.readBytes(buff, offset, size, in);
			fragment.setPosition(offset);
		}
		fragment.limit(offset + size);
		return fragment;
	}

	private final void readBytes(byte[] buff, int offset, int size,
			InputStream input) throws IOException {
		do {
			int i = input.read(buff, offset, size);
			if (i < 0) {
				throw new IOException(String.format("����ͨ�ţ�Զ�������ر������ӣ��ڵ�ID[%s]������[%d]", this.owner.remoteNodeInfo.appID, this.owner.remoteNodeInfo.index));
			}
			offset += i;
			size -= i;
		} while (size > 0);
	}
}
