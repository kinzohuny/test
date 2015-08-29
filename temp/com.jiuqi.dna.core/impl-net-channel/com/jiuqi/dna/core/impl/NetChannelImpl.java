package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingThread;
import com.jiuqi.dna.core.type.GUID;

/**
 * 网络通信通道
 * 
 * @author niuhaifeng
 * 
 */
public final class NetChannelImpl {
	/**
	 * 连接状态
	 * 
	 * @author niuhaifeng
	 * 
	 */
	private enum ConnectionState {
		/**
		 * 没有连接
		 */
		NONE,
		/**
		 * 等待连接完成
		 */
		CONNECTING,
		/**
		 * 连接完成，可以进行通信
		 */
		STABLE,
		/**
		 * 连接失败，无连接可用
		 */
		FAIL,
		/**
		 * 等待连接关闭
		 */
		CLOSE_WAIT
	}

	/**
	 * 需要确认的数据片段的构造器
	 * 
	 * @author niuhaifeng
	 * 
	 */
	private interface IAckRequiredFragmentBuilder {
		public DataFragment build();
	}

	/**
	 * 需要确认的数据片段队列元素
	 * 
	 * @author niuhaifeng
	 * 
	 */
	private static class AckRequiredFragmentStub {
		int ackID;
		final IAckRequiredFragmentBuilder builder;

		public AckRequiredFragmentStub(int ackID,
				IAckRequiredFragmentBuilder builder) {
			this.ackID = ackID;
			this.builder = builder;
		}
	}

	/**
	 * 类型掩码
	 */
	static final byte CTRL_FLAG_TYPE_MASK = (byte) 0x0f;
	/**
	 * 子类型掩码
	 */
	static final byte CTRL_FLAG_SUBTYPE_MASK = (byte) 0xf0;
	/**
	 * 控制标记：数据包片断，不需要回复
	 */
	static final byte CTRL_FLAG_PACKAGE = 1;
	/**
	 * 控制标记：数据包片断头
	 */
	static final byte CTRL_FLAG_PACKAGE_FIRST = (byte) 0x40;
	/**
	 * 控制标记：数据包片断尾
	 */
	static final byte CTRL_FLAG_PACKAGE_LAST = (byte) 0x80;
	/**
	 * 控制标记：终止接受方接受数据包，该标记由发送方发送，不需要回复
	 */
	static final byte CTRL_FLAG_BREAK_RECEIVE = 2;
	/**
	 * 控制标记：终止发送方发送数据包，该标记由接受方发送，需要回复ackID
	 */
	static final byte CTRL_FLAG_BREAK_SEND = 3;
	/**
	 * 控制标记：包接受完毕，接受方还原完毕某包后回发，需要回复ackID
	 */
	static final byte CTRL_FLAG_RESOLVED = 4;
	/**
	 * 控制标记：询问数据包接收状态，需要回复ackID和没有接收完毕的数据包的packageID
	 */
	static final byte CTRL_FLAG_PACKAGE_STATE = 5;
	/**
	 * 控制标记：断开连接，可选回复，回复消息表示继续保持连接
	 */
	static final byte CTRL_FLAG_CLOSE = 7;
	/**
	 * 控制标记：保持连接，不需要回复
	 */
	static final byte CTRL_FLAG_KEEP_ALIVE = 12;
	/**
	 * 控制标记：回复消息
	 */
	static final byte CTRL_FLAG_RESPONSE = (byte) 0x40;
	/**
	 * CLOSE消息回复：取消断开连接
	 */
	static final byte CTRL_FLAG_CLOSE_CANCEL = CTRL_FLAG_RESPONSE;
	/**
	 * 数据片的最大长度
	 */
	static final int PACKAGE_FRAGMENT_SIZE = 1024 * 32;
	/**
	 * 空闲状态超时时间
	 */
	static final long DEFAULT_IDLE_TIMEOUT = 10000L;
	/**
	 * keep-alive消息的间隔时间
	 */
	static final long KEEP_ALIVE_TIMING = 4000L;

	private final NetChannelManagerImpl owner;
	/**
	 * send锁
	 */
	final Object outLock = new Object();
	/**
	 * receive锁
	 */
	final Object inLock = new Object();
	/**
	 * ACK序号种子
	 */
	private volatile int ackSeed;
	/**
	 * 等待ack的消息队列
	 */
	private final Queue<AckRequiredFragmentStub> waitingAckFragments = new LinkedList<AckRequiredFragmentStub>();
	/**
	 * 等待构造的数据包
	 */
	private final Queue<NetPackageSendingEntry<?>> waitingBuildingPackages = new LinkedList<NetPackageSendingEntry<?>>();
	/**
	 * 发送中的数据包
	 */
	final IntKeyMap<NetPackageSendingEntry<?>> sendingPackages = new IntKeyMap<NetPackageSendingEntry<?>>();
	/**
	 * 待发送片断<br>
	 * 为了使发送连续，因此在一个片断发送时，另一些片断就预先准备好。
	 */
	protected final Queue<DataFragment> waitingSendingFragments = new LinkedList<DataFragment>();
	/**
	 * 接收的数据包
	 */
	private final IntKeyMap<NetPackageReceivingEntry<?>> receivingPackages = new IntKeyMap<NetPackageReceivingEntry<?>>();
	/**
	 * 正在构造的Fragment的个数
	 */
	protected volatile int buildingFragmentCount;
	/**
	 * 等待发送列队的最大长度
	 */
	private int waitingSendingQueueMaxSize = 5;
	/**
	 * 指示是否保持连接
	 */
	private final AtomicInteger keepAlive = new AtomicInteger();
	/**
	 * 空闲超时时间
	 */
	private long timeout = DEFAULT_IDLE_TIMEOUT;
	/**
	 * 连接状态
	 */
	private ConnectionState connectionState = ConnectionState.NONE;
	/**
	 * 连接锁
	 */
	private final Object connectionLock = new Object();
	/**
	 * 有效连接
	 */
	private NetConnectionImpl conn;
	/**
	 * 临时的主动连接
	 */
	private NetActiveConnectionImpl activeConn;
	/**
	 * 临时的被动连接
	 */
	private NetPassiveConnectionImpl passiveConn;
	/**
	 * 远程主机地址，节点ID，索引号
	 */
	final NetNodeToken remoteNodeInfo;
	private final LinkedList<NetNodeToken> allRemoteNodeInfos = new LinkedList<NetNodeToken>();

	private short remoteSerializeVersion;
	private long remoteAppInstanceVersion;
	private GUID remoteAppID;

	NetChannelImpl(NetChannelManagerImpl owner, NetNodeToken info) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		if (info == null) {
			throw new NullArgumentException("info");
		}
		this.owner = owner;
		this.remoteNodeInfo = info;
	}

	final void appendURL(NetNodeToken info) {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		synchronized (this.allRemoteNodeInfos) {
			if (!this.allRemoteNodeInfos.contains(info)) {
				this.allRemoteNodeInfos.add(info);
				if (this.hostAddr == null && info.ncl != null) {
					this.hostAddr = info.ncl;
					this.hostInfo = null;
				}
			}
		}
		if (this.hostInfo == null) {
			this.hostInfo = String.format(" - 远程主机URL[%s]索引号[%d]ID[%s]", this.hostAddr, this.remoteNodeInfo.index, this.remoteNodeInfo.appID);
		}
	}

	final void clearURLs(HashMap<NetNodeToken, NetChannelImpl> dict) {
		for (NetNodeToken info : this.allRemoteNodeInfos) {
			NetChannelImpl old = dict.remove(info);
			if (old != this) {
				dict.put(info, old);
			}
		}
	}

	public final GUID getRemoteNodeID() {
		return this.remoteNodeInfo.appID;
	}

	/**
	 * 集群ID
	 * 
	 * @return
	 */
	public final GUID getRemoteAppID() {
		this.connect();
		return this.remoteAppID;
	}

	public final int getRemoteNodeIndex() {
		return this.remoteNodeInfo.index;
	}

	public final long getRemoteAppInstanceVersion() {
		this.connect();
		return this.remoteAppInstanceVersion;
	}

	public final short getRemoteSerializeVersion() {
		this.connect();
		return this.remoteSerializeVersion;
	}

	public final void incKeepAlive(boolean isKeepAlive) {
		this.keepAlive.addAndGet(isKeepAlive ? 1 : -1);
	}

	public final boolean isKeepAlive() {
		return this.keepAlive.get() != 0;
	}

	final void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	final int getComponentVersion() {
		return this.owner.getVersion();
	}

	final long getChannelVersion() {
		return this.owner.channelVersion;
	}

	final ApplicationImpl getApplication() {
		return this.owner.application;
	}

	final int newPackageID() {
		return this.owner.newPackageID();
	}

	final void offerFragmentResolve(NetPackageReceivingEntry<?> rpe)
			throws InterruptedException {
		this.owner.offerFragmentResolve(rpe);
	}

	/**
	 * 分配大小固定的缓冲区，大小为PACKAGE_FRAGMENT_SIZE
	 * 
	 * @return
	 */
	final DataFragment allocDataFragment() {
		return this.allocDataFragment(PACKAGE_FRAGMENT_SIZE);
	}

	/**
	 * 分配能够容纳字节数为capacity的数据的缓冲区，缓冲区前后各空余4个字节作为头部
	 * 
	 * @param capacity
	 * @return
	 */
	final DataFragment allocDataFragment(int capacity) {
		// 总长度 = 4(size) + capacity + 4(next size)
		SafeDataFragmentImpl f = new SafeDataFragmentImpl(capacity + 8);
		// size
		f.skip(4);
		f.limit(capacity + 4);
		return f;
	}

	final void releaseDataFragment(DataFragment fragment) {
		// do nothing
	}

	private final void setConnStateNoSync(ConnectionState state) {
		this.connectionState = state;
		this.connectionLock.notifyAll();
		// DebugHelper.trace("conn: " + state, 5);
	}

	/**
	 * 建立连接并且启动发送和接收线程
	 */
	public final void connect() {
		try {
			synchronized (this.connectionLock) {
				for (;;) {
					switch (this.connectionState) {
					case NONE:
						NetNodeToken addr = null;
						synchronized (this.allRemoteNodeInfos) {
							for (NetNodeToken info : this.allRemoteNodeInfos) {
								if (info.ncl != null) {
									addr = info;
									break;
								}
							}
						}
						if (addr != null) {
							this.setConnStateNoSync(ConnectionState.CONNECTING);
							// 没有连接，则建立主动连接
							if (this.activeConn == null) {
								this.activeConn = new NetActiveConnectionImpl(this, addr);
							}
							this.activeConn.connect();
							continue;
						}
						// 没有连接可用
						this.setConnStateNoSync(ConnectionState.FAIL);
						break;
					case FAIL:
						throw new IOException("连接失败");
					case STABLE:
						// 连接已建立好，直接返回
						return;
					case CONNECTING:
					case CLOSE_WAIT:
						this.connectionLock.wait();
						continue;
					}
					break;
				}
			}
			this.dispose();
			throw new IOException("连接失败");
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void unuse() {
		synchronized (this.connectionLock) {
			switch (this.connectionState) {
			case FAIL:
			case NONE:
				return;
			case CONNECTING:
			case STABLE:
				if (this.conn != null) {
					this.conn.disconnect();
				}
				break;
			}
			for (;;) {
				switch (this.connectionState) {
				case FAIL:
				case NONE:
					return;
				default:
					try {
						this.connectionLock.wait();
					} catch (InterruptedException e) {
						throw Utils.tryThrowException(e);
					}
					break;
				}
			}
		}
	}

	private final void attachPassiveConnection(InputStream in,
			OutputStream out, String remoteHost) throws Throwable {
		if (in != null && out != null) {
			throw new IllegalArgumentException();
		}
		NetPassiveConnectionImpl conn;
		for (;;) {
			synchronized (this.connectionLock) {
				switch (this.connectionState) {
				case NONE:
					this.setConnStateNoSync(ConnectionState.CONNECTING);
				case CONNECTING:
					// 返回被动连接
					if (this.passiveConn == null) {
						this.passiveConn = new NetPassiveConnectionImpl(this, remoteHost);
					}
					conn = this.passiveConn;
					break;
				case STABLE:
					if (this.conn != this.passiveConn) {
						throw new IOException("拒绝连接");
					}
					conn = this.passiveConn;
					break;
				case CLOSE_WAIT:
					this.connectionLock.wait();
					continue;
				default:
					// FAIL
					throw new IOException("连接失败");
				}
			}
			break;
		}
		if (out != null) {
			conn.servletOutputThreadEntry(out);
		} else {
			conn.servletInputThreadEntry(in);
		}
	}

	/**
	 * 设置被动连接的OutputStream并且尝试启动发送线程
	 * 
	 * @param out
	 */
	public final void attachServletOutput(OutputStream out, String remoteInfo) {
		try {
			this.attachPassiveConnection(null, out, remoteInfo);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * 设置被动连接的InputStream并且尝试启动接收线程
	 * 
	 * @return
	 * @throws IOException
	 */
	public final void attachServletInput(InputStream in, String remoteInfo) {
		try {
			this.attachPassiveConnection(in, null, remoteInfo);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * 根据NodeID判断哪个连接优先级高
	 * 
	 * @return
	 */
	private final boolean useActiveChannel() {
		int i = this.remoteNodeInfo.appID.compareTo(this.owner.application.localNodeID);
		if (i > 0) {
			return false;
		} else if (i < 0) {
			return true;
		}
		throw new UnsupportedOperationException("不能建立到自身的连接");
	}

	/**
	 * 设置连接成功并且启动发送和接收线程
	 */
	private final void stableAndStartNoSync() {
		this.setConnStateNoSync(ConnectionState.STABLE);
		this.remoteAppID = this.conn.getRemoteAppID();
		this.remoteAppInstanceVersion = this.conn.getRemoteAppInstanceVersion();
		this.remoteSerializeVersion = this.conn.getRemoteSerializeVersion();
		this.conn.start();
	}

	/**
	 * 连接成功事件
	 * 
	 * @param conn
	 */
	final void onConnectionStable(NetConnectionImpl conn) {
		if (conn != this.activeConn && conn != this.passiveConn) {
			throw new IllegalStateException();
		}
		synchronized (this.connectionLock) {
			switch (this.connectionState) {
			case NONE:
			case CLOSE_WAIT:
			case FAIL:
				throw new IllegalStateException();
			case CONNECTING:
				NetConnectionImpl conn2;
				if (conn == this.activeConn) {
					conn2 = this.passiveConn;
				} else {
					conn2 = this.activeConn;
				}
				if (conn2 != null) {
					switch (conn2.getState()) {
					case NONE:
					case CLOSING:
					case DISPOSED:
						this.conn = conn;
						this.stableAndStartNoSync();
						break;
					case CONNECTING:
						return;
					case STABLE:
						try {
							if (this.useActiveChannel()) {
								this.passiveConn.disconnect();
							} else {
								this.activeConn.disconnect();
							}
						} catch (UnsupportedOperationException e) {
							this.activeConn.disconnect();
							this.passiveConn.disconnect();
							throw e;
						}
						break;
					default:
						// RUNNING
						throw new IllegalStateException();
					}
				} else {
					this.conn = conn;
					this.stableAndStartNoSync();
				}
				break;
			case STABLE:
				if (conn != this.conn) {
					throw new IllegalStateException();
				}
				// 重新启动线程
				conn.start();
				break;
			}
		}
	}

	/**
	 * 连接销毁事件
	 * 
	 * @param conn
	 */
	final void onConnectionDisposed(NetConnectionImpl conn) {
		if (conn != this.activeConn && conn != this.passiveConn) {
			throw new IllegalStateException();
		}
		synchronized (this.connectionLock) {
			switch (this.connectionState) {
			case NONE:
			case FAIL:
				throw new IllegalStateException();
			case CONNECTING:
				NetConnectionImpl conn2;
				if (conn == this.activeConn) {
					conn2 = this.passiveConn;
				} else {
					conn2 = this.activeConn;
				}
				if (conn2 != null) {
					switch (conn2.getState()) {
					case NONE:
					case DISPOSED:
						this.conn = null;
						this.setConnStateNoSync(ConnectionState.FAIL);
						break;
					case CLOSING:
					case CONNECTING:
						return;
					case STABLE:
						this.conn = conn2;
						this.stableAndStartNoSync();
						return;
					default:
						// RUNNING
						throw new IllegalStateException();
					}
				} else {
					this.conn = null;
					this.setConnStateNoSync(ConnectionState.FAIL);
				}
				break;
			case STABLE:
				if (conn != this.conn) {
					throw new IllegalStateException();
				}
				this.conn = null;
				this.setConnStateNoSync(ConnectionState.FAIL);
				break;
			case CLOSE_WAIT:
				if (conn != this.conn) {
					throw new IllegalStateException();
				}
				this.conn = null;
				this.setConnStateNoSync(ConnectionState.NONE);
				return;
			}
		}
		this.dispose();
	}

	private final void dispose() {
		if (NetDebug.TRACE_CONNECT()) {
			this.trace("网络通信：连接到远程主机的信道已销毁");
		}
		this.owner.unuseChannel(this);
		synchronized (this.outLock) {
			this.sendingPackages.visitAll(new ValueVisitor<NetPackageSendingEntry<?>>() {
				public void visit(int key, NetPackageSendingEntry<?> value) {
					value.setResolved(false);
				}
			});
			this.waitingAckFragments.clear();
			this.waitingBuildingPackages.clear();
			this.waitingSendingFragments.clear();
			this.sendingPackages.clear();
		}
		synchronized (this.inLock) {
			this.receivingPackages.visitAll(new ValueVisitor<NetPackageReceivingEntry<?>>() {
				public void visit(int key, NetPackageReceivingEntry<?> value) {
					value.breakResolve();
				}
			});
			this.receivingPackages.clear();
		}
	}

	// /////////////////////////////////////////////////
	// //////////////构造线程相关////////////////////////

	public final <TAttachment> AsyncIOStub<TAttachment> startSendingPackage(
			DataFragmentBuilder<? super TAttachment> builder,
			TAttachment attachment) {
		if (builder == null) {
			throw new NullArgumentException("builder");
		}
		// 确保连接有效
		this.connect();
		final NetPackageSendingEntry<TAttachment> spe = new NetPackageSendingEntry<TAttachment>(this, builder, attachment);
		synchronized (this.outLock) {
			this.sendingPackages.put(spe.packageID, spe);
			this.tryStartFragmentBuildNoSync(spe);
		}
		return spe;
	}

	final void tryStartFragmentBuild(NetPackageSendingEntry<?> newOne) {
		synchronized (this.outLock) {
			this.tryStartFragmentBuildNoSync(newOne);
		}
	}

	protected final void tryStartFragmentBuildNoSync(
			NetPackageSendingEntry<?> newOne) {
		if (newOne != null) {
			newOne.setState(NetPackageSendingEntry.State.QUEUING);
			this.waitingBuildingPackages.add(newOne);
		}
		for (int needStartBuildCount = this.waitingSendingQueueMaxSize - this.waitingSendingFragments.size() - this.buildingFragmentCount; needStartBuildCount > 0; needStartBuildCount--) {
			// 需要起动fragment构造
			final NetPackageSendingEntry<?> one = this.waitingBuildingPackages.poll();
			if (one == null) {
				break;
			}
			one.setState(NetPackageSendingEntry.State.QUEUING);
			this.owner.offerFragmentBuild(one);
			this.buildingFragmentCount++;
		}
	}

	// //////////////构造线程相关////////////////////////
	// /////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// //////////////发送线程相关////////////////////////

	/**
	 * 发送中断接收数据包控制信息，不需要回复确认
	 */
	final void postBreakReceivePackageCtrl(int packageID) {
		// 将包移出发送队列
		synchronized (this.outLock) {
			this.sendingPackages.remove(packageID);
		}
		DataFragment fragment = this.allocDataFragment(5);
		fragment.writeByte(CTRL_FLAG_BREAK_RECEIVE);
		fragment.writeInt(packageID);
		this.postDataFragmentToSend(null, fragment);
	}

	/**
	 * 发送中断发送数据包控制信息，需要回复确认
	 */
	final void postBreakSendPackageCtrl(final int packageID) {
		this.postAckReqiredFragmentToSend(new IAckRequiredFragmentBuilder() {
			public DataFragment build() {
				DataFragment fragment = NetChannelImpl.this.allocDataFragment(5);
				fragment.writeByte(CTRL_FLAG_BREAK_SEND);
				fragment.writeInt(packageID);
				return fragment;
			}
		});
	}

	final void packageResolved(final int packageID) {
		synchronized (this.inLock) {
			this.receivingPackages.remove(packageID);
		}
		// 发送数据包还原完毕控制信息，需要回复确认
		this.postAckReqiredFragmentToSend(new IAckRequiredFragmentBuilder() {
			public DataFragment build() {
				DataFragment fragment = NetChannelImpl.this.allocDataFragment(5);
				fragment.writeByte(CTRL_FLAG_RESOLVED);
				fragment.writeInt(packageID);
				return fragment;
			}
		});
	}

	/**
	 * 构造并投递需要ack的片段
	 * 
	 * @param builder
	 * @param otherBuilders
	 */
	final void postAckReqiredFragmentToSend(
			IAckRequiredFragmentBuilder builder,
			IAckRequiredFragmentBuilder... otherBuilders) {
		synchronized (this.outLock) {
			int ackID = this.ackSeed++;
			DataFragment fragment = builder.build();
			fragment.writeInt(ackID);
			fragment.limit(fragment.getPosition());
			// 放入ack队列
			this.waitingAckFragments.offer(new AckRequiredFragmentStub(ackID, builder));
			// 放入发送对列
			this.waitingSendingFragments.add(fragment);
			for (IAckRequiredFragmentBuilder b : otherBuilders) {
				ackID = this.ackSeed++;
				fragment = b.build();
				fragment.writeInt(ackID);
				fragment.limit(fragment.getPosition());
				// 放入ack队列
				this.waitingAckFragments.offer(new AckRequiredFragmentStub(ackID, builder));
				// 放入发送对列
				this.waitingSendingFragments.add(fragment);
			}
			// 通知发送线程启动
			this.outLock.notifyAll();
		}
	}

	/**
	 * 构造好Fragment后投递到发送线程
	 * 
	 * @param asyncStub
	 * @param fragment
	 */
	final void postDataFragmentToSend(NetPackageSendingEntry<?> asyncStub,
			DataFragment fragment) {
		POST: {
			synchronized (this.outLock) {
				if (asyncStub != null) {
					this.buildingFragmentCount--;
					if (asyncStub.needResetPackage(this.conn.generation)) {
						break POST;
					}
					switch (asyncStub.getState()) {
					case BUILDING_AND_SENDING:
						// 尝试再次启动构造过程
						this.tryStartFragmentBuildNoSync(asyncStub);
						break;
					case BUILDING_COMPLETE:
						asyncStub.setState(NetPackageSendingEntry.State.WAITING_RESOLVE);
						break;
					}
				}
				fragment.limit(fragment.getPosition());
				this.waitingSendingFragments.add(fragment);
				// 通知发送线程启动
				this.outLock.notifyAll();
			}
			return;
		}
		asyncStub.tryResetPackage();
	}

	protected final void sendThreadRun(OutputStream out) throws Throwable {
		// 询问数据包接收状态
		int ackID;
		final ArrayList<NetPackageSendingEntry<?>> arr = new ArrayList<NetPackageSendingEntry<?>>();
		synchronized (this.outLock) {
			this.sendingPackages.visitAll(new ValueVisitor<NetPackageSendingEntry<?>>() {
				public void visit(int key, NetPackageSendingEntry<?> value) {
					switch (value.getState()) {
					case WAITING_RESOLVE:
						arr.add(value);
						break;
					}
				}
			});
			ackID = this.ackSeed++;
		}
		DataFragment toSend = this.allocDataFragment(5 + arr.size() * 4);
		toSend.writeByte(CTRL_FLAG_PACKAGE_STATE);
		for (NetPackageSendingEntry<?> spe : arr) {
			toSend.writeInt(spe.packageID);
		}
		toSend.writeInt(ackID);
		toSend.limit(toSend.getPosition());
		synchronized (this.outLock) {
			this.waitingSendingFragments.offer(toSend);
		}
		// 发送线程主循环
		for (;;) {
			SEND: {
				synchronized (this.outLock) {
					toSend = this.waitingSendingFragments.poll();
					// 启动新构造线程
					this.tryStartFragmentBuildNoSync(null);
					if (toSend == null) {
						// 发送完成，进入空闲状态
						break SEND;
					}
				}
				if (NetDebug.TRACE_IO()) {
					this.trace("网络通信：发送数据包[" + NetDebug.dataToStr(toSend) + "]");
				}
				this.conn.send(out, toSend);
				continue;
			}
			this.doIdle(out);
		}
	}

	private final void doIdle(OutputStream out) throws Throwable {
		DataFragment toSend;
		long idleTime = 0;
		for (;;) {
			POLL: synchronized (this.outLock) {
				// 检查发送线程空闲
				if (!this.waitingSendingFragments.isEmpty()) {
					return;
				}
				// 等待
				this.outLock.wait(KEEP_ALIVE_TIMING);
				// 检查发送线程空闲
				if (!this.waitingSendingFragments.isEmpty()) {
					return;
				}
				// 检查信道空闲
				boolean idle = !this.isKeepAlive() && this.sendingPackages.isEmpty() && this.waitingBuildingPackages.isEmpty() && this.waitingAckFragments.isEmpty();
				if (idle) {
					synchronized (this.inLock) {
						idle = this.receivingPackages.isEmpty();
					}
				}
				IDLE: if (idle) {
					// 记录空闲时间
					idleTime += KEEP_ALIVE_TIMING;
					// 判断超时
					if (idleTime > this.timeout) {
						// 关闭连接
						synchronized (this.connectionLock) {
							switch (this.connectionState) {
							case STABLE:
								break;
							case CLOSE_WAIT:
								// 发送keep-alive
								break IDLE;
							default:
								throw new IllegalStateException("错误状态：" + this.connectionState);
							}
							// 锁定发送线程
							this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
							this.conn.allowClose(true);
						}
						if (NetDebug.TRACE_CONNECT()) {
							this.trace("网络通信：信道空闲时间过长，主动关闭连接");
						}
						// 发送close消息
						toSend = this.allocDataFragment(1);
						toSend.writeByte(CTRL_FLAG_CLOSE);
						toSend.limit(toSend.getPosition());
						break POLL;
					}
				} else {
					// 清除空闲时间
					idleTime = 0;
				}
				// 发送keep-alive
				toSend = this.allocDataFragment(1);
				toSend.writeByte(CTRL_FLAG_KEEP_ALIVE);
				toSend.limit(toSend.getPosition());
			}
			if (NetDebug.TRACE_IO()) {
				this.trace("网络通信：发送数据包[" + NetDebug.dataToStr(toSend) + "]");
			}
			this.conn.send(out, toSend);
		}
	}

	// //////////////发送线程相关////////////////////////
	// /////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// //////////////接收线程相关////////////////////////

	/**
	 * 处理数据包片断
	 */
	private final void onPackageFragment(byte ctrlFlag, DataFragment received)
			throws InterruptedException {
		final int packageID = received.readInt();
		NetPackageReceivingEntry<?> rpe;
		synchronized (this.inLock) {
			rpe = this.receivingPackages.get(packageID);
		}
		if (rpe != null && rpe.receiverGeneration != this.conn.generation) {
			// 连接重置，重新接收数据包
			synchronized (this.inLock) {
				this.receivingPackages.remove(packageID);
			}
			rpe.cancel();
			rpe = null;
		}
		if (rpe == null) {
			if ((ctrlFlag & CTRL_FLAG_PACKAGE_FIRST) == 0) {
				// 丢弃掉无主的片断，说明是中断接收的片段
				this.releaseDataFragment(received);
				if (NetDebug.TRACE_FAULT()) {
					this.trace("网络通信：收到无效的片段，包ID[" + packageID + "]");
				}
				this.postBreakSendPackageCtrl(packageID);
				return;
			}
			// 新数据包第一个片断
			rpe = new NetPackageReceivingEntry<Object>(this, packageID);
			rpe.receiverGeneration = this.conn.generation;
			this.owner.offerPackageReceiving(rpe, received);
			if (!rpe.resolverValid()) {
				if (NetDebug.TRACE_FAULT()) {
					this.trace("网络通信：收到无法处理的数据包，包ID[" + packageID + "]");
				}
				// 数据包没有被接受
				this.releaseDataFragment(received);
				this.postBreakSendPackageCtrl(packageID);
				return;
			}
			synchronized (this.inLock) {
				this.receivingPackages.put(packageID, rpe);
			}
		}
		// 添加到resolve队列
		rpe.queueToResolve(received, (ctrlFlag & CTRL_FLAG_PACKAGE_LAST) != 0);
	}

	private final void onBreakPackageReceiveFragment(DataFragment received)
			throws InterruptedException {
		this.breakReceive(received.readInt());
	}

	private final void onBreakPackageSendFragment(DataFragment received) {
		final int packageID = received.readInt();
		final int ackID = received.readInt();
		try {
			this.breakSend(packageID);
		} finally {
			DataFragment fragment = this.allocDataFragment(5);
			fragment.writeByte((byte) (CTRL_FLAG_BREAK_SEND | CTRL_FLAG_RESPONSE));
			fragment.writeInt(ackID);
			this.postDataFragmentToSend(null, fragment);
		}
	}

	private final void onBreakPackageSendResponseFragment(DataFragment received) {
		this.commitAck(received.readInt());
	}

	/**
	 * 包接收完毕，接收方还原完毕某包后回发
	 * 
	 * @param received
	 */
	private final void onPackageResolvedFragment(DataFragment received) {
		final int packageID = received.readInt();
		final int ackID = received.readInt();
		NetPackageSendingEntry<?> spe = null;
		try {
			synchronized (this.outLock) {
				spe = this.sendingPackages.remove(packageID);
				if (spe == null) {
					return;
				}
			}
			spe.setResolved(true);
		} finally {
			DataFragment fragment = this.allocDataFragment(5);
			fragment.writeByte((byte) (CTRL_FLAG_RESOLVED | CTRL_FLAG_RESPONSE));
			fragment.writeInt(packageID);
			fragment.writeInt(ackID);
			this.postDataFragmentToSend(null, fragment);
		}
	}

	/**
	 * 回复数据包还原完毕消息
	 * 
	 * @param received
	 */
	private final void onPackageResolvedResponseFragment(DataFragment received) {
		int packageID = received.readInt();
		this.commitAck(received.readInt());
		synchronized (this.inLock) {
			this.receivingPackages.remove(packageID);
		}
	}

	private final void onPackageStateFragment(DataFragment received) {
		DataFragment fragment = this.allocDataFragment(5 + received.remain());
		fragment.writeByte((byte) (CTRL_FLAG_PACKAGE_STATE | CTRL_FLAG_RESPONSE));
		fragment.writeInt(this.conn.generation);
		if (received.remain() > 4) {
			synchronized (this.inLock) {
				do {
					int packageID = received.readInt();
					NetPackageReceivingEntry<?> rpe = this.receivingPackages.get(packageID);
					if (rpe == null || !rpe.isReceivingComplete()) {
						fragment.writeInt(packageID);
					}
				} while (received.remain() > 4);
			}
		}
		this.commitAck(received.readInt());
		this.postDataFragmentToSend(null, fragment);
	}

	private final void onPackageStateResponseFragment(DataFragment received) {
		if (this.conn.generation > received.readInt()) {
			return;
		}
		if (received.remain() > 0) {
			final ArrayList<NetPackageSendingEntry<?>> arr = new ArrayList<NetPackageSendingEntry<?>>();
			synchronized (this.outLock) {
				do {
					int packageID = received.readInt();
					NetPackageSendingEntry<?> spe = this.sendingPackages.get(packageID);
					if (spe != null) {
						switch (spe.getState()) {
						case WAITING_RESOLVE:
							arr.add(spe);
						}
					}
				} while (received.remain() > 0);
			}
			if (arr.size() > 0) {
				// 重置数据包发送过程
				this.owner.application.overlappedManager.startWork(new Work() {
					@Override
					protected void doWork(WorkingThread thread)
							throws Throwable {
						for (NetPackageSendingEntry<?> spe : arr) {
							spe.tryResetPackage();
						}
					}
				});
			}
		}
	}

	private final void onCloseFragment(DataFragment received) {
		synchronized (this.outLock) {
			boolean idle = false;
			if (this.sendingPackages.isEmpty() && this.waitingAckFragments.isEmpty()) {
				synchronized (this.inLock) {
					if (this.receivingPackages.isEmpty()) {
						idle = true;
					}
				}
			}
			synchronized (this.connectionLock) {
				switch (this.connectionState) {
				case STABLE:
					if (!idle) {
						// 取消断开
						break;
					}
					this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
				case CLOSE_WAIT:
					// 断开连接
					this.conn.disconnect();
					return;
				}
			}
		}
		// 取消断开
		DataFragment fragment = this.allocDataFragment(1);
		fragment.writeByte((byte) (CTRL_FLAG_CLOSE | CTRL_FLAG_CLOSE_CANCEL));
		this.postDataFragmentToSend(null, fragment);
	}

	private final void onCloseCancelFragment(DataFragment received) {
		// 解锁
		synchronized (this.connectionLock) {
			switch (this.connectionState) {
			case CLOSE_WAIT:
				this.setConnStateNoSync(ConnectionState.STABLE);
				this.conn.allowClose(false);
				break;
			default:
				return;
			}
		}
	}

	/**
	 * 校验ack，并从ack队列中移除
	 * 
	 * @param ackID
	 */
	private final void commitAck(int ackID) {
		ArrayList<IAckRequiredFragmentBuilder> arr = null;
		synchronized (this.outLock) {
			for (;;) {
				AckRequiredFragmentStub stub = this.waitingAckFragments.peek();
				if (stub == null) {
					break;
				}
				if (stub.ackID < ackID) {
					// 移除
					if (arr == null) {
						arr = new ArrayList<IAckRequiredFragmentBuilder>();
					}
					arr.add(this.waitingAckFragments.poll().builder);
					if (NetDebug.TRACE_FAULT()) {
						this.trace("网络通信：片段[" + stub.ackID + "]没有送达，重新发送");
					}
					continue;
				} else if (stub.ackID == ackID) {
					// 移除
					this.waitingAckFragments.poll();
				}
				break;
			}
		}
		if (arr != null) {
			if (arr.size() > 1) {
				int c = arr.size();
				IAckRequiredFragmentBuilder[] otherBuilders = new IAckRequiredFragmentBuilder[c - 1];
				for (int i = 1; i < c; i++) {
					otherBuilders[i - 1] = arr.get(i);
				}
				this.postAckReqiredFragmentToSend(arr.get(0), otherBuilders);
			} else {
				this.postAckReqiredFragmentToSend(arr.get(0));
			}
		}
	}

	final void breakReceive(int packageID) {
		if (NetDebug.TRACE_FAULT()) {
			this.trace("网络通信：中断接收数据包[" + packageID + "]");
		}
		final NetPackageReceivingEntry<?> rpe;
		synchronized (this.inLock) {
			rpe = this.receivingPackages.remove(packageID);
		}
		if (rpe != null) {
			rpe.breakResolve();
		}
	}

	final void breakSend(int packageID) {
		if (NetDebug.TRACE_FAULT()) {
			this.trace("网络通信：中断发送数据包[" + packageID + "]");
		}
		NetPackageSendingEntry<?> spe;
		synchronized (this.outLock) {
			spe = this.sendingPackages.remove(packageID);
			if (spe == null) {
				return;
			}
			this.waitingBuildingPackages.remove(spe);
		}
		spe.setResolved(false);
	}

	/**
	 * 处理接收的片段，并返回缓冲区可否重用
	 * 
	 * @param received
	 * @return
	 * @throws Throwable
	 */
	protected final boolean dispatchFragment(DataFragment received)
			throws Throwable {
		if (NetDebug.TRACE_IO()) {
			this.trace("网络通信：收到数据包[" + NetDebug.dataToStr(received) + "]");
		}
		final byte ctrlFlag = received.readByte();
		switch (ctrlFlag & CTRL_FLAG_TYPE_MASK) {
		case CTRL_FLAG_PACKAGE:
			this.onPackageFragment(ctrlFlag, received);
			break;
		case CTRL_FLAG_BREAK_RECEIVE:
			this.onBreakPackageReceiveFragment(received);
			return true;
		case CTRL_FLAG_BREAK_SEND:
			switch (ctrlFlag & CTRL_FLAG_SUBTYPE_MASK) {
			case 0:
				this.onBreakPackageSendFragment(received);
				break;
			case CTRL_FLAG_RESPONSE:
				this.onBreakPackageSendResponseFragment(received);
				break;
			}
			return true;
		case CTRL_FLAG_RESOLVED:
			switch (ctrlFlag & CTRL_FLAG_SUBTYPE_MASK) {
			case 0:
				this.onPackageResolvedFragment(received);
				break;
			case CTRL_FLAG_RESPONSE:
				this.onPackageResolvedResponseFragment(received);
				break;
			}
			return true;
		case CTRL_FLAG_PACKAGE_STATE:
			switch (ctrlFlag & CTRL_FLAG_SUBTYPE_MASK) {
			case 0:
				this.onPackageStateFragment(received);
				break;
			case CTRL_FLAG_RESPONSE:
				this.onPackageStateResponseFragment(received);
				break;
			}
			return true;
		case CTRL_FLAG_CLOSE:
			switch (ctrlFlag & CTRL_FLAG_SUBTYPE_MASK) {
			case 0:
				this.onCloseFragment(received);
				break;
			case CTRL_FLAG_CLOSE_CANCEL:
				this.onCloseCancelFragment(received);
				break;
			}
			return true;
		case CTRL_FLAG_KEEP_ALIVE:
			// do nothing
			return true;
		default:
			throw new IllegalStateException("无法识别的数据类型");
		}
		return false;
	}

	/**
	 * 发送线程提取片断来发送<br>
	 * 返回需要发送的片断，<br>
	 * 如果返回null表示没有需要发送的片断，<br>
	 * 此时发送线程可以回归池中
	 * 
	 * @return 返回需要发送的片断或null
	 */
	final void receiveThreadRun(InputStream in) throws Throwable {
		try {
			for (;;) {
				DataFragment received = this.allocDataFragment();
				for (;;) {
					received = this.conn.receive(in, received);
					if (!this.dispatchFragment(received)) {
						break;
					}
				}
			}
		} catch (Throwable e) {
			if (NetDebug.TRACE_EXCEPTION()) {
				e.printStackTrace();
			}
		}
	}

	// //////////////接收线程相关////////////////////////
	// /////////////////////////////////////////////////

	private String hostInfo;
	private URL hostAddr;

	final void trace(String msg) {
		NetDebug.trace(msg + this.hostInfo);
	}

	@Override
	public String toString() {
		return String.format("NetChannel[app = %s, node = %s, state = %s]", this.remoteAppID, this.remoteNodeInfo, this.connectionState);
	}
}