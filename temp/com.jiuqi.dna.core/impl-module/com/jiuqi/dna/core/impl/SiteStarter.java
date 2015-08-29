package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.DataPackageReceiver.NetPackageReceivingStarter;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingThread;
import com.jiuqi.dna.core.type.GUID;

/**
 * 站点启动器
 * <p>
 * 负责协调加入集群和站点锁。
 * </p>
 * 站点锁<br/>
 * <p>
 * 站点锁是一种读写锁的变相实现。主要用于控制本地事务产生。<br/>
 * </p>
 * <p>
 * 站点锁有以下特点：
 * <ul>
 * <li>站点锁包含本地事务请求计数(transLock)和本地同步请求计数(syncLock)两个状态</li>
 * <li>提供三种锁请求：事务请求，异步事务请求和同步请求，前两种请求累计到transLock，后一种请求累计到syncLock，
 * 属性remoteLockOwner表示非本地发起的同步请求的发起者。</li>
 * <li>事务请求和同步请求互斥，当syncLock>0时阻塞事务请求，反之，当transLock>0时阻塞同步请求。</li>
 * <li>异步事务请求和同步请求互斥，但是异步事务请求具有优先权，即在同步请求处于等待状态(transLock>0)时，不阻塞异步事务请求。
 * 这样可以防止异步调用发生死锁。</li>
 * <li>同一个发起者发起的同步请求是并发的，不同发起者发起的同步请求是互斥的。</li>
 * </ul>
 * </p>
 * 
 * @author niuhaifeng
 * 
 */
class SiteStarter {
	/**
	 * 状态：初始
	 */
	final static byte STATE_BOOT = 0;
	/**
	 * 状态：发出启动请求并等待回复
	 */
	final static byte STATE_REQUEST = 1;
	/**
	 * 状态：初始化资源
	 */
	final static byte STATE_INIT_RESOURCE = 2;
	/**
	 * 状态：初始化服务
	 */
	final static byte STATE_INIT_SERVICE = 3;
	/**
	 * 状态：完成
	 */
	final static byte STATE_FINISH = 4;
	/**
	 * 状态：销毁
	 */
	final static byte STATE_DISPOSE = 5;
	/**
	 * 状态：发生错误
	 */
	final static byte STATE_ERROR = -1;

	/**
	 * 消息：请求加入集群
	 */
	final static byte MSG_REQUEST = 0x01;
	/**
	 * 返回：等待（直到收到MSG_START）
	 */
	final static byte RETURN_WAIT = 0x02;
	/**
	 * 返回：允许启动
	 */
	final static byte RETURN_START = 0x03;
	/**
	 * 消息：启动完成
	 */
	final static byte MSG_READY = 0x05;

	/**
	 * 消息：锁定站点
	 */
	final static byte MSG_LOCK = 0x06;
	/**
	 * 返回：完成锁定站点
	 */
	final static byte RETURN_LOCKED = 0x07;
	/**
	 * 消息：解除站点锁定
	 */
	final static byte MSG_UNLOCK = 0x08;
	/**
	 * 消息：重启
	 */
	final static byte MSG_RESTART = 0x09;
	/**
	 * 消息：关闭
	 */
	final static byte MSG_SHUTDOWN = 0x0a;

	private final Object syncObj = new Object();
	private final Site site;
	/**
	 * 记录请求回复状况的mask
	 */
	private volatile int clusterMask;
	private volatile byte state;
	/**
	 * 锁版本
	 */
	private volatile int lockVer;
	/**
	 * 指示先于当前节点启动的某一个节点，用于从该节点上同步资源
	 */
	private NetNodeImpl source;

	public SiteStarter(Site site) {
		this.site = site;
	}

	// ======================== 启动 ===========================

	/**
	 * 加入集群
	 */
	final void start() {
		WAIT: synchronized (this.syncObj) {
			switch (this.state) {
			case STATE_BOOT:
				this.setState(STATE_REQUEST);
				// 广播MSG_REQUEST消息
				this.broadcastToAll(new SiteMessage() {
					@Override
					void build(DataOutputFragment fragment,
							NetNodeImpl attachment) {
						fragment.writeByte(MSG_REQUEST);
					}

					@Override
					public void onFragmentOutError(NetNodeImpl attachment) {
						SiteStarter.this.onReturnStart(attachment);
					}
				});
				if (this.clusterMask == -1) {
					this.setState(STATE_INIT_RESOURCE);
					break WAIT;
				}
				break;
			default:
				throw illegalState(this.state);
			}
			// 等待
			for (;;) {
				switch (this.state) {
				case STATE_REQUEST:
					try {
						this.syncObj.wait();
					} catch (InterruptedException e) {
						throw Utils.tryThrowException(e);
					}
					continue;
				case STATE_INIT_RESOURCE:
					break;
				default:
					throw illegalState(this.state);
				}
				break;
			}
		}
		if (this.site.shared) {
			NetClusterImpl c = this.site.getNetCluster();
			synchronized (c) {
				for (NetNodeImpl n = c.getFirstNetNode(); n != null; n = n.getNextNodeInCluster()) {
					if (n.getState() == NetNodeImpl.STATE_READY) {
						this.source = n;
						break;
					}
				}
			}
		}
		this.site.application.isFirstInCluster = this.source == null;
	}

	/**
	 * 加载资源
	 */
	final void loadResources() {
		synchronized (this.syncObj) {
			if (this.state != STATE_INIT_RESOURCE) {
				throw illegalState(this.state);
			}
		}
		final Transaction transaction = this.site.newTransaction(TransactionKind.CACHE_INIT, null);
		transaction.bindCurrentThread();
		boolean commit = true;
		try {
			if (this.source == null) {
				// 本地是第一个启动的节点，初始化资源定义
				this.site.cache.initializeCache(null, transaction);
			} else {
				// 集群中已经有节点启动，从source同步集群资源
				NetTaskRequestImpl<SiteRequestSynchronizeTask, None> req = this.source.newSession(this.site).newRemoteTransactionRequest(new SiteRequestSynchronizeTask(), None.NONE, transaction);
				try {
					req.internalWaitStop(0);
				} catch (InterruptedException e) {
					throw Utils.tryThrowException(e);
				}
				switch (req.getState()) {
				case FINISHED:
					break;
				default:
					if (req.getException() != null) {
						throw req.getException();
					}
					throw new IllegalStateException("同步站点资源时发生错误");
				}
			}
		} catch (Throwable e) {
			commit = false;
			synchronized (this.syncObj) {
				this.setState(STATE_ERROR);
			}
			String msg = "系统：启动时发生异常，启动失败。";
			Logger logger = DNALogManager.getLogger("core/system");
			logger.logFatal(null, msg, e, false);
			System.err.println(msg);
			e.printStackTrace();
			System.exit(-1);
		} finally {
			this.source = null;
			try {
				transaction.finish(commit);
			} finally {
				transaction.dispose();
			}
		}
		synchronized (this.syncObj) {
			this.setState(STATE_INIT_SERVICE);
		}
	}

	/**
	 * 完成启动
	 */
	final void finish() {
		// 资源加载完毕，设置状态
		synchronized (this.syncObj) {
			switch (this.state) {
			case STATE_INIT_SERVICE:
				this.setState(STATE_FINISH);
				break;
			default:
				throw illegalState(this.state);
			}
			// 广播MSG_START
			this.broadcastToAll(new SiteMessage() {
				@Override
				void build(DataOutputFragment fragment, NetNodeImpl attachment) {
					fragment.writeByte(MSG_READY);
				}
			});
		}
	}

	// ===================== 锁 ========================

	private final static byte LOCK_NONE = 0;
	private final static byte LOCK_REQUEST = 1;
	private final static byte LOCK_ACQUIRED = 2;
	private final static byte LOCK_REMOTE = 3;
	private volatile int transLock;
	private volatile byte syncLock; // 同步锁的状态
	/**
	 * 远程锁的所有者
	 */
	private volatile NetNodeImpl remoteLockOwner;

	final void shutdown(ContextImpl<?, ?, ?> context, final boolean restart)
			throws Throwable {
		synchronized (this.syncObj) {
			if (this.state != STATE_FINISH) {
				throw new IllegalStateException("系统：启动未完成，不能重启");
			}
		}
		try {
			boolean single = true;
			if (this.site.shared) {
				single = this.broadcastToReady(new SiteMessage() {
					@Override
					void build(DataOutputFragment fragment,
							NetNodeImpl attachment) {
						fragment.writeByte(restart ? MSG_RESTART : MSG_SHUTDOWN);
					}

					@Override
					public void onFragmentOutError(NetNodeImpl attachment) {
						this.setFlag(attachment);
					}

					@Override
					public void onFragmentOutFinished(NetNodeImpl attachment) {
						this.setFlag(attachment);
					}

					private void setFlag(NetNodeImpl source) {
						synchronized (SiteStarter.this.syncObj) {
							switch (SiteStarter.this.state) {
							case STATE_FINISH:
								SiteStarter.this.clusterMask |= 1 << source.channel.getRemoteNodeIndex();
								if (SiteStarter.this.clusterMask == -1) {
									SiteStarter.this.setState(STATE_DISPOSE);
								}
								break;
							}
						}
					}
				});
			}
			synchronized (this.syncObj) {
				if (single) {
					this.setState(STATE_DISPOSE);
				} else {
					for (;;) {
						switch (this.state) {
						case STATE_FINISH:
							this.syncObj.wait();
							continue;
						case STATE_DISPOSE:
							break;
						default:
							throw new IllegalStateException();
						}
						break;
					}
				}
			}
		} finally {
			this.site.application.shutdown(context, restart);
		}
	}

	final void transLock(TransactionKind kind) {
		synchronized (this.syncObj) {
			for (;;) {
				if (this.state == STATE_ERROR) {
					throw new UnsupportedOperationException("系统：启动失败，无法执行业务操作。");
				}
				switch (kind) {
				case SYSTEM_INIT:
					// 站点启动完成后不允许构造初始化事务
					if (this.state == STATE_FINISH) {
						throw new IllegalStateException();
					}
					this.transLock++;
					return;
				case NORMAL:
				case SIMULATION:
					// 资源初始化完成前阻塞常规事务
					if (this.state > STATE_INIT_RESOURCE && this.syncLock == LOCK_NONE) {
						this.transLock++;
						return;
					}
					break;
				case CACHE_INIT:
				case TRANSIENT:
					// 资源初始化完成前阻塞异步调用/远程调用事务
					// 请求异步调用/远程调用事务，具有优先权
					if (this.state >= STATE_INIT_RESOURCE && (this.syncLock == LOCK_NONE || this.transLock > 0)) {
						this.transLock++;
						return;
					}
					break;
				case REMOTE:
					// 站点启动完成前阻塞远程事务
					if (this.state > STATE_INIT_RESOURCE) {
						return;
					}
					break;
				default:
					throw new IllegalStateException();
				}
				try {
					this.syncObj.wait();
				} catch (InterruptedException e) {
					throw Utils.tryThrowException(e);
				}
			}
		}
	}

	final void releaseTransLock(TransactionKind kind) {
		switch (kind) {
		case SYSTEM_INIT:
		case CACHE_INIT:
		case SIMULATION:
		case NORMAL:
		case TRANSIENT:
			synchronized (this.syncObj) {
				this.transLock--;
				if (this.transLock == 0 && this.syncLock != LOCK_NONE) {
					if (this.remoteLockOwner != null) {
						this.responseRemoteLock();
					} else {
						this.syncObj.notifyAll();
					}
				}
			}
			break;
		case REMOTE:
			break;
		default:
			throw new IllegalStateException();
		}
	}

	final int getLockVer() {
		return this.lockVer;
	}

	final void putLock(NetNodeImpl promoter, int lockVer) {
		synchronized (this.syncObj) {
			if (this.syncLock != LOCK_NONE) {
				throw new IllegalStateException();
			}
			this.syncLock = LOCK_REMOTE;
			this.lockVer = lockVer;
			this.remoteLockOwner = promoter;
			this.syncObj.notifyAll();
		}
	}

	final void syncLock() {
		for (;;) {
			final int lockVer;
			synchronized (this.syncObj) {
				switch (this.state) {
				case STATE_FINISH:
					for (;;) {
						switch (this.syncLock) {
						case LOCK_NONE:
							this.syncLock = LOCK_REQUEST;
							this.syncObj.notifyAll();
							break;
						case LOCK_ACQUIRED:
						case LOCK_REQUEST:
						case LOCK_REMOTE:
							try {
								this.syncObj.wait();
							} catch (InterruptedException e) {
								throw Utils.tryThrowException(e);
							}
							continue;
						default:
							throw new IllegalStateException();
						}
						break;
					}
					lockVer = this.lockVer;
					break;
				case STATE_ERROR:
					throw new IllegalStateException("启动失败");
				default:
					throw new IllegalStateException("启动过程中不能加锁");
				}
			}
			boolean single = this.broadcastToReady(new SiteMessage() {
				@Override
				void build(DataOutputFragment fragment, NetNodeImpl attachment) {
					fragment.writeByte(MSG_LOCK);
					fragment.writeInt(lockVer);
				}

				@Override
				public void onFragmentOutError(NetNodeImpl attachment) {
					SiteStarter.this.onReturnLocked(attachment, lockVer);
				}
			});
			synchronized (this.syncObj) {
				// 等待远程加锁成功
				if (single) {
					this.syncLock = LOCK_ACQUIRED;
				}
				for (;;) {
					if (this.lockVer != lockVer) {
						break;
					}
					switch (this.syncLock) {
					case LOCK_NONE:
					case LOCK_REMOTE:
						break;
					case LOCK_ACQUIRED:
						// 等待本地加锁成功
						while (this.transLock > 0) {// 无事务锁、异步事务锁和远程发起的同步锁
							try {
								this.syncObj.wait();
							} catch (InterruptedException e) {
								throw Utils.tryThrowException(e);
							}
						}
						return;
					case LOCK_REQUEST:
						try {
							this.syncObj.wait();
						} catch (InterruptedException e) {
							throw Utils.tryThrowException(e);
						}
						continue;
					default:
						throw new IllegalStateException();
					}
					break;
				}
			}
		}
	}

	final void releaseSyncLock() {
		final int lockVer;
		synchronized (this.syncObj) {
			switch (this.syncLock) {
			case LOCK_ACQUIRED:
				this.syncLock = LOCK_NONE;
				break;
			default:
				throw new IllegalStateException();
			}
			lockVer = this.lockVer++;
			this.syncObj.notifyAll();
		}
		this.broadcastToReady(new SiteMessage() {
			@Override
			void build(DataOutputFragment fragment, NetNodeImpl attachment) {
				fragment.writeByte(MSG_UNLOCK);
				fragment.writeInt(lockVer);
			}
		});
	}

	private final void remoteSyncLock(NetNodeImpl promoter, int ver) {
		if (promoter == null) {
			throw new NullArgumentException("promoter");
		}
		synchronized (this.syncObj) {
			if (ver < this.lockVer) {
				return;
			}
			if (ver > this.lockVer) {
				this.remoteReleaseSyncLock(promoter, -1);
				this.lockVer = ver;
			}
			switch (this.syncLock) {
			case LOCK_NONE:
				this.remoteLockOwner = promoter;
				this.syncLock = LOCK_REMOTE;
				this.syncObj.notifyAll();
				break;
			case LOCK_REQUEST:
				// 本地有同步请求
				if (this.site.getNetCluster().thisClusterNodeIndex < promoter.channel.getRemoteNodeIndex()) {
					// 远程优先
					this.remoteLockOwner = promoter;
					this.syncLock = LOCK_REMOTE;
					this.syncObj.notifyAll();
					break;
				}
				return;
			case LOCK_REMOTE:
				// 远程有同步请求
				if (this.remoteLockOwner.channel.getRemoteNodeIndex() < promoter.channel.getRemoteNodeIndex()) {
					// 远程节点竞争成功
					this.remoteLockOwner = promoter;
					break;
				}
				return;
			default:
				throw new IllegalStateException();
			}
			if (this.transLock == 0) {
				this.responseRemoteLock();
			}
		}
	}

	private final void remoteReleaseSyncLock(NetNodeImpl promoter, int ver) {
		if (promoter == null) {
			throw new NullArgumentException("promoter");
		}
		synchronized (this.syncObj) {
			if (this.syncLock != LOCK_REMOTE) {
				return;
			}
			if (ver > this.lockVer) {
				this.lockVer = ver;
			} else if (ver < this.lockVer && ver != -1 || this.remoteLockOwner != promoter) {
				return;
			}
			this.syncLock = LOCK_NONE;
			this.remoteLockOwner = null;
			this.syncObj.notifyAll();
			this.lockVer++;
		}
	}

	final void remoteReleaseSyncLockNoCheck(NetNodeImpl promoter) {
		this.remoteReleaseSyncLock(promoter, -1);
	}

	private final void responseRemoteLock() {
		this.remoteLockOwner.channel.startSendingPackage(new SiteMessage() {
			@Override
			void build(DataOutputFragment fragment, NetNodeImpl attachment) {
				fragment.writeByte(RETURN_LOCKED);
				fragment.writeInt(SiteStarter.this.lockVer);
			}
		}, this.remoteLockOwner);
	}

	private final void onReturnLocked(NetNodeImpl source, int ver) {
		synchronized (this.syncObj) {
			if (ver != this.lockVer) {
				return;
			}
			switch (this.state) {
			case STATE_FINISH:
				switch (this.syncLock) {
				case LOCK_REQUEST:
					this.clusterMask |= 1 << source.channel.getRemoteNodeIndex();
					if (this.clusterMask == -1) {
						this.syncLock = LOCK_ACQUIRED;
						this.syncObj.notifyAll();
					}
					break;
				default:
					return;
				}
				break;
			}
		}
	}

	private final static IllegalStateException illegalState(byte state) {
		return new IllegalStateException("无效状态:" + state);
	}

	private final void setState(byte state) {
		this.state = state;
		this.syncObj.notifyAll();
	}

	private final void broadcastToAll(SiteMessage m) {
		if (!this.site.shared) {
			this.clusterMask = -1;
			return;
		}
		NetClusterImpl c = this.site.getNetCluster();
		synchronized (c) {
			int mask = -1;
			NetNodeImpl node = c.getFirstNetNode();
			while (node != null) {
				mask ^= 1 << node.channel.getRemoteNodeIndex();
				node = node.getNextNodeInCluster();
			}
			this.clusterMask = mask;
			node = c.getFirstNetNode();
			while (node != null) {
				node.channel.startSendingPackage(m, node);
				node = node.getNextNodeInCluster();
			}
		}
	}

	private final boolean broadcastToReady(SiteMessage m) {
		if (!this.site.shared) {
			this.clusterMask = -1;
			return true;
		}
		NetClusterImpl c = this.site.getNetCluster();
		synchronized (c) {
			int mask = -1;
			NetNodeImpl node = c.getFirstNetNode();
			while (node != null) {
				if (node.getState() == NetNodeImpl.STATE_READY) {
					mask ^= 1 << node.channel.getRemoteNodeIndex();
				}
				node = node.getNextNodeInCluster();
			}
			this.clusterMask = mask;
			if (mask == -1) {
				return true;
			}
			node = c.getFirstNetNode();
			while (node != null) {
				if (node.getState() == NetNodeImpl.STATE_READY) {
					node.channel.startSendingPackage(m, node);
				}
				node = node.getNextNodeInCluster();
			}
		}
		return false;
	}

	private final void onMessage(NetNodeImpl source, DataInputFragment fragment) {
		byte ctrlFlag = fragment.readByte();
		switch (ctrlFlag) {
		case MSG_REQUEST:
			this.onRequest(source);
			break;
		case RETURN_START:
			this.onReturnStart(source);
			break;
		case RETURN_WAIT:
			break;
		case MSG_READY:
			synchronized (source.cluster) {
				source.setState(NetNodeImpl.STATE_READY);
			}
			this.onReady(source);
			break;
		case MSG_LOCK:
			this.remoteSyncLock(source, fragment.readInt());
			break;
		case MSG_UNLOCK:
			this.remoteReleaseSyncLock(source, fragment.readInt());
			break;
		case RETURN_LOCKED:
			this.onReturnLocked(source, fragment.readInt());
			break;
		case MSG_RESTART:
			this.onShutdown(true);
			break;
		case MSG_SHUTDOWN:
			this.onShutdown(false);
			break;
		default:
			throw illegalState(ctrlFlag);
		}
	}

	private final void onRequest(NetNodeImpl source) {
		final byte result;
		synchronized (this.syncObj) {
			switch (this.state) {
			case STATE_BOOT:
				result = RETURN_START;
				break;
			case STATE_REQUEST:
				// 比较nodeID
				if (this.site.application.localNodeID.compareTo(source.channel.getRemoteNodeID()) > 0) {
					// 本地节点大，发送RETURN_WAIT
					result = RETURN_WAIT;
				} else {
					result = RETURN_START;
					// 清除本地标记
					this.clusterMask &= ~(1 << source.channel.getRemoteNodeIndex());
				}
				break;
			case STATE_INIT_RESOURCE:
			case STATE_INIT_SERVICE:
				result = RETURN_WAIT;
				break;
			case STATE_FINISH:
				result = MSG_READY;
				break;
			case STATE_ERROR:
				result = RETURN_START;
				break;
			default:
				throw illegalState(this.state);
			}
		}
		source.channel.startSendingPackage(new SiteMessage() {
			@Override
			void build(DataOutputFragment fragment, NetNodeImpl attachment) {
				fragment.writeByte(result);
			}
		}, source);
	}

	private final void onReturnStart(NetNodeImpl source) {
		synchronized (this.syncObj) {
			switch (this.state) {
			case STATE_REQUEST:
				this.clusterMask |= 1 << source.channel.getRemoteNodeIndex();
				if (this.clusterMask == -1) {
					this.setState(STATE_INIT_RESOURCE);
				}
				break;
			}
		}
	}

	private final void onReady(NetNodeImpl source) {
		synchronized (this.syncObj) {
			switch (this.state) {
			case STATE_BOOT:
				// 忽略此消息
				break;
			case STATE_REQUEST:
				// 启动，并从source同步资源
				this.setState(STATE_INIT_RESOURCE);
				break;
			case STATE_INIT_RESOURCE:
			case STATE_INIT_SERVICE:
			case STATE_FINISH:
			case STATE_DISPOSE:
			case STATE_ERROR:
				break;
			default:
				throw illegalState(this.state);
			}
		}
	}

	private final void onShutdown(final boolean restart) {
		synchronized (this.syncObj) {
			switch (this.state) {
			case STATE_BOOT:
			case STATE_REQUEST:
			case STATE_INIT_RESOURCE:
			case STATE_INIT_SERVICE:
			case STATE_ERROR:
				return;
			case STATE_FINISH:
				this.setState(STATE_DISPOSE);
				break;
			default:
				throw illegalState(this.state);
			}
		}
		this.site.application.overlappedManager.postWork(new Work() {
			@Override
			protected void workDoing(WorkingThread thread) throws Throwable {
				SessionImpl session = SiteStarter.this.site.application.sessionManager.newSession(SessionKind.TRANSIENT, User.anonym, null, null);
				ContextImpl<?, ?, ?> context = session.newContext(ContextKind.DISPOSER);
				try {
					SiteStarter.this.site.application.shutdown(context, restart);
				} finally {
					context.dispose();
				}
			}
		});
	}

	private abstract class SiteMessage implements
			DataFragmentBuilder<NetNodeImpl> {
		public boolean buildFragment(DataOutputFragment fragment,
				NetNodeImpl attachment) throws Throwable {
			fragment.writeByte(INetPackageSign.SITE_PACKAGE);
			fragment.writeLong(SiteStarter.this.site.id.getMostSigBits());
			fragment.writeLong(SiteStarter.this.site.id.getLeastSigBits());
			this.build(fragment, attachment);
			return true;
		}

		abstract void build(DataOutputFragment fragment, NetNodeImpl attachment);

		public void onFragmentOutError(NetNodeImpl attachment) {
		}

		public void onFragmentOutFinished(NetNodeImpl attachment) {
		}

		public boolean tryResetPackage(NetNodeImpl attachment) {
			return true;
		}
	}

	private static DataFragmentResolver<NetNodeImpl> resolver = new DataFragmentResolver<NetNodeImpl>() {
		public boolean resolveFragment(DataInputFragment fragment,
				NetNodeImpl attachment) throws Throwable {
			GUID siteID = GUID.valueOf(fragment.readLong(), fragment.readLong());
			Site site = attachment.owner.application.findSite(siteID);
			if (site != null && site.shared) {
				switch (site.state) {
				case INITING:
				case ACTIVE:
					site.starter.onMessage(attachment, fragment);
					return true;
				case LOADING_METADATA:
				case WAITING_LOAD_METADATA:
				case DISPOSING:
				case DISPOSED:
					break;
				}
			}
			return false;
		}

		public void onFragmentInFailed(NetNodeImpl attachment) throws Throwable {
		}
	};

	final static void startReceivingPackage(NetPackageReceivingStarter starter,
			NetNodeImpl source, DataInputFragment fragment) {
		starter.startReceivingPackage(resolver, source);
	}

	@Override
	public String toString() {
		String state;
		switch (this.state) {
		case STATE_BOOT:
			state = "BOOT";
			break;
		case STATE_ERROR:
			state = "ERROR";
			break;
		case STATE_FINISH:
			state = "FINISH";
			break;
		case STATE_INIT_RESOURCE:
			state = "INIT_RESOURCE";
			break;
		case STATE_INIT_SERVICE:
			state = "INIT_SERVICE";
			break;
		case STATE_REQUEST:
			state = "REQUEST";
			break;
		default:
			state = "INVALID";
			break;
		}
		return String.format("SiteStarter[state = %s, transLock = %d, siteLock = %d]", state, this.transLock, this.syncLock);
	}
}
