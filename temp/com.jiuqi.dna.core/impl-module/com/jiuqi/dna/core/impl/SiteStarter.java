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
 * վ��������
 * <p>
 * ����Э�����뼯Ⱥ��վ������
 * </p>
 * վ����<br/>
 * <p>
 * վ������һ�ֶ�д���ı���ʵ�֡���Ҫ���ڿ��Ʊ������������<br/>
 * </p>
 * <p>
 * վ�����������ص㣺
 * <ul>
 * <li>վ�����������������������(transLock)�ͱ���ͬ���������(syncLock)����״̬</li>
 * <li>�ṩ�������������������첽���������ͬ������ǰ���������ۼƵ�transLock����һ�������ۼƵ�syncLock��
 * ����remoteLockOwner��ʾ�Ǳ��ط����ͬ������ķ����ߡ�</li>
 * <li>���������ͬ�����󻥳⣬��syncLock>0ʱ�����������󣬷�֮����transLock>0ʱ����ͬ������</li>
 * <li>�첽���������ͬ�����󻥳⣬�����첽���������������Ȩ������ͬ�������ڵȴ�״̬(transLock>0)ʱ���������첽��������
 * �������Է�ֹ�첽���÷���������</li>
 * <li>ͬһ�������߷����ͬ�������ǲ����ģ���ͬ�����߷����ͬ�������ǻ���ġ�</li>
 * </ul>
 * </p>
 * 
 * @author niuhaifeng
 * 
 */
class SiteStarter {
	/**
	 * ״̬����ʼ
	 */
	final static byte STATE_BOOT = 0;
	/**
	 * ״̬�������������󲢵ȴ��ظ�
	 */
	final static byte STATE_REQUEST = 1;
	/**
	 * ״̬����ʼ����Դ
	 */
	final static byte STATE_INIT_RESOURCE = 2;
	/**
	 * ״̬����ʼ������
	 */
	final static byte STATE_INIT_SERVICE = 3;
	/**
	 * ״̬�����
	 */
	final static byte STATE_FINISH = 4;
	/**
	 * ״̬������
	 */
	final static byte STATE_DISPOSE = 5;
	/**
	 * ״̬����������
	 */
	final static byte STATE_ERROR = -1;

	/**
	 * ��Ϣ��������뼯Ⱥ
	 */
	final static byte MSG_REQUEST = 0x01;
	/**
	 * ���أ��ȴ���ֱ���յ�MSG_START��
	 */
	final static byte RETURN_WAIT = 0x02;
	/**
	 * ���أ���������
	 */
	final static byte RETURN_START = 0x03;
	/**
	 * ��Ϣ���������
	 */
	final static byte MSG_READY = 0x05;

	/**
	 * ��Ϣ������վ��
	 */
	final static byte MSG_LOCK = 0x06;
	/**
	 * ���أ��������վ��
	 */
	final static byte RETURN_LOCKED = 0x07;
	/**
	 * ��Ϣ�����վ������
	 */
	final static byte MSG_UNLOCK = 0x08;
	/**
	 * ��Ϣ������
	 */
	final static byte MSG_RESTART = 0x09;
	/**
	 * ��Ϣ���ر�
	 */
	final static byte MSG_SHUTDOWN = 0x0a;

	private final Object syncObj = new Object();
	private final Site site;
	/**
	 * ��¼����ظ�״����mask
	 */
	private volatile int clusterMask;
	private volatile byte state;
	/**
	 * ���汾
	 */
	private volatile int lockVer;
	/**
	 * ָʾ���ڵ�ǰ�ڵ�������ĳһ���ڵ㣬���ڴӸýڵ���ͬ����Դ
	 */
	private NetNodeImpl source;

	public SiteStarter(Site site) {
		this.site = site;
	}

	// ======================== ���� ===========================

	/**
	 * ���뼯Ⱥ
	 */
	final void start() {
		WAIT: synchronized (this.syncObj) {
			switch (this.state) {
			case STATE_BOOT:
				this.setState(STATE_REQUEST);
				// �㲥MSG_REQUEST��Ϣ
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
			// �ȴ�
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
	 * ������Դ
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
				// �����ǵ�һ�������Ľڵ㣬��ʼ����Դ����
				this.site.cache.initializeCache(null, transaction);
			} else {
				// ��Ⱥ���Ѿ��нڵ���������sourceͬ����Ⱥ��Դ
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
					throw new IllegalStateException("ͬ��վ����Դʱ��������");
				}
			}
		} catch (Throwable e) {
			commit = false;
			synchronized (this.syncObj) {
				this.setState(STATE_ERROR);
			}
			String msg = "ϵͳ������ʱ�����쳣������ʧ�ܡ�";
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
	 * �������
	 */
	final void finish() {
		// ��Դ������ϣ�����״̬
		synchronized (this.syncObj) {
			switch (this.state) {
			case STATE_INIT_SERVICE:
				this.setState(STATE_FINISH);
				break;
			default:
				throw illegalState(this.state);
			}
			// �㲥MSG_START
			this.broadcastToAll(new SiteMessage() {
				@Override
				void build(DataOutputFragment fragment, NetNodeImpl attachment) {
					fragment.writeByte(MSG_READY);
				}
			});
		}
	}

	// ===================== �� ========================

	private final static byte LOCK_NONE = 0;
	private final static byte LOCK_REQUEST = 1;
	private final static byte LOCK_ACQUIRED = 2;
	private final static byte LOCK_REMOTE = 3;
	private volatile int transLock;
	private volatile byte syncLock; // ͬ������״̬
	/**
	 * Զ������������
	 */
	private volatile NetNodeImpl remoteLockOwner;

	final void shutdown(ContextImpl<?, ?, ?> context, final boolean restart)
			throws Throwable {
		synchronized (this.syncObj) {
			if (this.state != STATE_FINISH) {
				throw new IllegalStateException("ϵͳ������δ��ɣ���������");
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
					throw new UnsupportedOperationException("ϵͳ������ʧ�ܣ��޷�ִ��ҵ�������");
				}
				switch (kind) {
				case SYSTEM_INIT:
					// վ��������ɺ��������ʼ������
					if (this.state == STATE_FINISH) {
						throw new IllegalStateException();
					}
					this.transLock++;
					return;
				case NORMAL:
				case SIMULATION:
					// ��Դ��ʼ�����ǰ������������
					if (this.state > STATE_INIT_RESOURCE && this.syncLock == LOCK_NONE) {
						this.transLock++;
						return;
					}
					break;
				case CACHE_INIT:
				case TRANSIENT:
					// ��Դ��ʼ�����ǰ�����첽����/Զ�̵�������
					// �����첽����/Զ�̵������񣬾�������Ȩ
					if (this.state >= STATE_INIT_RESOURCE && (this.syncLock == LOCK_NONE || this.transLock > 0)) {
						this.transLock++;
						return;
					}
					break;
				case REMOTE:
					// վ���������ǰ����Զ������
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
					throw new IllegalStateException("����ʧ��");
				default:
					throw new IllegalStateException("���������в��ܼ���");
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
				// �ȴ�Զ�̼����ɹ�
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
						// �ȴ����ؼ����ɹ�
						while (this.transLock > 0) {// �����������첽��������Զ�̷����ͬ����
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
				// ������ͬ������
				if (this.site.getNetCluster().thisClusterNodeIndex < promoter.channel.getRemoteNodeIndex()) {
					// Զ������
					this.remoteLockOwner = promoter;
					this.syncLock = LOCK_REMOTE;
					this.syncObj.notifyAll();
					break;
				}
				return;
			case LOCK_REMOTE:
				// Զ����ͬ������
				if (this.remoteLockOwner.channel.getRemoteNodeIndex() < promoter.channel.getRemoteNodeIndex()) {
					// Զ�̽ڵ㾺���ɹ�
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
		return new IllegalStateException("��Ч״̬:" + state);
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
				// �Ƚ�nodeID
				if (this.site.application.localNodeID.compareTo(source.channel.getRemoteNodeID()) > 0) {
					// ���ؽڵ�󣬷���RETURN_WAIT
					result = RETURN_WAIT;
				} else {
					result = RETURN_START;
					// ������ر��
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
				// ���Դ���Ϣ
				break;
			case STATE_REQUEST:
				// ����������sourceͬ����Դ
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
