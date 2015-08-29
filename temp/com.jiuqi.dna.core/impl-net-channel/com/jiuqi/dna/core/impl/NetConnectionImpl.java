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
	 * 连接的状态
	 * 
	 * @author niuhaifeng
	 * 
	 */
	public enum State {
		/**
		 * 未连接
		 */
		NONE,
		/**
		 * 正在连接
		 */
		CONNECTING,
		/**
		 * 连接成功
		 */
		STABLE,
		/**
		 * 发送和接收线程开始运行
		 */
		RUNNING,
		/**
		 * 连接正在关闭
		 */
		CLOSING,
		/**
		 * 已销毁，不能再建立连接
		 */
		DISPOSED
	}

	/**
	 * 连接断开时的处理方式
	 * 
	 * @author niuhaifeng
	 * 
	 */
	protected enum BreakAction {
		/**
		 * 网络发生错误，断开时尝试重新建立连接
		 */
		RETRY,
		/**
		 * 网络空闲超时，断开时不重连
		 */
		CLOSE,
		/**
		 * 销毁连接，断开时不重连，并且连接对象不会再使用
		 */
		DISPOSE
	}

	/**
	 * 连接超时
	 */
	static final int CONNECTION_TIMEOUT = 3000;

	final NetChannelImpl owner;
	private short remoteSerializeVersion;
	private long remoteAppInstanceVersion;
	private GUID remoteAppID;
	/**
	 * 连接的代
	 */
	protected int generation;
	/**
	 * 连接锁
	 */
	protected final Object lock = new Object();
	/**
	 * 连接状态
	 */
	protected State state = State.NONE;
	/**
	 * 指示连接断开时的处理方式
	 */
	protected BreakAction actionOnBreak = BreakAction.RETRY;

	/**
	 * 输入流
	 */
	protected InputStream input;
	/**
	 * 输出流
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
	 * 设置是否允许关闭连接
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
	 * 获取连接的状态
	 * 
	 * @return
	 */
	public final State getState() {
		synchronized (this.lock) {
			return this.state;
		}
	}

	/**
	 * 通知建立连接，非阻塞
	 */
	public abstract void connect();

	/**
	 * 启动发送和接收线程
	 */
	public abstract void start();

	/**
	 * 连接并等待连接完成
	 */
	protected abstract void connectAndWait();

	/**
	 * 退出发送线程并尝试重新连接
	 */
	protected abstract void exitAndRetry(int generation) throws Throwable;

	/**
	 * 通知连接断开，非阻塞
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
					// 正在关闭连接
					this.actionOnBreak = BreakAction.DISPOSE;
				case DISPOSED:
					// 已销毁
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
		// 接收数据
		int offset = f.getAvailableOffset();
		this.readBytes(f.getBytes(), offset, 4, in);
		f.setPosition(offset);
		this.readBytes(f.getBytes(), offset + 4, f.readInt(), in);
		// 读取网络通信组件版本
		int ver = f.readInt();
		if (ver != this.owner.getComponentVersion()) {
			this.disconnect();
			throw new IOException(String.format("网络通信：网络协议不兼容，本地网络协议版本[%d]远程网络协议版本[%d]，节点ID[%s]索引号[%d]", this.owner.getComponentVersion(), ver, this.owner.remoteNodeInfo.appID, this.owner.remoteNodeInfo.index));
		}
		// 读取timestamp
		long remoteTimestamp = f.readLong();
		if (NetDebug.TRACE_INTERNAL()) {
			this.owner.trace("网络通信：获取远程时间戳[" + remoteTimestamp + "]");
		}
		if (this.remoteAppInstanceVersion == 0L) {
			this.remoteAppInstanceVersion = remoteTimestamp;
		} else if (this.remoteAppInstanceVersion != remoteTimestamp) {
			this.disconnect();
			throw new ConnectException("网络通信：远程服务已重置");
		}
		// 读取远程appID
		this.remoteAppID = GUID.valueOf(f.getBytes(), f.getPosition());
		if (NetDebug.TRACE_INTERNAL()) {
			this.owner.trace("网络通信：获取远程appID[" + this.remoteAppID + "]");
		}
		f.skip(16);
		// 读取远程clusterNodeIndex
		int remoteNodeIndex = f.readInt();
		if (remoteNodeIndex != this.owner.remoteNodeInfo.index) {
			this.disconnect();
			throw new IOException(String.format("网络通信：远程节点的索引号发生变化，请重新连接，节点ID[%s]旧索引号[%d]新索引号[%d]", this.owner.remoteNodeInfo.appID, this.owner.remoteNodeInfo.index, remoteNodeIndex));
		}
		// 读取serializeVersion
		this.remoteSerializeVersion = f.readShort();
		if (NetDebug.TRACE_INTERNAL()) {
			this.owner.trace("网络通信：获取远程序列化器版本[" + this.remoteSerializeVersion + "]");
		}
		// 读取senderGeneration
		int g = f.readInt();
		if (NetDebug.TRACE_INTERNAL()) {
			this.owner.trace("网络通信：获取远程信道版本[" + g + "]");
		}
		if (this.generation < g) {
			this.generation = g;
		}
	}

	protected final void initializeOutputStream(OutputStream out)
			throws IOException {
		DataFragment f = this.owner.allocDataFragment(48);
		// 写入网络通信组件版本
		f.writeInt(this.owner.getComponentVersion());
		// 写入timestamp
		f.writeLong(this.owner.getChannelVersion());
		// 写入appID
		NetSelfClusterImpl c = this.owner.getApplication().netNodeManager.thisCluster;
		c.clusterID.toBytes(f.getBytes(), f.getPosition());
		f.skip(16);
		// 写入clusterNodeIndex
		f.writeInt(c.thisClusterNodeIndex);
		// 写入serializeVersion
		f.writeShort(NSerializer.getHighestSerializeVersion());
		// 写入generation
		f.writeInt(this.generation);
		// 发送数据
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
				throw new IOException(String.format("网络通信：远程主机关闭了连接，节点ID[%s]索引号[%d]", this.owner.remoteNodeInfo.appID, this.owner.remoteNodeInfo.index));
			}
			offset += i;
			size -= i;
		} while (size > 0);
	}
}
