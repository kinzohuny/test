package com.jiuqi.dna.core.impl;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.SiteState;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.DataPackageReceiver.NetPackageReceivingStarter;
import com.jiuqi.dna.core.internal.db.datasource.DataSourceRef;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.service.AsyncInfo;
import com.jiuqi.dna.core.service.AsyncInfo.AwaitSchedule;
import com.jiuqi.dna.core.service.AsyncInfo.SessionMode;
import com.jiuqi.dna.core.spi.application.Application;
import com.jiuqi.dna.core.type.GUID;

/**
 * �������
 * <p>
 * ������������³�����ʹ�ã�
 * <ul>
 * <li>���ز���</li>
 * <li>Զ�̵���</li>
 * <li>��Ⱥ�����߲���</li>
 * </ul>
 * ǰ���ֳ����У����������ݷ����߽�ɫ����һ�ֳ����У����������ݲ����߽�ɫ�� ���ֽ�ɫ�ṩ�Ĳ����ǲ���ͬ�ģ���Ҫ�����ڣ�
 * <ul>
 * <li>������ʽ�������߶���Դ��L��������������G����ȫ�������������߶���Դ��R����Զ������</li>
 * <li>�ύ��ʽ���������ύ���ݿ����񣬲��ҿ��Ƽ�Ⱥ������ύ���̡�������ֻ�ύ�ڴ������ڼ�Ⱥ��������Ӧ�����ߵ���Ϣ��</li>
 * </ul>
 * </p>
 * 
 * @author gaojingxin
 * 
 */
public final class Transaction extends AcquirerHolder implements
		ITransactionMessage {

	public static final int INVALID_TRANSACTION_ID = 0;

	/**
	 * ״̬������
	 */
	final static byte STATE_READY = 0;
	/**
	 * ״̬�����ύ
	 */
	final static byte STATE_COMMIT = 1;
	/**
	 * ״̬���ѻع�
	 */
	final static byte STATE_ROLLBACK = 2;
	/**
	 * ״̬��׼���ύ
	 */
	final static byte STATE_PREPARED = 3;
	/**
	 * ״̬��������
	 */
	final static byte STATE_DISPOSED = 4;

	/**
	 * ����״̬����������
	 */
	final static byte ACTION_LOCAL = 0;
	/**
	 * ����״̬��ȫ������ֻ�������ز���
	 */
	final static byte ACTION_GLOBAL_STUB = 1;
	/**
	 * ����״̬��ȫ�����񣬰���ȫ�ֲ���
	 */
	final static byte ACTION_GLOBAL_COMMIT = 2;

	// private static final AtomicReferenceFieldUpdater<Transaction, Statement>
	// processingStatementSetter =
	// AtomicReferenceFieldUpdater.newUpdater(Transaction.class,
	// Statement.class, "processingStatement");

	private TransientContainer transientContainer;
	/**
	 * ���ݿ�������(��)
	 */
	private DBAdapterImpl lastDBAdapter;

	final Site site;
	public final int id;
	final NetNodeImpl ownerNode;
	final TransactionKind kind;
	/**
	 * ����ִ�е���䣬����ȡ��������ʱʹ�á�
	 */
	public volatile Statement processingStatement;
	private ContextImpl<?, ?, ?> currentContext;
	private Thread ownerThread;
	private volatile byte state = STATE_READY;
	private final Object lock = new Object();
	private volatile TransactionPackage waitingPackage;
	private byte actionState;

	private final Transaction parent;

	Transaction(Site site, int id, TransactionKind kind, NetNodeImpl ownerNode,
			final Transaction parent) {
		if (site == null) {
			throw new NullArgumentException("site");
		}
		if (id == INVALID_TRANSACTION_ID) {
			throw new IllegalArgumentException("id");
		}
		if (kind == null) {
			throw new IllegalArgumentException("kind");
		}
		this.site = site;
		this.id = id;
		this.kind = kind;
		this.ownerNode = ownerNode;
		this.parent = parent;
	}

	final void dispose() {
		this.unbindThread();
		synchronized (this.lock) {
			switch (this.state) {
			case STATE_DISPOSED:
				return;
			default:
				if (this.size > 0) {
					String msg = String.format("ϵͳ����ͼ����[δ�ύ]������[%x]��", this.id);
					Logger logger = DNALogManager.getLogger("core/system");
					logger.logFatal(null, msg, false);
				}
				break;
			}
			this.state = STATE_DISPOSED;
		}
		this.site.transactionDisposed(this);
		if (this.isLocal() && this.actionState >= ACTION_GLOBAL_STUB) {
			// �㲥MSG_DISPOSE
			this.broadcast(new TransactionPackage() {
				@Override
				void build(DataOutputFragment fragment, NetNodeImpl attachment)
						throws Throwable {
					fragment.writeByte(MSG_DISPOSE);
				}
			});
		}
	}

	final boolean disposed() {
		return this.state == STATE_DISPOSED;
	}

	@Override
	int getNodeIndex() {
		return this.ownerNode != null ? this.ownerNode.channel.getRemoteNodeIndex() : this.site.getNetCluster().thisClusterNodeIndex;
	}

	final boolean isLocal() {
		return this.ownerNode == null;
	}

	final void checkContextValid() {
		final ContextImpl<?, ?, ?> currentContext = this.currentContext;
		if (currentContext == null) {
			throw new IllegalStateException("������������Ѿ�����");
		}
		currentContext.checkValid();
	}

	final boolean isContextValid() {
		final ContextImpl<?, ?, ?> currentContext = this.currentContext;
		if (currentContext == null) {
			return false;
		}
		return currentContext.isValid();
	}

	final void bindCurrentThread() {
		this.bindThread(Thread.currentThread());
	}

	final void bindThread(Thread ownerThread) {
		this.ownerThread = ownerThread;
	}

	final void unbindThread() {
		this.ownerThread = null;
	}

	final boolean isOwnerThread(Thread thread) {
		return this.ownerThread == thread;
	}

	final ExceptionCatcher getExceptionCatcher() {
		return this.site.application.catcher;
	}

	final ContextImpl<?, ?, ?> getCurrentContext() {
		return this.currentContext;
	}

	public final SessionImpl getCurrentSession() {
		ContextImpl<?, ?, ?> context = this.currentContext;
		if (context != null) {
			return context.session;
		}
		return null;
	}

	final void bindContext(ContextImpl<?, ?, ?> currentContext) {
		if (currentContext == null) {
			throw new NullArgumentException("currentContext");
		}
		this.currentContext = currentContext;
		this.bindThread(Thread.currentThread());
	}

	final void unbindContext(boolean commit) {
		try {
			// �������������ύ����
			switch (this.kind) {
			case SYSTEM_INIT:
			case NORMAL:
			case SIMULATION:
			case TRANSIENT:
				try {
					this.finish(commit);
				} finally {
					this.dispose();
				}
				break;
			case CACHE_INIT:
			case REMOTE:
				break;
			}
		} finally {
			this.lastDBAdapter = null;
			this.currentContext = null;
		}
	}

	final void interupt() {
		// final Statement processingStatement = processingStatementSetter
		// .getAndSet(this, null);
		// if (processingStatement != null) {
		// try {
		// processingStatement.cancel();
		// } catch (Throwable e) {
		// this.getExceptionCatcher().catchException(e, this);
		// }
		// }
	}

	final TransientContainer getTransientContainer() {
		final TransientContainer container = this.transientContainer;
		if (container != null) {
			return container;
		}
		return this.transientContainer = new TransientContainer();
	}

	final void clearTransient(Object owner) {
		final TransientContainer container = this.transientContainer;
		if (container != null) {
			container.clear(owner);
		}
	}

	final void leaveFrame(int depth) {
		if (this.transientContainer != null) {
			this.transientContainer.leaveFrame(depth);
		}
	}

	private boolean usingDB;

	final void usingDataSource(DataSourceRef dataSourceRef) {
		if (dataSourceRef != null) {
			this.lastDBAdapter = new DBAdapterImpl(this, dataSourceRef, null);
		} else {
			this.lastDBAdapter = null;
		}
		this.usingDB = true;
	}

	final DBAdapterImpl getDBAdapter(DataSourceRef dataSourceRef)
			throws SQLException {
		if (this.kind == TransactionKind.SIMULATION) {
			if (!this.usingDB && this.parent != null) {
				return this.parent.getDBAdapter(dataSourceRef);
			}
			if (this.lastDBAdapter == null) {
				throw new MissingObjectException("����Դ������");
			}
			return this.lastDBAdapter;
		}
		if (this.parent == null) {
			DBAdapterImpl lastDBAdapter = this.lastDBAdapter;
			if (lastDBAdapter != null) {
				if (lastDBAdapter.dataSourceRef == dataSourceRef) {
					return lastDBAdapter;
				}
				return this.lastDBAdapter = lastDBAdapter.getOtherDBAdapter(dataSourceRef);
			} else {
				return this.lastDBAdapter = new DBAdapterImpl(this, dataSourceRef, null);
			}
		} else {
			return this.parent.getDBAdapter(dataSourceRef);
		}
	}

	final void postedModifiedValue(final Object modifiedValue) {
		if (this.size > 0) {
			Acquirer[] acquirers = this.acquirers;
			for (int i = 0, c = acquirers.length; i < c; i++) {
				for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
					if (a.acquirable instanceof CacheHolder<?, ?, ?>) {
						CacheHolder<?, ?, ?> item = (CacheHolder<?, ?, ?>) a.acquirable;
						if (item.tryGetModifyingValue(this) == modifiedValue) {
							item.tryPostModifiedValue(modifiedValue, this);
							return;
						}
					}
				}
			}
		}
		throw new CacheStateError();
	}

	final void markAsGlobal() {
		this.actionState = ACTION_GLOBAL_COMMIT;
	}

	/**
	 * �ύ/�ع����ݿ�����
	 */
	private void finishDBTrans(boolean commit) {
		// �ύ/�ع����ݿ�����
		final DBAdapterImpl lastDBAdapter = this.lastDBAdapter;
		if (lastDBAdapter != null) {
			lastDBAdapter.resetTrans(commit, false);
		}
	}

	/**
	 * �ύ/�ع��ڴ�����
	 */
	private void finishMemTrans(boolean commit) {
		// �ύ/�ع��ڴ�����
		if (this.size == 0) {
			return;
		}
		if (commit) {
			Acquirer[] acquirers = this.acquirers;
			for (int i = 0, c = acquirers.length; i < c; i++) {
				for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
					if (a.state != IAcquirerState.LOCK_LS) {
						a.acquirable.onTransactionCommit(this);
					}
				}
			}
		} else {
			Acquirer[] acquirers = this.acquirers;
			for (int i = 0, c = acquirers.length; i < c; i++) {
				for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
					if (a.state != IAcquirerState.LOCK_LS) {
						a.acquirable.onTransactionRollback(this);
					}
				}
			}
		}
	}

	final AcquirableAccessor accessor() {
		return this.site.cacheAccessor;
	}

	// ================== �����߽�ɫ ============================

	private final void lock(Acquirer handle, AcquireFor operation) {
		switch (operation) {
		case ADD:
			this.accessor().exclusiveOnNew(handle);
			break;
		case COMMIT:
		case MODIFY_ITEMS:
		case REMOVE:
			this.accessor().exclusive(handle);
			break;
		case MODIFY:
			this.accessor().upgradable(handle);
			break;
		case READ:
			this.accessor().shared(handle);
			break;
		}
	}

	final void handleAcquirable(Acquirable res, AcquireFor operation) {
		if (!this.isLocal()) {
			throw new UnsupportedOperationException("����������֧�ּӱ���������");
		}
		Acquirer handle = this.getAcquirer(res);
		if (handle == null) {
			handle = new AcquirableHandle(res, this, operation);
			// ������������Ҫ����handle������Ҫ�Ƚ�handle����������
			this.putAcquirer(handle);
			try {
				this.lock(handle, operation);
			} catch (Throwable e) {
				// ����ʧ�ܣ����������Ƴ�handle
				this.removeAcquirer(handle);
				throw Utils.tryThrowException(e);
			}
			if (res.needSynchronizeInCluster()) {
				this.actionState = ACTION_GLOBAL_COMMIT;
			}
		} else {
			this.putAcquirer(handle);
		}
	}

	final void releaseAcquirable(Acquirable res) {
		Acquirer a = this.getAcquirer(res);
		if (a != null) {
			this.accessor().release(a);
			this.removeAcquirer(a);
		}
	}

	final boolean handledCacheItemInCacheGroup(
			final CacheGroup<?, ?, ?> fixGroup) {
		if (this.size > 0) {
			Acquirer[] acquirers = this.acquirers;
			for (int i = 0, c = acquirers.length; i < c; i++) {
				for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
					if (a.acquirable instanceof CacheHolder<?, ?, ?>) {
						CacheHolder<?, ?, ?> item = (CacheHolder<?, ?, ?>) a.acquirable;
						if (item.ownGroup == fixGroup) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static class TaskHolder<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> {
		final TTask task;
		final TMethod method;

		public TaskHolder(TTask t, TMethod m) {
			this.task = t;
			this.method = m;
		}
	}

	private TaskHolder<?, ?> commitTask;
	private TaskHolder<?, ?> rollbackTask;

	final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> void setCommitTask(
			TTask task, TMethod method) {
		this.actionState = ACTION_GLOBAL_COMMIT;
		this.commitTask = new TaskHolder<TTask, TMethod>(task, method);
	}

	final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> void setRollbackTask(
			TTask task, TMethod method) {
		this.actionState = ACTION_GLOBAL_COMMIT;
		this.rollbackTask = new TaskHolder<TTask, TMethod>(task, method);
	}

	@SuppressWarnings("unchecked")
	private final void doSyncTask(boolean commit) {
		TaskHolder holder = commit ? this.commitTask : this.rollbackTask;
		if (holder == null || !this.site.shared) {
			return;
		}
		ArrayList<NetRequestImpl> reqs = new ArrayList<NetRequestImpl>();
		NetClusterImpl c = this.site.getNetCluster();
		synchronized (c) {
			NetNodeImpl node = c.getFirstNetNode();
			while (node != null) {
				if (node.getState() == NetNodeImpl.STATE_READY) {
					reqs.add(node.newSession(this.site).newRequest(holder.task, holder.method));
				}
				node = node.getNextNodeInCluster();
			}
		}
		// �ȴ���Ӧ
		try {
			ContextImpl.internalWaitFor(0L, null, reqs.toArray(new NetRequestImpl[reqs.size()]));
		} catch (InterruptedException e) {
			throw Utils.tryThrowException(e);
		}
		// ����Ƿ���ڴ���
		for (NetRequestImpl req : reqs) {
			Throwable e = req.getException();
			if (e != null) {
				String msg = "ϵͳ�������ύ�׶�Task���÷����쳣��";
				Logger logger = DNALogManager.getLogger("core/system");
				logger.logFatal(null, msg, e, false);
				if (Application.IN_DEBUG_MODE) {
					System.err.println(msg);
					e.printStackTrace();
				}
			}
		}
	}

	final void finish(boolean commit) {
		try {
			this.internalFinish(commit);
		} catch (Throwable e) {
			String msg = String.format("ϵͳ������[%x]�ύ�׶η����쳣��", this.id);
			Logger logger = DNALogManager.getLogger("core/system");
			logger.logFatal(null, msg, e, false);
		}
	}

	static final AsyncInfo AFTER_CONTEXT_SUCCESS = new AsyncInfo(AwaitSchedule.AFTER_CURRENT_CONTEXT_SUCCESS, SessionMode.INDIVIDUAL_ANONYMOUS);

	private final void internalFinish(boolean commit) {
		if (!this.isLocal()) {
			return;
		}
		switch (this.state) {
		case STATE_READY:
			break;
		case STATE_DISPOSED:
			throw new IllegalStateException(String.format("ϵͳ����ͼ�ύ[������]������[%x]��", this.id));
		default:
			throw new IllegalStateException();
		}
		// ׼���ύ
		if (commit) {
			commit = this.clusterPrepareCommit();
		}
		if (commit && ContextVariableIntl.ENABLE_CACHE_MODIFY_EVENT) {
			notify: {
				if (this.site.state != SiteState.ACTIVE) {
					break notify;
				}
				if (this.kind != TransactionKind.CACHE_INIT || this.directReplicate) {
					final CacheModifyingEvent event = new CacheModifyingEvent();
					try {
						this.collectModifiedData(event);
					} catch (Throwable e) {
						DNALogManager.getLogger("dna/dist/repl").logError(null, "�ռ�������ʱ�����쳣", e, false);
						break notify;
					}
					if (event.haveData) {
						if (this.directReplicate) {
							if (this.currentContext == null) {
								final ContextImpl<?, ?, ?> c = this.site.application.getSystemSession().newContext(false);
								try {
									c.handle(event);
								} finally {
									c.dispose();
								}
							} else {
								DNALogManager.getLogger("dna/dist/repl").logFatal(null, "ֱ�Ӹ��Ƶ��������������", false);
							}
						} else {
							if (this.currentContext != null) {
								this.currentContext.asyncHandle(event, AFTER_CONTEXT_SUCCESS);
							} else {
								DNALogManager.getLogger("dna/dist/repl").logFatal(null, "��ֱ�Ӹ��Ƶ�����û�а���������", false);
							}
						}
					}
				}
			}
		}
		this.state = STATE_PREPARED;
		// �ύ��������
		try {
			this.finishDBTrans(commit);
		} catch (Throwable e) {
			commit = false;
		}
		try {
			this.finishMemTrans(commit);
		} catch (Throwable e) {
			commit = false;
			// ��¼��־
			String msg = "ϵͳ���ڴ������ύʧ�ܡ�";
			Logger logger = DNALogManager.getLogger("core/system");
			logger.logDebug(null, msg, e, false);
		}
		this.state = commit ? STATE_COMMIT : STATE_ROLLBACK;
		try {
			// �ύ/�ع�Զ�����񣬹㲥MSG_COMMIT/MSG_ROLLBACK
			if (this.actionState == ACTION_GLOBAL_COMMIT) {
				final byte ctrlFlag = commit ? MSG_COMMIT : MSG_ROLLBACK;
				synchronized (this.lock) {
					this.broadcastAndWait(new TransactionPackage() {
						@Override
						public void build(DataOutputFragment fragment,
								NetNodeImpl attachment) throws Throwable {
							fragment.writeByte(ctrlFlag);
						}

						@Override
						public void onFragmentOutFinished(NetNodeImpl attachment) {
							Transaction.this.setClusterMask(attachment.channel.getRemoteNodeIndex());
						}

						@Override
						public void onFragmentOutError(NetNodeImpl attachment) {
							// ���Գ���Ľڵ�
							Transaction.this.setClusterMask(attachment.channel.getRemoteNodeIndex());
						}
					});
				}
				// ��������
				this.doSyncTask(commit);
			}
		} finally {
			// ����
			if (this.size > 0) {
				Acquirer[] acquirers = this.acquirers;
				for (int i = 0, c = acquirers.length; i < c; i++) {
					for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
						this.accessor().release(a);
					}
				}
				this.clearAcquirers();
			}
			// ��������״̬
			synchronized (this.lock) {
				switch (this.state) {
				case STATE_DISPOSED:
					break;
				default:
					this.state = STATE_READY;
					break;
				}
			}
			if (this.actionState == ACTION_GLOBAL_COMMIT) {
				this.actionState = ACTION_GLOBAL_STUB;
				// �㲥��������״̬��Ϣ
				synchronized (this.lock) {
					this.broadcastAndWait(new TransactionPackage() {
						@Override
						public void build(DataOutputFragment fragment,
								NetNodeImpl attachment) throws Throwable {
							fragment.writeByte(MSG_RESET);
						}

						@Override
						public void onFragmentOutFinished(NetNodeImpl attachment) {
							Transaction.this.setClusterMask(attachment.channel.getRemoteNodeIndex());
						}

						@Override
						public void onFragmentOutError(NetNodeImpl attachment) {
							// ���Գ���Ľڵ�
							Transaction.this.setClusterMask(attachment.channel.getRemoteNodeIndex());
						}
					});
				}
			}
		}
	}

	private final void collectModifiedData(CacheSynchronizeCollector collector) {

	}

	private final boolean clusterPrepareCommit() {
		if (this.size == 0 || !this.site.shared) {
			return true;
		}
		final NetClusterImpl c = this.site.getNetCluster();
		if (!c.haveRemoteNode()) {
			return true;
		}
		final CacheClusterSynchronizeTask transactionSynchronizeTask = new CacheClusterSynchronizeTask(this.kind == TransactionKind.CACHE_INIT);
		final QuirkCacheClusterDataCollectTask quirkCacheDataCollectTask = new QuirkCacheClusterDataCollectTask();
		final QuirkCacheResourceLogTask logTask = new QuirkCacheResourceLogTask();

		// �Ӷ�ռ�����ռ�Զ������
		final Acquirer[] acquirers = this.acquirers;
		for (int i = 0, l = acquirers.length; i < l; i++) {
			for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
				final Acquirable acquirable = a.acquirable;
				if (acquirable.isModifiableOnTransaction(this)) {
					this.accessor().exclusive(a);
					CacheClusterSynchronizeTask synchronizeTask = null;
					if (acquirable.needSynchronizeInCluster()) {
						synchronizeTask = transactionSynchronizeTask;
					} else {
						if (this.kind != TransactionKind.SYSTEM_INIT) {
							if (acquirable instanceof CacheHolder<?, ?, ?>) {
								if (this.kind != TransactionKind.CACHE_INIT) {
									CacheHolder<?, ?, ?> holder = (CacheHolder<?, ?, ?>) acquirable;
									if (holder.ownGroup.define.quirkMode) {
										synchronizeTask = quirkCacheDataCollectTask;
									}
								}
							}
							if (acquirable instanceof CacheGroup<?, ?, ?>) {
								CacheGroup<?, ?, ?> group = (CacheGroup<?, ?, ?>) acquirable;
								if (group.define.quirkMode) {
									synchronizeTask = quirkCacheDataCollectTask;
								}
							}
						}
					}
					if (synchronizeTask != null) {
						if (acquirable instanceof CacheHolder<?, ?, ?>) {
							((CacheHolder<?, ?, ?>) acquirable).collectModifiedHolderData(synchronizeTask, this);
							if (Cache.IN_DEBUG_MODE && this.kind != TransactionKind.CACHE_INIT && this.kind != TransactionKind.SYSTEM_INIT) {
								((CacheHolder<?, ?, ?>) acquirable).collectModifiedHolderData(logTask, this);
							}

						} else if (acquirable instanceof CacheGroup<?, ?, ?>) {
							((CacheGroup<?, ?, ?>) acquirable).collectModifiedGroupData(synchronizeTask);
						}
					}

				}
			}
		}

		if (Cache.IN_DEBUG_MODE) {
			// ��¼��Դ�޸���־
			this.site.application.contextLocal.get().asyncHandle(logTask);
		}

		//
		if (quirkCacheDataCollectTask.haveData) {
			QuirkCacheClusterNotifyTask task = new QuirkCacheClusterNotifyTask(quirkCacheDataCollectTask);
			try {
				synchronized (c) {
					NetNodeImpl node = c.getFirstNetNode();
					while (node != null) {
						if (node.getState() == NetNodeImpl.STATE_READY) {
							task.nodeList.add(node);
						}
						node = node.getNextNodeInCluster();
					}
				}
				this.site.application.contextLocal.get().handle(task);
			} catch (Throwable t) {
				String msg = "��¼Quirk����仯ʧ��";
				Logger logger = DNALogManager.getLogger("core/cluster/quirk");
				logger.logFatal(null, msg, t, false);
			}
		}

		//
		if (!transactionSynchronizeTask.haveData) {
			return true; // XXX��������Ƿ����������
		}

		// ����׼���ύָ��
		final ArrayList<NetRequestImpl> taskList = new ArrayList<NetRequestImpl>();
		synchronized (c) {
			NetNodeImpl node = c.getFirstNetNode();
			while (node != null) {
				if (node.getState() == NetNodeImpl.STATE_READY) {
					taskList.add(node.newSession(this.site).newRemoteTransactionRequest(transactionSynchronizeTask, None.NONE, this));
				}
				node = node.getNextNodeInCluster();
			}
		}
		// �ȴ���Ӧ
		try {
			for (NetRequestImpl req : taskList) {
				req.internalWaitStop(0);
			}
		} catch (InterruptedException e) {
			throw Utils.tryThrowException(e);
		}
		// ������Ӧ���
		for (NetRequestImpl asyncTask : taskList) {
			switch (asyncTask.getState()) {
			case FINISHED:
				continue;
			case CANCELED:
				this.getExceptionCatcher().catchException(new UnsupportedOperationException(), null);
			case ERROR:
				return false;
			}
		}
		return true;
	}

	private final void collectModifiedData(CacheModifyingEvent task) {
		if (this.size == 0) {
			return;
		}
		final Acquirer[] acquirers = this.acquirers;
		for (int i = 0, l = acquirers.length; i < l; i++) {
			for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
				final Acquirable acquirable = a.acquirable;
				if (acquirable.isModifiableOnTransaction(this)) {
					this.accessor().exclusive(a);
					if (acquirable instanceof CacheGroup) {
						CacheGroup<?, ?, ?> group = (CacheGroup<?, ?, ?>) acquirable;
						if (group.define.kind.inCluster) {
							group.collectModifiedGroupData(task);
						}
					} else if (acquirable instanceof CacheHolder<?, ?, ?>) {
						CacheHolder<?, ?, ?> holder = (CacheHolder<?, ?, ?>) acquirable;
						if (holder.ownGroup.define.kind.inCluster) {
							holder.collectModifiedHolderData(task, this);
						}
					}
				}
			}
		}
	}

	private final void setClusterMask(int index) {
		synchronized (this.lock) {
			if (this.waitingPackage == null) {
				return;
			}
			if (index > 0) {
				this.waitingPackage.clusterMask |= 1 << index;
			}
			if (this.waitingPackage.clusterMask == -1) {
				this.lock.notify();
			}
		}
	}

	private final void resetClusterMask() {
		synchronized (this.lock) {
			if (this.waitingPackage != null) {
				this.waitingPackage.clusterMask = -1;
				this.lock.notifyAll();
			}
		}
	}

	private final void broadcast(TransactionPackage p) {
		if (!this.site.shared) {
			return;
		}
		NetClusterImpl c = this.site.getNetCluster();
		synchronized (c) {
			NetNodeImpl node = c.getFirstNetNode();
			while (node != null) {
				if (node.getState() == NetNodeImpl.STATE_READY) {
					node.channel.startSendingPackage(p, node);
				}
				node = node.getNextNodeInCluster();
			}
		}
	}

	private final void broadcastAndWait(TransactionPackage p) {
		this.waitingPackage = p;
		try {
			if (!this.site.shared) {
				p.clusterMask = -1;
				return;
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
				p.clusterMask = mask;
				node = c.getFirstNetNode();
				while (node != null) {
					if (node.getState() == NetNodeImpl.STATE_READY) {
						node.channel.startSendingPackage(p, node);
					}
					node = node.getNextNodeInCluster();
				}
			}
			while (p.clusterMask != -1) {
				try {
					this.lock.wait();
				} catch (InterruptedException e) {
					throw Utils.tryThrowException(e);
				}
			}
		} finally {
			this.waitingPackage = null;
		}
	}

	private final TransactionPackage packageLookupResult() {
		final byte result;
		switch (this.state) {
		case STATE_COMMIT:
			result = RESULT_COMMIT;
			break;
		case STATE_READY:
		case STATE_ROLLBACK:
			result = RESULT_ROLLBACK;
			break;
		case STATE_PREPARED:
		case STATE_DISPOSED:
			result = RESULT_UNKNOWN;
			break;
		default:
			throw new IllegalStateException();
		}
		return new TransactionPackage() {
			@Override
			void build(DataOutputFragment fragment, NetNodeImpl attachment)
					throws Throwable {
				fragment.writeByte(MSG_LOOKUP_RESULT);
				fragment.writeByte(result);
			}
		};
	}

	private abstract class TransactionPackage implements
			DataFragmentBuilder<NetNodeImpl> {
		volatile int clusterMask;

		public boolean buildFragment(DataOutputFragment fragment,
				NetNodeImpl attachment) throws Throwable {
			fragment.writeByte(INetPackageSign.TRANSACTION_PACKAGE);
			// siteID
			GUID siteID = Transaction.this.site.id;
			fragment.writeLong(siteID.getMostSigBits());
			fragment.writeLong(siteID.getLeastSigBits());
			// transactionID
			fragment.writeInt(Transaction.this.id);
			this.build(fragment, attachment);
			return true;
		}

		abstract void build(DataOutputFragment fragment, NetNodeImpl attachment)
				throws Throwable;

		public void onFragmentOutError(NetNodeImpl attachment) {
		}

		public void onFragmentOutFinished(NetNodeImpl attachment) {
		}

		public boolean tryResetPackage(NetNodeImpl attachment) {
			return true;
		}
	}

	// =================== ������ ==========================

	final void onNetNodeDisabled(NetNodeImpl node) {
		if (this.ownerNode != node) {
			return;
		}
		synchronized (this.lock) {
			try {
				switch (this.state) {
				case STATE_COMMIT:
				case STATE_ROLLBACK:
					this.broadcast(this.packageLookupResult());
					this.remoteReset();
					break;
				case STATE_READY:
					this.broadcast(this.packageLookupResult());
					this.remoteFinish(false);
					this.remoteReset();
					break;
				case STATE_PREPARED:
					this.broadcastAndWait(new TransactionPackage() {
						@Override
						void build(DataOutputFragment fragment,
								NetNodeImpl attachment) throws Throwable {
							fragment.writeByte(MSG_LOOKUP);
						}

						@Override
						public void onFragmentOutFinished(NetNodeImpl attachment) {
							Transaction.this.setClusterMask(attachment.channel.getRemoteNodeIndex());
						}

						@Override
						public void onFragmentOutError(NetNodeImpl attachment) {
							// ���Գ���Ľڵ�
							Transaction.this.setClusterMask(attachment.channel.getRemoteNodeIndex());
						}
					});
					if (this.state == STATE_PREPARED) {
						// �ع�
						this.remoteFinish(false);
					}
					this.remoteReset();
					break;
				case STATE_DISPOSED:
					return;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		this.dispose();
	}

	final void remoteHandleNewAcquirable(Acquirable res) {
		this.remoteHandleAcquirable(res, AcquireFor.ADD, 0);
	}

	private final void remoteHandleAcquirable(Acquirable res,
			AcquireFor operation, int clock) {
		synchronized (this.lock) {
			Acquirer handle = this.getAcquirer(res), oldHandle = handle;
			if (handle == null) {
				handle = new AcquirableHandle(res, this, operation);
			}
			switch (operation) {
			case ADD:
				this.accessor().remoteExclusiveOnNew(handle);
				break;
			case COMMIT:
			case MODIFY_ITEMS:
			case REMOVE:
				if (!this.accessor().remoteExclusive(handle, clock)) {
					return;
				}
				break;
			case MODIFY:
				if (!this.accessor().remoteUpgradable(handle, clock)) {
					return;
				}
				break;
			case READ:
				throw new UnsupportedOperationException("��Ⱥ������������֧�ֶ���");
			}
			if (oldHandle == null) {
				this.putAcquirer(handle);
			}
		}
	}

	final void remotePrepare(CacheClusterSynchronizeTask task) {
		ContextImpl<?, ?, ?> context = this.currentContext;
		if (context == null) {
			throw new IllegalStateException();
		}
		synchronized (this.lock) {
			switch (this.state) {
			case STATE_COMMIT:
			case STATE_ROLLBACK:
			case STATE_DISPOSED:
			case STATE_PREPARED:
				return;
			case STATE_READY:
				this.state = STATE_PREPARED;
				break;
			}
		}
		context.occorAt.site.cache.synchronizeModifiedData(task, this);
	}

	private final void remoteFinish(boolean commit) {
		synchronized (this.lock) {
			switch (this.state) {
			case STATE_PREPARED:
				break;
			default:
				return;
			}
			this.state = commit ? STATE_COMMIT : STATE_ROLLBACK;
			// �ύ/�ع��ڴ�����
			try {
				this.finishMemTrans(commit);
			} catch (Throwable e) {
				commit = false;
				// ��¼��־
				String msg = String.format("��Ⱥ��Զ������[%x]�ύʧ�ܡ�", this.id);
				Logger logger = DNALogManager.getLogger("core/cluster");
				logger.logFatal(null, msg, e, false);
				if (Application.IN_DEBUG_MODE) {
					System.err.println(msg);
					e.printStackTrace();
				}
			}
		}
	}

	private final static DataFragmentResolver<NetNodeImpl> resolver = new DataFragmentResolver<NetNodeImpl>() {
		public boolean resolveFragment(DataInputFragment fragment,
				NetNodeImpl attachment) throws Throwable {
			GUID siteID = GUID.valueOf(fragment.readLong(), fragment.readLong());
			Site site = attachment.owner.application.findSite(siteID);
			if (site == null) {
				return true;
			}
			site.getTransaction(fragment.readInt(), attachment).onMessage(fragment, attachment);
			return true;
		}

		public void onFragmentInFailed(NetNodeImpl attachment) throws Throwable {
		}

	};

	private void onMessage(DataInputFragment fragment, NetNodeImpl attachment) {
		int ctrlFlag = fragment.readByte();
		switch (ctrlFlag) {
		// ������Ϣ
		case MSG_COMMIT:
			this.remoteFinish(true);
			break;
		case MSG_ROLLBACK:
			this.remoteFinish(false);
			break;
		case MSG_LOOKUP:
			this.onPackageLookup(attachment);
			break;
		case MSG_LOOKUP_RESULT:
			this.onPackageLookupResult(fragment, attachment);
			break;
		case MSG_RESET:
			this.remoteReset();
			break;
		case MSG_DISPOSE:
			this.dispose();
			break;
		// ͬ����Ϣ
		case MSG_ACQUIRE:
			this.onPackageAcquire(fragment);
			break;
		case MSG_UPGRADE:
			this.onPackageUpgrade(fragment);
			break;
		case MSG_RELEASE:
			this.onPackageRelease(fragment);
			break;
		case MSG_ACQUIRE_RESULT:
			this.onPackageAcquireResult(fragment, attachment);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	private final void onPackageLookup(NetNodeImpl attachment) {
		attachment.channel.startSendingPackage(this.packageLookupResult(), attachment);
	}

	private final void onPackageLookupResult(DataInputFragment fragment,
			NetNodeImpl attachment) {
		synchronized (this.lock) {
			switch (this.state) {
			case STATE_READY:
			case STATE_COMMIT:
			case STATE_ROLLBACK:
			case STATE_DISPOSED:
				return;
			case STATE_PREPARED:
				break;
			}
			switch (fragment.readByte()) {
			case RESULT_UNKNOWN:
				this.setClusterMask(attachment.channel.getRemoteNodeIndex());
				break;
			case RESULT_COMMIT:
				this.remoteFinish(true);
				this.resetClusterMask();
				break;
			case RESULT_ROLLBACK:
				this.remoteFinish(false);
				this.resetClusterMask();
				break;
			}
		}
	}

	private final void remoteReset() {
		synchronized (this.lock) {
			if (this.size > 0) {
				Acquirer[] acquirers = this.acquirers;
				for (int i = 0, c = acquirers.length; i < c; i++) {
					for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
						this.accessor().remoteRelease(a, -1);
					}
				}
				this.clearAcquirers();
			}
			switch (this.state) {
			case STATE_DISPOSED:
				break;
			default:
				this.state = STATE_READY;
				break;
			}
		}
	}

	private final void onPackageAcquire(DataInputFragment fragment) {
		Acquirable a = this.readResource(fragment);
		int clock = fragment.readInt();
		AcquireFor op;
		byte opera = fragment.readByte();
		try {
			switch (opera) {
			case PARAM_METHOD_ADD:
				op = AcquireFor.ADD;
				break;
			case PARAM_METHOD_COMMIT:
				op = AcquireFor.COMMIT;
				break;
			case PARAM_METHOD_MODIFY:
				op = AcquireFor.MODIFY;
				break;
			case PARAM_METHOD_MODIFY_ITEMS:
				op = AcquireFor.MODIFY_ITEMS;
				break;
			case PARAM_METHOD_READ:
				op = AcquireFor.READ;
				break;
			case PARAM_METHOD_REMOVE:
				op = AcquireFor.REMOVE;
				break;
			default:
				throw new UnsupportedOperationException("��Ⱥ����֧�ֵļ�����ʽ:" + opera);
			}
			this.remoteHandleAcquirable(a, op, clock);
		} catch (Throwable e) {
			Logger logger = DNALogManager.getLogger("core/cluster");
			logger.logFatal(null, e, false);
			throw Utils.tryThrowException(e);
		}
	}

	private final Acquirable readResource(DataInputFragment fragment) {
		Acquirable a;
		byte type = fragment.readByte();
		try {
			switch (type) {
			case PARAM_TYPE_ITEM: {
				long id = fragment.readLong();
				a = this.site.cache.clusterHolderContainer.findHolder(id);
				if (a == null) {
					throw new IllegalArgumentException(String.format("��Ⱥ������[%x]�Ҳ�����Դ��[%x]", this.id, id));
				}
				break;
			}
			case PARAM_TYPE_GROUP: {
				long id = fragment.readLong();
				a = this.site.cache.clusterGroupContainer.findGroup(id);
				if (a == null) {
					throw new IllegalArgumentException(String.format("��Ⱥ������[%x]�Ҳ�����Դ��[%x]", this.id, id));
				}
				break;
			}
			case PARAM_TYPE_CACHE:
				a = this.site.cache;
				break;
			default:
				throw new UnsupportedOperationException("��Ⱥ����֧�ֵ���Դ����[" + type + "]");
			}
		} catch (Throwable e) {
			Logger logger = DNALogManager.getLogger("core/cluster");
			logger.logFatal(null, e, false);
			throw Utils.tryThrowException(e);
		}
		return a;
	}

	private final void onPackageUpgrade(DataInputFragment fragment) {
		synchronized (this.lock) {
			this.accessor().remoteExclusive(this.readHandle(fragment), fragment.readInt());
		}
	}

	private final void onPackageRelease(DataInputFragment fragment) {
		byte type = fragment.readByte();
		Acquirable acq;
		switch (type) {
		case PARAM_TYPE_ITEM:
			acq = this.site.cache.clusterHolderContainer.findHolder(fragment.readLong());
			break;
		case PARAM_TYPE_GROUP:
			acq = this.site.cache.clusterGroupContainer.findGroup(fragment.readLong());
			break;
		case PARAM_TYPE_CACHE:
			acq = this.site.cache;
			break;
		default:
			acq = null;
			Logger logger = DNALogManager.getLogger("core/cluster");
			logger.logFatal(null, "��Ⱥ����֧�ֵ���Դ����[" + type + "]", false);
			break;
		}
		if (acq != null) {
			synchronized (this.lock) {
				Acquirer a = this.getAcquirer(acq);
				int clock = fragment.readInt();
				if (a != null) {
					if (this.accessor().remoteRelease(a, clock)) {
						this.removeAcquirer(a);
					}
				} else {
					this.accessor().updateVer(acq, clock);
				}
			}
		}
	}

	final void onPackageAcquireResult(DataInputFragment fragment,
			NetNodeImpl attachment) {
		Acquirer a;
		synchronized (this.lock) {
			a = this.readHandle(fragment);
		}
		int clock = fragment.readInt();
		byte result = fragment.readByte();
		this.accessor().onAcquireResult(a, clock, result == RESULT_SUCCEED, attachment.channel.getRemoteNodeIndex());
	}

	private final Acquirer readHandle(DataInputFragment fragment) {
		try {
			byte type = fragment.readByte();
			switch (type) {
			case PARAM_TYPE_ITEM: {
				long id = fragment.readLong();
				if (this.size > 0) {
					final Acquirer[] acquirers = this.acquirers;
					for (int i = 0, c = acquirers.length; i < c; i++) {
						for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
							if (a.acquirable instanceof CacheHolder<?, ?, ?>) {
								if (((CacheHolder<?, ?, ?>) a.acquirable).longIdentifier == id) {
									return a;
								}
							}
						}
					}
				}
				throw new IllegalArgumentException(String.format("��Ⱥ������[%x]�в�������Դ��[%x]�ľ��", this.id, id));
			}
			case PARAM_TYPE_GROUP: {
				long id = fragment.readLong();
				if (this.size > 0) {
					final Acquirer[] acquirers = this.acquirers;
					for (int i = 0, c = acquirers.length; i < c; i++) {
						for (Acquirer a = acquirers[i]; a != null; a = a.nextInHolder) {
							if (a.acquirable instanceof CacheGroup<?, ?, ?>) {
								if (((CacheGroup<?, ?, ?>) a.acquirable).longIdentifier == id) {
									return a;
								}
							}
						}
					}
				}
				throw new IllegalArgumentException(String.format("��Ⱥ������[%x]�в�������Դ��[%x]�ľ��", this.id, id));
			}
			case PARAM_TYPE_CACHE: {
				Acquirer a = this.getAcquirer(this.site.cache);
				if (a == null) {
					throw new IllegalArgumentException(String.format("��Ⱥ������[%x]�в�������Դ�����ľ��", this.id));
				}
				return a;
			}
			default:
				throw new UnsupportedOperationException("��Ⱥ����֧�ֵ���Դ����[" + type + "]");
			}
		} catch (Throwable e) {
			Logger logger = DNALogManager.getLogger("core/cluster");
			logger.logFatal(null, e, false);
			if (Application.IN_DEBUG_MODE) {
				e.printStackTrace();
			}
			throw Utils.tryThrowException(e);
		}
	}

	final static void startReceivingPackage(NetPackageReceivingStarter starter,
			NetNodeImpl source, DataInputFragment fragment) {
		starter.startReceivingPackage(resolver, source);
	}

	@Override
	public String toString() {
		return String.format("Transaction[id=%x, kind=%s, promoter=%s]", this.id, this.kind, this.ownerNode);
	}

	// FIXME ֻ����ʱ����
	boolean directReplicate;
}