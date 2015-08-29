package com.jiuqi.dna.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipFile;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.SiteState;
import com.jiuqi.dna.core.da.ORMAccessor;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.db.datasource.DataSourceRef;
import com.jiuqi.dna.core.internal.db.tool.IllegalBackupFileException;
import com.jiuqi.dna.core.license.LicenseEntry;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXElementBuilder;
import com.jiuqi.dna.core.spi.application.SessionDisposeEvent;
import com.jiuqi.dna.core.spi.metadata.LoadAllMetaDataTask;
import com.jiuqi.dna.core.spi.metadata.LoadMetaDataEvent;
import com.jiuqi.dna.core.type.GUID;

/**
 * վ��
 * 
 * @author gaojingxin
 * 
 */
final class Site extends Space {
	/**
	 * վ����ʹ�õ�bundles
	 */
	private final BundleStub[] bundles;
	/**
	 * ��ԭ���ݿ�ı�־�ļ�
	 */
	static final String BACKUP_MARK = ".dbrestored";

	static Map<String, QuirkCacheResourceLog> clusterResourceModes = null;

	private final void doInit(ContextImpl<?, ?, ?> context, SiteState state)
			throws Throwable {
		final ResolveHelper helper = new ResolveHelper(context, new SXElementBuilder());
		if (state != SiteState.LOADING_METADATA) {
			// ע�������ı�������
			ContextVariableIntl.reg(context);
			// ��������־������
			helper.regStartupEntry(LogManagerStartupStep.PREPARE, new RefStartupEntry<LogManager>(this.logManager));
		}
		// ע��cache������
		helper.regStartupEntry(SiteStartupEntry.STARTUP, new SiteStartupEntry(this));
		helper.regStartupEntry(new StartupStep<StartupEntry>() {

			public String getDescription() {
				return "��ʼ��Ⱥ����Դģʽ";
			}

			public int getPriority() {
				return StartupStep.SERVICE_HIGHEST_PRI - 1;
			}

			public StartupStep<StartupEntry> doStep(ResolveHelper helper,
					StartupEntry target) throws Throwable {

				if (clusterResourceModes != null) {
					return null;
				}
				clusterResourceModes = new HashMap<String, QuirkCacheResourceLog>();
				Context context = helper.context;
				DataSourceRef ds = null;
				try {
					ds = ((ContextImpl) context).getDataSourceRef();
				} catch (Throwable e) {

				}
				if (ds == null) {
					return null;
				}
				ORMAccessor<QuirkCacheResourceLog> orm = context.newORMAccessor(context.find(ORM_CoreResourceLog.class));
				for (QuirkCacheResourceLog log : orm.fetch()) {
					clusterResourceModes.put(log.getFacade(), log);
				}
				return null;
			}
		}, new StartupEntry() {
		});
		// helper.regStartupEntry(DataSourceTenantStep.INSTANCE,
		// new EmptyStartupEntry());
		final SpaceNode spaceSave = this.updateContextSpace(context);
		try {
			// ��վ��װ��װ�ص�bundle�ڷ�����Ԫ��
			for (BundleStub bundle : this.bundles) {
				bundle.gatherElement(this, helper);
			}
			this.state = SiteState.INITING;
			helper.startup();
			this.state = state;
		} finally {
			spaceSave.updateContextSpace(context);
		}
	}

	final ContextImpl<?, ?, ?> newSystemSessionSiteContext(ContextKind kind) {
		return this.application.getSystemSession().newContext(this, kind);
	}

	private void tryRestoreDB(ContextImpl<?, ?, ?> context) {
		final File mask = new File(this.application.getDNAWork(), BACKUP_MARK);
		if (mask != null && mask.isFile()) {
			return;
		}
		final File backupFile = DbBackup.findBackupFile(this.application, this.dataSourceRef);
		if (backupFile != null) {
			if (ContextVariableIntl.CHECK_IMP_FILE) {
				try {
					ZipFile zf = new ZipFile(backupFile);
					if (zf.getEntry(DbBackup.SUCCESS_MARK) == null) {
						this.application.catcher.catchException(new IllegalBackupFileException(backupFile), this);
						return;
					}
				} catch (Throwable e) {
					this.application.catcher.catchException(e, this);
				}
			}
			try {
				final FileInputStream fis = new FileInputStream(backupFile);
				try {
					DbBackup.restore(context, fis);
				} finally {
					try {
						fis.close();
					} catch (Throwable e) {
						this.application.catcher.catchException(e, this);
					} finally {
						final File restoredFile = DbBackup.calRestoredFile(this.application, this.dataSourceRef);
						boolean rename = backupFile.renameTo(restoredFile);
						if (!rename) {
							final String message = "���ݿ⻹ԭ���޷�ɾ�������ļ�����Ҫ�ֶ�ɾ���ļ���" + restoredFile.getParent() + "��";
							File newMask = new File(this.application.getDNAWork(), BACKUP_MARK);
							try {
								if (!newMask.createNewFile()) {
									DNALogManager.getLogger("core/db/restore").logFatal(null, message, false);
									System.exit(-1);
								}
							} catch (Exception e) {
								DNALogManager.getLogger("core/db/restore").logFatal(null, message, false);
								System.exit(-1);
							}
						}
					}
				}
			} catch (Throwable e) {
				this.application.catcher.catchException(e, this);
			}
		}
	}

	final void active(boolean tryRestoreDB) throws Throwable {
		final ContextImpl<?, ?, ?> context = this.newSystemSessionSiteContext(ContextKind.INITER);
		try {
			if (tryRestoreDB) {
				this.tryRestoreDB(context);
			}
			if (Boolean.getBoolean("com.jiuqi.dna.restore-db-only")) {
				DNALogManager.getLogger("core/db/restore").logWarn(null, "DNA��������ֹ�����ݿ⻹ԭģʽ��", true);
				System.exit(-1);
			}
			if (ContextVariableIntl.UNSAFE_FORCE_SKIP_DB_SYNC) {
				DNALogManager.getLogger("core/db/sync").logWarn(null, "ǿ�ƺ������ݿ�ͬ��������", true);
			}
			this.doInit(context, SiteState.ACTIVE);
		} finally {
			context.dispose();
		}
	}

	final void load(ContextImpl<?, ?, ?> context, LoadAllMetaDataTask task)
			throws Throwable {
		// �Ƿ���Ҫ������������װ����ͬһ�����
		context.setNextStep(0.05f);
		this.doInit(context, SiteState.LOADING_METADATA);
		final SpaceNode spaceSave = this.updateContextSpace(context);
		try {
			final LoadMetaDataEvent event = new LoadMetaDataEvent(task.getMethod() == LoadAllMetaDataTask.LoadMode.MERGE, task.metaData, task.logger);
			event.getMetaStream().use();
			try {
				context.setNextStep(0.95f);
				context.dispatch(event);
			} finally {
				event.getMetaStream().unuse();
			}
		} finally {
			this.state = SiteState.DISPOSING;
			spaceSave.updateContextSpace(context);
		}
		// ����
		final long finishTime = System.currentTimeMillis();
		task.finishTime = finishTime;
		synchronized (task) {
			for (long needWait = task.getRestartDelay(); needWait > 0; needWait = task.getRestartDelay() - (System.currentTimeMillis() - finishTime)) {
				task.wait(needWait);
			}
		}
	}

	private final static EventListenerChain unInintTag = new EventListenerChain(null);
	private EventListenerChain sessionDisposeEventListeners = unInintTag;

	private final static SessionDisposeEvent disposeEvent = new SessionDisposeEvent();

	final ContextImpl<?, ?, ?> sessionDisposing(SessionImpl session,
			ContextImpl<?, ?, ?> context) {
		switch (session.kind) {
		case NORMAL:
			EventListenerChain listeners = this.sessionDisposeEventListeners;
			if (listeners == unInintTag) {
				this.sessionDisposeEventListeners = listeners = this.collectEvent(SessionDisposeEvent.class, null, null, null, InvokeeQueryMode.IN_SITE);
			}
			if (listeners != null) {
				if (context == null) {
					context = session.newContext(this, ContextKind.DISPOSER);
				}
				try {
					context.processEvents(listeners, disposeEvent, false);
				} catch (Throwable e) {
					// ����
				}
			}
			break;
		}
		return context;
	}

	/**
	 * վ��״̬
	 */
	volatile SiteState state = SiteState.INITING;

	/**
	 * ���վ��
	 * 
	 * @param catcher
	 */
	@Override
	void doDispose(ContextImpl<?, ?, ?> context) {
		try {
			super.doDispose(context);
		} finally {
			this.state = SiteState.DISPOSED;
		}
	}

	/**
	 * վ��ID
	 */
	GUID id = GUID.emptyID;

	final Cache cache;

	final int asSimpleID() {
		final GUID siteid = this.id;
		return siteid == null ? 0 : (int) ((siteid.getMostSigBits() >>> TimeRelatedSequenceImpl.TIME_ZOOM_SHIFT) ^ siteid.getLeastSigBits());
	}

	final void setSiteInfo(CoreSiteInfo siteInfo) {
		this.id = siteInfo.RECID;
	}

	@Override
	public final String toString() {
		return this.name + " (site)";
	}

	final Space tryLocateSpace(String spacePath, char spaceSeparator) {
		Space space = this;
		if (spacePath != null && spacePath.length() > 0) {
			int start = spacePath.charAt(0) == spaceSeparator ? 1 : 0;
			int eof = spacePath.length();
			while (start < eof) {
				int end = spacePath.indexOf(spaceSeparator, start);
				if (end < 0) {
					end = eof;
				}
				if (end > start) {
					Space childSpace = space.findSub(spacePath, start, end - start);
					if (childSpace == null) {
						break;
					}
					space = childSpace;
				}
				start = end + 1;
			}
		}
		return space;
	}

	final Space ensureSpace(String spacePath, char spaceSeparator) {
		Space space = this;
		if (spacePath != null && spacePath.length() > 0) {
			int start = spacePath.charAt(0) == spaceSeparator ? 1 : 0;
			int eof = spacePath.length();
			while (start < eof) {
				int end = spacePath.indexOf(spaceSeparator, start);
				if (end < 0) {
					end = eof;
				}
				if (end > start) {
					Space sub = space.findSub(spacePath, start, end - start);
					if (sub == null) {
						sub = new Space(space, spacePath.substring(start, end));
					}
					space = sub;
				}
				start = end + 1;
			}
		}
		return space;
	}

	/**
	 * Ӧ�ö���
	 */
	final ApplicationImpl application;
	final LogManager logManager;

	@Override
	final Site asSite() {
		return this;
	}

	final static String xml_attr_name = "name";

	private static final String getSiteName(SXElement siteInfo) {
		if (siteInfo != null) {
			final String name = siteInfo.getAttribute(xml_attr_name);
			if (name != null && name.length() > 0) {
				return name;
			}
		}
		return "default";
	}

	final void fillSiteConnectionInfos(SXElement siteInfo) {
		if (this.application.dataSourceManager.isEmpty()) {
			return;
		}
		final TreeMap<String, DataSourceRef> sourceRefs = new TreeMap<String, DataSourceRef>();
		if (siteInfo != null) {
			for (SXElement datasourcerefE : siteInfo.getChildren(Site.xml_element_datasourcerefs, DataSourceRef.xml_element_datasourceref)) {
				final String space = datasourcerefE.getAttribute(DataSourceRef.xml_attr_space, "");
				if (!sourceRefs.containsKey(space)) {
					try {
						DataSourceRef ref = new DataSourceRef(this.application.dataSourceManager, datasourcerefE);
						sourceRefs.put(space, ref);
					} catch (Throwable e) {
						this.application.catcher.catchException(e, this.application.dataSourceManager);
					}
				}
			}
		}
		if (!sourceRefs.containsKey("")) {
			this.dataSourceRef = new DataSourceRef(this.application.dataSourceManager.getDefaultSource());
		}
		for (Entry<String, DataSourceRef> e : sourceRefs.entrySet()) {
			this.ensureSpace(e.getKey(), '/').dataSourceRef = e.getValue();
		}
	}

	/**
	 * �ո�վ��Ĺ��캯��
	 */
	Site(ApplicationImpl application, SXElement siteInfo, boolean shared) {
		super(null, getSiteName(siteInfo));
		this.application = application;
		this.shared = shared;
		this.logManager = new LogManager(this);
		this.bundles = application.bundles.values().toArray(new BundleStub[application.bundles.size()]);
		this.fillSiteConnectionInfos(siteInfo);
		this.cache = new Cache(this);
		this.cacheAccessor = new AcquirableAccessor();
		this.starter = new SiteStarter(this);
	}

	// /////////////////////////////////////////////////////
	// ////// ����
	// ////////////////////////////////////////////////////
	final static String xml_element_site = "site";
	final static String xml_element_publish = "publish";
	final static String xml_element_datasourcerefs = "datasource-refs";

	private final static int TRANSACTION_ID_LEN = 32;
	private final static int MIN_TRANSACTION_ID = 1;
	private final static int MAX_TRANSACTION_ID = (1 << (TRANSACTION_ID_LEN - NetClusterImpl.NODE_INDEX_LEN)) - 1;
	private final static int TRANSACTION_NODE_SHIFT = TRANSACTION_ID_LEN - NetClusterImpl.NODE_INDEX_LEN;

	private volatile int transIDCounter;
	private final IntKeyMap<Transaction> transactions = new IntKeyMap<Transaction>();
	final boolean shared;
	final SiteStarter starter;
	/**
	 * ��Դ��������
	 */
	final AcquirableAccessor cacheAccessor;

	final NetSelfClusterImpl getNetCluster() {
		return this.application.netNodeManager.thisCluster;
	}

	// ======================= �������� =========================

	final Transaction newTransaction(TransactionKind kind,
			final Transaction parent) {
		if (kind == null) {
			throw new NullArgumentException("kind");
		}
		if (kind == TransactionKind.REMOTE) {
			throw new IllegalArgumentException();
		}
		this.starter.transLock(kind);
		synchronized (this.transactions) {
			// ��������id
			int tid = ++this.transIDCounter;
			if (tid > MAX_TRANSACTION_ID) {
				tid = this.transIDCounter = MIN_TRANSACTION_ID;
			}
			tid |= this.getNetCluster().thisClusterNodeIndex << TRANSACTION_NODE_SHIFT;
			if (this.transactions.get(tid) != null) {
				throw new IllegalStateException("�ظ�ע������[" + tid + "]");
			}
			// �½�����
			Transaction transaction = new Transaction(this, tid, kind, null, parent);
			this.transactions.put(tid, transaction);
			return transaction;
		}
	}

	final boolean transactionDisposed(Transaction transaction) {
		if (transaction == null) {
			throw new NullArgumentException("transaction");
		}
		// ��������
		synchronized (this.transactions) {
			// ɾ������
			Transaction t = this.transactions.remove(transaction.id);
			if (t == null) {
				return false;
			}
			if (transaction != t) {
				this.transactions.put(t.id, t);
				return false;
			}
		}
		this.starter.releaseTransLock(transaction.kind);
		return true;
	}

	final Transaction getTransaction(int id, NetNodeImpl node) {
		if (id == 0) {
			throw new IllegalArgumentException("����ID������Ϊ0");
		}
		Transaction trans;
		if ((id >>> TRANSACTION_NODE_SHIFT) == this.getNetCluster().thisClusterNodeIndex) {
			synchronized (this.transactions) {
				trans = this.transactions.get(id);
			}
			if (trans == null) {
				throw new IllegalStateException(String.format("�Ҳ���IDΪ[%x]���������", id));
			}
		} else {
			synchronized (this.transactions) {
				trans = this.transactions.get(id);
				if (trans != null) {
					return trans;
				}
				trans = new Transaction(this, id, TransactionKind.REMOTE, node, null);
				this.transactions.put(trans.id, trans);
			}
			this.starter.transLock(TransactionKind.REMOTE);
		}
		return trans;
	}

	/**
	 * ��վ�������������Ŀ�������������������������Խ��м�Ⱥ����Ĳ���
	 */
	final void acquireLock() {
		this.starter.syncLock();
	}

	/**
	 * ���վ����
	 */
	final void releaseLock() {
		// ��������Ϣͬ���������ڵ���
		this.starter.releaseSyncLock();
	}

	final void shutdown(ContextImpl<?, ?, ?> context, boolean restart)
			throws Throwable {
		this.starter.shutdown(context, restart);
	}

	final int getClusterSessionCount(ContextImpl<?, ?, ?> context,
			boolean excludeBuildinUser) throws InterruptedException {
		int count = this.site.application.sessionManager.getNormalSessionCount(excludeBuildinUser);
		NetSelfClusterImpl c = this.getNetCluster();
		synchronized (c) {
			ArrayList<NetTaskRequestImpl<GetClusterSessionCountTask, None>> arr = new ArrayList<NetTaskRequestImpl<GetClusterSessionCountTask, None>>();
			for (NetNodeImpl n = c.getFirstNetNode(); n != null; n = n.getNextNodeInCluster()) {
				if (n.getState() == NetNodeImpl.STATE_READY) {
					GetClusterSessionCountTask task = new GetClusterSessionCountTask(excludeBuildinUser);
					arr.add(n.newSession(this).newRemoteTransactionRequest(task, None.NONE, context.transaction));
				}
			}
			for (NetTaskRequestImpl<GetClusterSessionCountTask, None> task : arr) {
				task.internalWaitStop(0);
				count += task.getTask().getCount();
			}
		}
		return count;
	}

	// ===================== Զ������ =======================

	final void onNetNodeDisposed(final NetNodeImpl netNode) {
		this.starter.remoteReleaseSyncLockNoCheck(netNode);
		final int nodeIndex = netNode.channel.getRemoteNodeIndex();
		final ArrayList<Transaction> arr = new ArrayList<Transaction>();
		synchronized (this.transactions) {
			if (this.transactions.isEmpty()) {
				return;
			}
			this.transactions.visitAll(new ValueVisitor<Transaction>() {
				public void visit(int key, Transaction value) {
					if ((key >>> TRANSACTION_NODE_SHIFT) == nodeIndex) {
						arr.add(value);
					}
				}
			});
		}
		for (Transaction t : arr) {
			t.onNetNodeDisabled(netNode);
		}
	}

	// ===========================��Ȩ���====================
	/**
	 * δȷ����Ȩ������
	 */
	private static final byte LFS_UNKNOWN = 0;
	/**
	 * ��Ȩ����ʹ����Դ�ṩ��
	 */
	private static final byte LFS_BY_CACHE = 1;
	/**
	 * ��Ȩ����ʹ�ý���ṩ��
	 */
	private static final byte LFS_BY_PROVIDER = 2;
	/**
	 * ��Ȩ������������
	 */
	private static final byte LFS_NO_FINDER = 3;
	/**
	 * ��Ȩ��������״̬
	 */
	private volatile byte licenseFinderState;
	/**
	 * ��Ȩ��������״̬ΪLFS_BY_CACHEʱ����Դ����
	 */
	private volatile CacheHolderIndex<LicenseEntry, ?, ?> leIndex;
	/**
	 * ��Ȩ��������״̬ΪLFS_BY_PROVIDERʱ�Ľ���ṩ��
	 */
	private volatile ServiceInvokeeBase<LicenseEntry, Context, String, ?, ?> leProvider;

	/**
	 * ��������Ȩ��
	 * 
	 * @param licenseEntryName
	 *            ��Ȩ������
	 * @param context
	 *            �����Ķ���
	 * @return �����ҵ�����Ȩ���null
	 */
	@SuppressWarnings("unchecked")
	public final LicenseEntry findLicenseEntry(String licenseEntryName,
			ContextImpl<?, ?, ?> context) {
		if (licenseEntryName == null) {
			throw new NullArgumentException(licenseEntryName);
		}
		for (;;) {
			switch (this.licenseFinderState) {
			case LFS_BY_CACHE:
				final CacheHolder<LicenseEntry, ?, ?> ch = this.leIndex.findHolder(licenseEntryName, null, null, context.transaction);
				return ch != null ? ch.tryGetFacade(context.transaction) : null;
			case LFS_BY_PROVIDER:
				try {
					return this.leProvider.provide(context, licenseEntryName);
				} catch (Throwable e) {
					throw Utils.tryThrowException(e);
				}
			case LFS_UNKNOWN:
				synchronized (this) {
					if (this.licenseFinderState != LFS_UNKNOWN) {
						continue;
					}
					final CacheGroup<LicenseEntry, ?, ?> group = this.cache.defaultGroupSpace.findGroup(LicenseEntry.class, context.transaction);
					if (group != null) {
						if ((this.leIndex = group.findIndex(String.class, null, null)) != null) {
							this.licenseFinderState = LFS_BY_CACHE;
							continue;
						}
					}
					if ((this.leProvider = this.findInvokeeBase(LicenseEntry.class, String.class, null, null, ServiceInvokeeBase.MASK_RESULT, InvokeeQueryMode.IN_SITE)) != null) {
						this.licenseFinderState = LFS_BY_PROVIDER;
						continue;
					}
					this.licenseFinderState = LFS_NO_FINDER;
				}
				// ���е�����ʱҲ��Ҫ����null��˲���Ҫbreak;
			default:
				return null;
			}
		}

	}
}
