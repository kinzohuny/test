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
 * ����ͨ��ͨ��
 * 
 * @author niuhaifeng
 * 
 */
public final class NetChannelImpl {
	/**
	 * ����״̬
	 * 
	 * @author niuhaifeng
	 * 
	 */
	private enum ConnectionState {
		/**
		 * û������
		 */
		NONE,
		/**
		 * �ȴ��������
		 */
		CONNECTING,
		/**
		 * ������ɣ����Խ���ͨ��
		 */
		STABLE,
		/**
		 * ����ʧ�ܣ������ӿ���
		 */
		FAIL,
		/**
		 * �ȴ����ӹر�
		 */
		CLOSE_WAIT
	}

	/**
	 * ��Ҫȷ�ϵ�����Ƭ�εĹ�����
	 * 
	 * @author niuhaifeng
	 * 
	 */
	private interface IAckRequiredFragmentBuilder {
		public DataFragment build();
	}

	/**
	 * ��Ҫȷ�ϵ�����Ƭ�ζ���Ԫ��
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
	 * ��������
	 */
	static final byte CTRL_FLAG_TYPE_MASK = (byte) 0x0f;
	/**
	 * ����������
	 */
	static final byte CTRL_FLAG_SUBTYPE_MASK = (byte) 0xf0;
	/**
	 * ���Ʊ�ǣ����ݰ�Ƭ�ϣ�����Ҫ�ظ�
	 */
	static final byte CTRL_FLAG_PACKAGE = 1;
	/**
	 * ���Ʊ�ǣ����ݰ�Ƭ��ͷ
	 */
	static final byte CTRL_FLAG_PACKAGE_FIRST = (byte) 0x40;
	/**
	 * ���Ʊ�ǣ����ݰ�Ƭ��β
	 */
	static final byte CTRL_FLAG_PACKAGE_LAST = (byte) 0x80;
	/**
	 * ���Ʊ�ǣ���ֹ���ܷ��������ݰ����ñ���ɷ��ͷ����ͣ�����Ҫ�ظ�
	 */
	static final byte CTRL_FLAG_BREAK_RECEIVE = 2;
	/**
	 * ���Ʊ�ǣ���ֹ���ͷ��������ݰ����ñ���ɽ��ܷ����ͣ���Ҫ�ظ�ackID
	 */
	static final byte CTRL_FLAG_BREAK_SEND = 3;
	/**
	 * ���Ʊ�ǣ���������ϣ����ܷ���ԭ���ĳ����ط�����Ҫ�ظ�ackID
	 */
	static final byte CTRL_FLAG_RESOLVED = 4;
	/**
	 * ���Ʊ�ǣ�ѯ�����ݰ�����״̬����Ҫ�ظ�ackID��û�н�����ϵ����ݰ���packageID
	 */
	static final byte CTRL_FLAG_PACKAGE_STATE = 5;
	/**
	 * ���Ʊ�ǣ��Ͽ����ӣ���ѡ�ظ����ظ���Ϣ��ʾ������������
	 */
	static final byte CTRL_FLAG_CLOSE = 7;
	/**
	 * ���Ʊ�ǣ��������ӣ�����Ҫ�ظ�
	 */
	static final byte CTRL_FLAG_KEEP_ALIVE = 12;
	/**
	 * ���Ʊ�ǣ��ظ���Ϣ
	 */
	static final byte CTRL_FLAG_RESPONSE = (byte) 0x40;
	/**
	 * CLOSE��Ϣ�ظ���ȡ���Ͽ�����
	 */
	static final byte CTRL_FLAG_CLOSE_CANCEL = CTRL_FLAG_RESPONSE;
	/**
	 * ����Ƭ����󳤶�
	 */
	static final int PACKAGE_FRAGMENT_SIZE = 1024 * 32;
	/**
	 * ����״̬��ʱʱ��
	 */
	static final long DEFAULT_IDLE_TIMEOUT = 10000L;
	/**
	 * keep-alive��Ϣ�ļ��ʱ��
	 */
	static final long KEEP_ALIVE_TIMING = 4000L;

	private final NetChannelManagerImpl owner;
	/**
	 * send��
	 */
	final Object outLock = new Object();
	/**
	 * receive��
	 */
	final Object inLock = new Object();
	/**
	 * ACK�������
	 */
	private volatile int ackSeed;
	/**
	 * �ȴ�ack����Ϣ����
	 */
	private final Queue<AckRequiredFragmentStub> waitingAckFragments = new LinkedList<AckRequiredFragmentStub>();
	/**
	 * �ȴ���������ݰ�
	 */
	private final Queue<NetPackageSendingEntry<?>> waitingBuildingPackages = new LinkedList<NetPackageSendingEntry<?>>();
	/**
	 * �����е����ݰ�
	 */
	final IntKeyMap<NetPackageSendingEntry<?>> sendingPackages = new IntKeyMap<NetPackageSendingEntry<?>>();
	/**
	 * ������Ƭ��<br>
	 * Ϊ��ʹ���������������һ��Ƭ�Ϸ���ʱ����һЩƬ�Ͼ�Ԥ��׼���á�
	 */
	protected final Queue<DataFragment> waitingSendingFragments = new LinkedList<DataFragment>();
	/**
	 * ���յ����ݰ�
	 */
	private final IntKeyMap<NetPackageReceivingEntry<?>> receivingPackages = new IntKeyMap<NetPackageReceivingEntry<?>>();
	/**
	 * ���ڹ����Fragment�ĸ���
	 */
	protected volatile int buildingFragmentCount;
	/**
	 * �ȴ������жӵ���󳤶�
	 */
	private int waitingSendingQueueMaxSize = 5;
	/**
	 * ָʾ�Ƿ񱣳�����
	 */
	private final AtomicInteger keepAlive = new AtomicInteger();
	/**
	 * ���г�ʱʱ��
	 */
	private long timeout = DEFAULT_IDLE_TIMEOUT;
	/**
	 * ����״̬
	 */
	private ConnectionState connectionState = ConnectionState.NONE;
	/**
	 * ������
	 */
	private final Object connectionLock = new Object();
	/**
	 * ��Ч����
	 */
	private NetConnectionImpl conn;
	/**
	 * ��ʱ����������
	 */
	private NetActiveConnectionImpl activeConn;
	/**
	 * ��ʱ�ı�������
	 */
	private NetPassiveConnectionImpl passiveConn;
	/**
	 * Զ��������ַ���ڵ�ID��������
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
			this.hostInfo = String.format(" - Զ������URL[%s]������[%d]ID[%s]", this.hostAddr, this.remoteNodeInfo.index, this.remoteNodeInfo.appID);
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
	 * ��ȺID
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
	 * �����С�̶��Ļ���������СΪPACKAGE_FRAGMENT_SIZE
	 * 
	 * @return
	 */
	final DataFragment allocDataFragment() {
		return this.allocDataFragment(PACKAGE_FRAGMENT_SIZE);
	}

	/**
	 * �����ܹ������ֽ���Ϊcapacity�����ݵĻ�������������ǰ�������4���ֽ���Ϊͷ��
	 * 
	 * @param capacity
	 * @return
	 */
	final DataFragment allocDataFragment(int capacity) {
		// �ܳ��� = 4(size) + capacity + 4(next size)
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
	 * �������Ӳ����������ͺͽ����߳�
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
							// û�����ӣ�������������
							if (this.activeConn == null) {
								this.activeConn = new NetActiveConnectionImpl(this, addr);
							}
							this.activeConn.connect();
							continue;
						}
						// û�����ӿ���
						this.setConnStateNoSync(ConnectionState.FAIL);
						break;
					case FAIL:
						throw new IOException("����ʧ��");
					case STABLE:
						// �����ѽ����ã�ֱ�ӷ���
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
			throw new IOException("����ʧ��");
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
					// ���ر�������
					if (this.passiveConn == null) {
						this.passiveConn = new NetPassiveConnectionImpl(this, remoteHost);
					}
					conn = this.passiveConn;
					break;
				case STABLE:
					if (this.conn != this.passiveConn) {
						throw new IOException("�ܾ�����");
					}
					conn = this.passiveConn;
					break;
				case CLOSE_WAIT:
					this.connectionLock.wait();
					continue;
				default:
					// FAIL
					throw new IOException("����ʧ��");
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
	 * ���ñ������ӵ�OutputStream���ҳ������������߳�
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
	 * ���ñ������ӵ�InputStream���ҳ������������߳�
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
	 * ����NodeID�ж��ĸ��������ȼ���
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
		throw new UnsupportedOperationException("���ܽ��������������");
	}

	/**
	 * �������ӳɹ������������ͺͽ����߳�
	 */
	private final void stableAndStartNoSync() {
		this.setConnStateNoSync(ConnectionState.STABLE);
		this.remoteAppID = this.conn.getRemoteAppID();
		this.remoteAppInstanceVersion = this.conn.getRemoteAppInstanceVersion();
		this.remoteSerializeVersion = this.conn.getRemoteSerializeVersion();
		this.conn.start();
	}

	/**
	 * ���ӳɹ��¼�
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
				// ���������߳�
				conn.start();
				break;
			}
		}
	}

	/**
	 * ���������¼�
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
			this.trace("����ͨ�ţ����ӵ�Զ���������ŵ�������");
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
	// //////////////�����߳����////////////////////////

	public final <TAttachment> AsyncIOStub<TAttachment> startSendingPackage(
			DataFragmentBuilder<? super TAttachment> builder,
			TAttachment attachment) {
		if (builder == null) {
			throw new NullArgumentException("builder");
		}
		// ȷ��������Ч
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
			// ��Ҫ��fragment����
			final NetPackageSendingEntry<?> one = this.waitingBuildingPackages.poll();
			if (one == null) {
				break;
			}
			one.setState(NetPackageSendingEntry.State.QUEUING);
			this.owner.offerFragmentBuild(one);
			this.buildingFragmentCount++;
		}
	}

	// //////////////�����߳����////////////////////////
	// /////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// //////////////�����߳����////////////////////////

	/**
	 * �����жϽ������ݰ�������Ϣ������Ҫ�ظ�ȷ��
	 */
	final void postBreakReceivePackageCtrl(int packageID) {
		// �����Ƴ����Ͷ���
		synchronized (this.outLock) {
			this.sendingPackages.remove(packageID);
		}
		DataFragment fragment = this.allocDataFragment(5);
		fragment.writeByte(CTRL_FLAG_BREAK_RECEIVE);
		fragment.writeInt(packageID);
		this.postDataFragmentToSend(null, fragment);
	}

	/**
	 * �����жϷ������ݰ�������Ϣ����Ҫ�ظ�ȷ��
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
		// �������ݰ���ԭ��Ͽ�����Ϣ����Ҫ�ظ�ȷ��
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
	 * ���첢Ͷ����Ҫack��Ƭ��
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
			// ����ack����
			this.waitingAckFragments.offer(new AckRequiredFragmentStub(ackID, builder));
			// ���뷢�Ͷ���
			this.waitingSendingFragments.add(fragment);
			for (IAckRequiredFragmentBuilder b : otherBuilders) {
				ackID = this.ackSeed++;
				fragment = b.build();
				fragment.writeInt(ackID);
				fragment.limit(fragment.getPosition());
				// ����ack����
				this.waitingAckFragments.offer(new AckRequiredFragmentStub(ackID, builder));
				// ���뷢�Ͷ���
				this.waitingSendingFragments.add(fragment);
			}
			// ֪ͨ�����߳�����
			this.outLock.notifyAll();
		}
	}

	/**
	 * �����Fragment��Ͷ�ݵ������߳�
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
						// �����ٴ������������
						this.tryStartFragmentBuildNoSync(asyncStub);
						break;
					case BUILDING_COMPLETE:
						asyncStub.setState(NetPackageSendingEntry.State.WAITING_RESOLVE);
						break;
					}
				}
				fragment.limit(fragment.getPosition());
				this.waitingSendingFragments.add(fragment);
				// ֪ͨ�����߳�����
				this.outLock.notifyAll();
			}
			return;
		}
		asyncStub.tryResetPackage();
	}

	protected final void sendThreadRun(OutputStream out) throws Throwable {
		// ѯ�����ݰ�����״̬
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
		// �����߳���ѭ��
		for (;;) {
			SEND: {
				synchronized (this.outLock) {
					toSend = this.waitingSendingFragments.poll();
					// �����¹����߳�
					this.tryStartFragmentBuildNoSync(null);
					if (toSend == null) {
						// ������ɣ��������״̬
						break SEND;
					}
				}
				if (NetDebug.TRACE_IO()) {
					this.trace("����ͨ�ţ��������ݰ�[" + NetDebug.dataToStr(toSend) + "]");
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
				// ��鷢���߳̿���
				if (!this.waitingSendingFragments.isEmpty()) {
					return;
				}
				// �ȴ�
				this.outLock.wait(KEEP_ALIVE_TIMING);
				// ��鷢���߳̿���
				if (!this.waitingSendingFragments.isEmpty()) {
					return;
				}
				// ����ŵ�����
				boolean idle = !this.isKeepAlive() && this.sendingPackages.isEmpty() && this.waitingBuildingPackages.isEmpty() && this.waitingAckFragments.isEmpty();
				if (idle) {
					synchronized (this.inLock) {
						idle = this.receivingPackages.isEmpty();
					}
				}
				IDLE: if (idle) {
					// ��¼����ʱ��
					idleTime += KEEP_ALIVE_TIMING;
					// �жϳ�ʱ
					if (idleTime > this.timeout) {
						// �ر�����
						synchronized (this.connectionLock) {
							switch (this.connectionState) {
							case STABLE:
								break;
							case CLOSE_WAIT:
								// ����keep-alive
								break IDLE;
							default:
								throw new IllegalStateException("����״̬��" + this.connectionState);
							}
							// ���������߳�
							this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
							this.conn.allowClose(true);
						}
						if (NetDebug.TRACE_CONNECT()) {
							this.trace("����ͨ�ţ��ŵ�����ʱ������������ر�����");
						}
						// ����close��Ϣ
						toSend = this.allocDataFragment(1);
						toSend.writeByte(CTRL_FLAG_CLOSE);
						toSend.limit(toSend.getPosition());
						break POLL;
					}
				} else {
					// �������ʱ��
					idleTime = 0;
				}
				// ����keep-alive
				toSend = this.allocDataFragment(1);
				toSend.writeByte(CTRL_FLAG_KEEP_ALIVE);
				toSend.limit(toSend.getPosition());
			}
			if (NetDebug.TRACE_IO()) {
				this.trace("����ͨ�ţ��������ݰ�[" + NetDebug.dataToStr(toSend) + "]");
			}
			this.conn.send(out, toSend);
		}
	}

	// //////////////�����߳����////////////////////////
	// /////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// //////////////�����߳����////////////////////////

	/**
	 * �������ݰ�Ƭ��
	 */
	private final void onPackageFragment(byte ctrlFlag, DataFragment received)
			throws InterruptedException {
		final int packageID = received.readInt();
		NetPackageReceivingEntry<?> rpe;
		synchronized (this.inLock) {
			rpe = this.receivingPackages.get(packageID);
		}
		if (rpe != null && rpe.receiverGeneration != this.conn.generation) {
			// �������ã����½������ݰ�
			synchronized (this.inLock) {
				this.receivingPackages.remove(packageID);
			}
			rpe.cancel();
			rpe = null;
		}
		if (rpe == null) {
			if ((ctrlFlag & CTRL_FLAG_PACKAGE_FIRST) == 0) {
				// ������������Ƭ�ϣ�˵�����жϽ��յ�Ƭ��
				this.releaseDataFragment(received);
				if (NetDebug.TRACE_FAULT()) {
					this.trace("����ͨ�ţ��յ���Ч��Ƭ�Σ���ID[" + packageID + "]");
				}
				this.postBreakSendPackageCtrl(packageID);
				return;
			}
			// �����ݰ���һ��Ƭ��
			rpe = new NetPackageReceivingEntry<Object>(this, packageID);
			rpe.receiverGeneration = this.conn.generation;
			this.owner.offerPackageReceiving(rpe, received);
			if (!rpe.resolverValid()) {
				if (NetDebug.TRACE_FAULT()) {
					this.trace("����ͨ�ţ��յ��޷���������ݰ�����ID[" + packageID + "]");
				}
				// ���ݰ�û�б�����
				this.releaseDataFragment(received);
				this.postBreakSendPackageCtrl(packageID);
				return;
			}
			synchronized (this.inLock) {
				this.receivingPackages.put(packageID, rpe);
			}
		}
		// ��ӵ�resolve����
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
	 * ��������ϣ����շ���ԭ���ĳ����ط�
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
	 * �ظ����ݰ���ԭ�����Ϣ
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
				// �������ݰ����͹���
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
						// ȡ���Ͽ�
						break;
					}
					this.setConnStateNoSync(ConnectionState.CLOSE_WAIT);
				case CLOSE_WAIT:
					// �Ͽ�����
					this.conn.disconnect();
					return;
				}
			}
		}
		// ȡ���Ͽ�
		DataFragment fragment = this.allocDataFragment(1);
		fragment.writeByte((byte) (CTRL_FLAG_CLOSE | CTRL_FLAG_CLOSE_CANCEL));
		this.postDataFragmentToSend(null, fragment);
	}

	private final void onCloseCancelFragment(DataFragment received) {
		// ����
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
	 * У��ack������ack�������Ƴ�
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
					// �Ƴ�
					if (arr == null) {
						arr = new ArrayList<IAckRequiredFragmentBuilder>();
					}
					arr.add(this.waitingAckFragments.poll().builder);
					if (NetDebug.TRACE_FAULT()) {
						this.trace("����ͨ�ţ�Ƭ��[" + stub.ackID + "]û���ʹ���·���");
					}
					continue;
				} else if (stub.ackID == ackID) {
					// �Ƴ�
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
			this.trace("����ͨ�ţ��жϽ������ݰ�[" + packageID + "]");
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
			this.trace("����ͨ�ţ��жϷ������ݰ�[" + packageID + "]");
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
	 * ������յ�Ƭ�Σ������ػ������ɷ�����
	 * 
	 * @param received
	 * @return
	 * @throws Throwable
	 */
	protected final boolean dispatchFragment(DataFragment received)
			throws Throwable {
		if (NetDebug.TRACE_IO()) {
			this.trace("����ͨ�ţ��յ����ݰ�[" + NetDebug.dataToStr(received) + "]");
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
			throw new IllegalStateException("�޷�ʶ�����������");
		}
		return false;
	}

	/**
	 * �����߳���ȡƬ��������<br>
	 * ������Ҫ���͵�Ƭ�ϣ�<br>
	 * �������null��ʾû����Ҫ���͵�Ƭ�ϣ�<br>
	 * ��ʱ�����߳̿��Իع����
	 * 
	 * @return ������Ҫ���͵�Ƭ�ϻ�null
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

	// //////////////�����߳����////////////////////////
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