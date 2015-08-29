package com.jiuqi.dna.core.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Locale;

import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.LoginState;
import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.exception.SessionDisposedException;
import com.jiuqi.dna.core.exception.SessionDisposedException.SessionDisposedKind;
import com.jiuqi.dna.core.exception.SituationReentrantException;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.spi.application.RemoteInfoSPI;
import com.jiuqi.dna.core.spi.application.Session;
import com.jiuqi.dna.core.spi.auth.AdjuestUserIdentifyEvent;
import com.jiuqi.dna.core.type.GUID;

public final class SessionImpl implements Session, RemoteInfoSPI {

	/**
	 * �����Ự
	 * 
	 * @param asSituation
	 *            �Ƿ���Ϊ�龰�����ģ�����UI���̣߳�
	 * @throws SessionDisposedException
	 *             �Ự�Ѿ�����
	 * @throws SituationReentrantException
	 *             ������龰�����ģ��򱨸��龰�����쳣���Ѿ�������������δ�˳���UI���̣߳�
	 */
	public final <TUserData> ContextImpl<?, ?, ?> newContext(boolean asSituation)
			throws SessionDisposedException, SituationReentrantException {
		return this.newContext(asSituation ? ContextKind.SITUATION : ContextKind.NORMAL);
	}

	/**
	 * ��õ�½��״̬
	 * 
	 * @return
	 */
	public final LoginState getState() {
		return this.state;
	}

	public final boolean isInvalid() {
		return this.invalid;
	}

	public final long getID() {
		return this.id;
	}

	public final long getVerificationCode() {
		return this.verificationCode;
	}

	@Override
	public final String toString() {
		return String.format("{LOGIN [ID:%s, USER:%s, STATE:%s]}", this.id, this.user, this.state);
	}

	public final User getUser() {
		return this.user;
	}

	/**
	 * ��ǵ��Ự�л����������е�������ʱ�����ͷ�����
	 */
	final static long IGNORE_IF_HAS_CONTEXT = -1l;

	/**
	 * �����ͷŻỰ
	 * 
	 * @param timeout
	 *            �ͷŻỰ���ӳ٣�0��ʾ�����ͷ�<br>
	 *            ����ֵ==IGNORE_IF_HAS_CONTEXTʱ��ʾ��������������е�����������Ը����󲢷���false
	 * @return
	 */
	final boolean internalDispose(long timeout) {
		synchronized (this) {
			if (timeout == IGNORE_IF_HAS_CONTEXT && this.contexts != null) {
				return false;
			}
			switch (this.state) {
			case DISPOSED:
				return false;
			case DISPOSING:
				final long oldDisposeTimeout = this.themeTimeOrDisposeTimeout;
				if (oldDisposeTimeout != Long.MAX_VALUE) {
					final long disposeTimeout = System.currentTimeMillis() + timeout;
					if (disposeTimeout < this.themeTimeOrDisposeTimeout) {
						this.themeTimeOrDisposeTimeout = disposeTimeout;
					}
				}
				return false;// �ɻỰ�����̸߳����ͷ�
			default:
				this.state = LoginState.DISPOSING;
				if (timeout == 0 || this.contexts == null) {
					// ���ⱻ�ظ�����
					this.themeTimeOrDisposeTimeout = Long.MAX_VALUE;
				} else {
					final long now = System.currentTimeMillis();
					this.heartbeatTimeOrDisposingTime = now;
					this.themeTimeOrDisposeTimeout = now + timeout;
					return false;// �ɻỰ�����̸߳����ͷ�
				}
			}
		}
		this.doDispose();
		return true;
	}

	/**
	 * �������٣��ᱣ��ϵͳ�Ự
	 */
	public final void dispose(long timeout) {
		if (this.kind == SessionKind.SYSTEM) {
			throw new UnsupportedOperationException("��֧������ϵͳ�Ự��");
		}
		this.internalDispose(timeout < 0l ? 0l : timeout);
	}

	/**
	 * ʹ��ǰ�Ự��Ч�����ǲ����٣�������Ȼ�����������߹��ڻ��ƶ����٣�
	 */
	public void invalid() {
		this.invalid = true;
	}

	final User changeUser(final User user) {
		synchronized (this) {
			switch (this.state) {
			case DISPOSING:
			case DISPOSED:
				throw new SessionDisposedException(SessionDisposedKind.NORMAL);
			}
			return this.internalSetUser(user);
		}
	}

	// -------------------------------����Ȩ�����--------------------------------------

	private final User internalSetUser(final User user) {
		if (user == null) {
			throw new NullArgumentException("user");
		}
		final User oldUser = this.user;
		final boolean newIsBuildIn = user.isBuildInUser();
		if (newIsBuildIn || user instanceof UserImplement) {
			this.user = user;
			this.state = (BuildInUser.anonym == user) ? LoginState.ANONYNOUS : LoginState.LOGIN;
		} else {
			throw new IllegalArgumentException("��֧�ֵ��û����͡�[" + user.getClass() + "]");
		}
		this.currentIdentifyIdentifier = AccessControlConstants.adjustACVersion(this.getAdjuestedUserIdentify(user, AccessControlConstants.DEFAULT_ACVERSION));
		if (this.kind == SessionKind.NORMAL && newIsBuildIn != (oldUser == null || oldUser.isBuildInUser())) {
			this.application.sessionManager.addNoneBuildinUseNormalrSessionCount(newIsBuildIn ? -1 : 1);
		}
		return oldUser;
	}

	public final void setUserCurrentOrg(final GUID ACVersion) {
		if (!this.user.isBuildInUser()) {
			this.currentIdentifyIdentifier = AccessControlConstants.adjustACVersion(this.getAdjuestedUserIdentify(this.user, ACVersion));
		}
	}

	public final GUID getUserCurrentOrg() {
		return this.currentIdentifyIdentifier;
	}

	private final GUID getAdjuestedUserIdentify(final User user,
			final GUID identifyIdentifier) {
		ContextImpl<?, ?, ?> context;
		synchronized (this) {
			final Thread currentThread = Thread.currentThread();
			final ContextImpl<?, ?, ?> firstContext = this.contexts;
			context = firstContext;
			while (context != null) {
				if (context.transaction.isOwnerThread(currentThread)) {
					break;
				}
				context = context.nextInSession();
				if (context == firstContext) {
					return identifyIdentifier;
				}
			}
		}
		if (context != null) {
			return getAdjuestedUserIdentify(context, user, identifyIdentifier);
		} else {
			return identifyIdentifier;
		}
	}

	static final GUID getAdjuestedUserIdentify(
			final ContextImpl<?, ?, ?> context, final User user,
			final GUID identifyIdentifier) {
		final AdjuestUserIdentifyEvent event = new AdjuestUserIdentifyEvent(user.getID(), identifyIdentifier);
		context.dispatch(event);
		return event.identifyIdentifier;
	}

	// -------------------------------����Ȩ�����--------------------------------------
	// --

	/**
	 * ��ȡApplication
	 * 
	 * @return
	 */
	public final ApplicationImpl getApplication() {
		return this.application;
	}

	/**
	 * �жϻỰ�Ƿ������Ҫ���٣��������ͨ�Ự����
	 */
	final boolean itsTimeToDispose(final long now) {
		if (this.kind != SessionKind.NORMAL) {
			return false;
		}
		switch (this.state) {
		case DISPOSED:
			return false;
		case DISPOSING:
			return now >= this.themeTimeOrDisposeTimeout;
		default:
			final int hearbeatTimeoutSec = this.heartbeatTimeoutSec;
			if (hearbeatTimeoutSec > 0 && (now - this.heartbeatTimeOrDisposingTime) > hearbeatTimeoutSec * 1000) {
				return true;
			}
			final int sessionTimeoutMin = this.sessionTimeoutMinutes;
			if (sessionTimeoutMin > 0 && (now - this.themeTimeOrDisposeTimeout) > sessionTimeoutMin * 60000L) {
				return true;
			}
			return false;
		}
	}

	public final SessionKind getKind() {
		return this.kind;
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// ////////////////////
	// //////////////////�������ڲ�����/////////////////////////////////////////////
	// ////
	// ////////////////
	// //////////////////////////////////////////////////////////////////////
	// ////
	// ////////////////////
	final ApplicationImpl application;
	/**
	 * ��¼ID
	 */
	final long id;
	/**
	 * ��֤��
	 */
	final long verificationCode;
	/**
	 * �û�
	 */
	private User user;
	/**
	 * Ȩ�����,�û���ǰ��֯ӳ��
	 */
	GUID currentIdentifyIdentifier;
	/**
	 * ״̬
	 */
	private volatile LoginState state;
	private volatile boolean invalid;
	private volatile ContextImpl<?, ?, ?> contexts;

	/**
	 * ��Դ���
	 */
	final SessionKind kind;
	/**
	 * ����ʱ��
	 */
	final long createTime;

	/**
	 * ���һ�����̻߳ʱ������ٳ�ʱʱ��
	 */
	private long themeTimeOrDisposeTimeout;
	/**
	 * �ϴ�����ʱ���ʼ����ʱ��
	 */
	private long heartbeatTimeOrDisposingTime;
	/**
	 * ��������ʱ�䣨�룩�������
	 */
	private int heartbeatTimeoutSec;

	public final int getHeartbeatTimeoutSec() {
		return this.heartbeatTimeoutSec;
	}

	public final void setHeartbeatTimeoutSec(int heartbeatTimeoutSec) {
		this.heartbeatTimeoutSec = heartbeatTimeoutSec > 0 ? heartbeatTimeoutSec : 0;
	}

	/**
	 * �Ự��ʱʱ�䣨�룩����ʮ����
	 */
	private int sessionTimeoutMinutes;

	public final int getSessionTimeoutMinutes() {
		return this.sessionTimeoutMinutes;
	}

	public final void setSessionTimeoutMinutes(int sessionTimeoutMinutes) {
		this.sessionTimeoutMinutes = sessionTimeoutMinutes > 0 ? sessionTimeoutMinutes : 0;
	}

	public synchronized final long getLastInteractiveTime() {
		LoginState state = this.state;
		switch (state) {
		case DISPOSING:
		case DISPOSED:
			return this.heartbeatTimeOrDisposingTime;
		default:
			return this.themeTimeOrDisposeTimeout;
		}
	}

	final static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

	final ContextImpl<?, ?, ?> contextCreated(ContextImpl<?, ?, ?> context)
			throws SessionDisposedException, SituationReentrantException {
		ContextKind contextKind = context.kind;
		final ThreadLocal<ContextImpl<?, ?, ?>> contextLocal = this.application.contextLocal;
		final ContextImpl<?, ?, ?> upper = contextLocal.get();
		if (contextKind != ContextKind.DISPOSER) {
			synchronized (this) {
				this.checkNotDisposedNoSync();
				switch (contextKind) {
				case SITUATION:
					if (this.kind == SessionKind.SYSTEM || this.themeContext != null) {
						throw new SituationReentrantException();
					}
					this.themeContext = context;
					this.heartbeatTimeOrDisposingTime = this.themeTimeOrDisposeTimeout = context.createTime;
					break;
				case NORMAL:
					this.heartbeatTimeOrDisposingTime = context.createTime;
					break;
				}
				this.contexts = context.joinChain(this.contexts);
			}
		}
		contextLocal.set(context);
		return upper;

	}

	final void contextDisposed(ContextImpl<?, ?, ?> context) {
		if (context.kind == ContextKind.DISPOSER) {
			return;
		}
		synchronized (this) {
			if (this.themeContext == context) {
				this.themeContext = null;
			}
			this.contexts = context.leaveChain(this.contexts);
		}
	}

	/**
	 * ���ûỰ�����ڲ���ͬ��ʱ����ϵͳ�Ự
	 */
	final void reset(ExceptionCatcher catcher) {
		if (this.themeRootSituation != null) {
			try {
				this.themeRootSituation.internalClose();
				this.themeRootSituation = new SituationImpl(this);
			} catch (Throwable e) {
				catcher.catchException(e, this);
			}
		}
		this.themeData = null;
		this.pendingTail = null;
		this.setLocale(Locale.getDefault());
		synchronized (this) {
			if (this.cacheGroupContainer != null) {
				final Transaction transaction = this.application.getDefaultSite().newTransaction(TransactionKind.NORMAL, null);
				transaction.bindCurrentThread();
				boolean commit = true;
				try {
					this.cacheGroupContainer.reset(transaction);
				} catch (Throwable e) {
					commit = false;
					transaction.getExceptionCatcher().catchException(e, null);
				} finally {
					try {
						transaction.finish(commit);
					} finally {
						transaction.dispose();
					}
				}
			}
		}
	}

	final void doDispose() {
		// this.state == LoginState.DISPOSING
		this.application.sessionManager.remove(this);
		final Site site = this.application.getDefaultSite();
		ContextImpl<?, ?, ?> disposer = null;
		try {
			if (this.kind == SessionKind.NORMAL) {
				if (site != null) {
					// XXX ֪ͨ���е�վ��
					disposer = site.sessionDisposing(this, disposer);
				}
			}
		} catch (Throwable e) {
			// ����
		} finally {
			if (disposer != null) {
				try {
					disposer.dispose();
				} catch (Throwable e) {
					// ����
				}
			}
		}
		synchronized (this) {
			this.state = LoginState.DISPOSED;
			ContextImpl<?, ?, ?> context = this.contexts;
			if (context != null) {
				this.contexts = null;
				context.cancelChain();
			}
			if (this.cacheGroupContainer != null) {
				this.cacheGroupContainer.dispose();
			}
		}
	}

	/**
	 * ���캯��
	 * 
	 * @param application
	 */
	SessionImpl(ApplicationImpl application, long id, SessionKind kind,
			User user, Object themeData, SessionImpl nextInHashTable,
			int sessionTimeoutMinutes, int heartbeatTimeoutSeconds) {
		if (user == null) {
			throw new NullArgumentException("user");
		}
		if (application == null) {
			throw new NullArgumentException("application");
		}
		if (kind == null) {
			throw new NullArgumentException("kind");
		}
		this.sessionTimeoutMinutes = sessionTimeoutMinutes;
		this.heartbeatTimeoutSec = heartbeatTimeoutSeconds;
		this.kind = kind;
		this.id = id;
		this.verificationCode = GUID.randomLong();
		this.application = application;
		this.nextInHashTable = nextInHashTable;
		this.setLocale(Locale.getDefault());
		this.internalSetUser(user);
		this.createTime = this.heartbeatTimeOrDisposingTime = this.themeTimeOrDisposeTimeout = System.currentTimeMillis();
		if (kind == SessionKind.NORMAL) {
			this.themeRootSituation = new SituationImpl(this);
		}
	}

	final static TransactionKind getTransactionKind(ContextKind kind) {
		TransactionKind transactionKind;
		switch (kind) {
		case INITER:
			transactionKind = TransactionKind.SYSTEM_INIT;
			break;
		case NORMAL:
		case SITUATION:
			transactionKind = TransactionKind.NORMAL;
			break;
		case INTERNAL:
		case TRANSIENT:
		case DISPOSER:
			transactionKind = TransactionKind.TRANSIENT;
			break;
		default:
			throw new IllegalArgumentException();
		}
		return transactionKind;
	}

	final ContextImpl<?, ?, ?> newContext(SpaceNode occorAt, ContextKind kind)
			throws SessionDisposedException, SituationReentrantException {
		final Transaction transaction = occorAt.site.newTransaction(getTransactionKind(kind), null);
		try {
			return new ContextImpl<Object, Object, Object>(this, occorAt, kind, transaction, null);
		} catch (Throwable e) {
			transaction.dispose();
			throw Utils.tryThrowException(e);
		}
	}

	final ContextImpl<?, ?, ?> newContext(ContextKind kind)
			throws SessionDisposedException, SituationReentrantException {
		final Site site = this.application.getDefaultSite();
		final Transaction transaction = site.newTransaction(getTransactionKind(kind), null);
		try {
			return new ContextImpl<Object, Object, Object>(this, site, kind, transaction, null);
		} catch (Throwable e) {
			transaction.dispose();
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * ����Զ�̵���ʹ��
	 */
	final ContextImpl<?, ?, ?> newContext(Transaction transaction,
			ContextKind kind, NetNodeImpl remoteCaller)
			throws SessionDisposedException, SituationReentrantException {
		return new ContextImpl<Object, Object, Object>(this, transaction.site, kind, transaction, remoteCaller);
	}

	/**
	 * �ڲ�ʹ�ã�kind����INIT/INTERNAL
	 */
	final ContextImpl<?, ?, ?> newContext(Transaction transaction)
			throws SessionDisposedException, SituationReentrantException {
		ContextKind kind;
		switch (transaction.site.state) {
		case INITING:
			kind = ContextKind.INITER;
			break;
		default:
			kind = ContextKind.INTERNAL;
			break;
		}
		return new ContextImpl<Object, Object, Object>(this, transaction.site, kind, transaction, null);
	}

	// ///////////////////////////////////////////////////////////

	/**
	 * ���ߵĸ��龰
	 */
	private SituationImpl themeRootSituation;

	public final SituationImpl resetSituation() {
		this.usingSituation();
		if (this.themeRootSituation != null) {
			this.themeRootSituation.internalClose();
			return this.themeRootSituation = new SituationImpl(this);
		} else {
			throw new UnsupportedOperationException("����ͨ�Ự��֧�������龰����");
		}
	}

	/**
	 * ���ߵĵ�ǰ������
	 */
	volatile ContextImpl<?, ?, ?> themeContext;

	/**
	 * ʹ���龰������
	 */
	final ContextImpl<?, ?, ?> usingSituation() {
		final ContextImpl<?, ?, ?> context = this.themeContext;
		if (context == null || context.thread != Thread.currentThread()) {
			throw new UnsupportedOperationException("�ǽ����߳̽�ֹ�����龰�ӿ�");
		}
		return context;
	}

	/**
	 * ��������
	 */
	private Object themeData;

	// //////////////////////////////////////////////////

	public final Object getData() {
		return this.themeData;
	}

	public final SituationImpl getSituation() {
		return this.themeRootSituation;
	}

	public final Object setData(Object data) {
		Object old = this.themeData;
		this.themeData = data;
		return old;
	}

	public final RemoteInfoSPI getRemoteInfo() {
		return this;
	}

	// ////////////////////////////
	SessionImpl nextInHashTable;
	// ///////////////////////////////////

	/**
	 * ��Ϣ���е�β��
	 */
	private PendingMessageImpl<?> pendingTail;

	/**
	 * �ӵȴ���Ϣ�ж����Ƴ�<br>
	 * ���ø÷���ǰ����������ǰtheme����
	 */
	final PendingMessageImpl<?> removePendingMessageNoSync(
			PendingMessageImpl<?> pending) {
		if (pending.prev == pending) {
			if (this.pendingTail == pending) {
				this.pendingTail = null;
			}
		} else {
			pending.prev.next = pending.next;
			pending.next.prev = pending.prev;
			if (this.pendingTail == pending) {
				this.pendingTail = pending.prev;
			}
		}
		return pending;
	}

	private final void checkNotDisposedNoSync() {
		switch (this.state) {
		case DISPOSING:
		case DISPOSED:
			throw new SessionDisposedException(SessionDisposedKind.USERINVALID);
		case LOGIN:
			switch (this.user.getState()) {
			case DISABLE:
			case DISPOSED:
				this.dispose(1);
				throw new SessionDisposedException(SessionDisposedKind.USERINVALID);
			}
		}
	}

	final boolean disposingOrDisposed() {
		final LoginState s = this.state;
		return s == LoginState.DISPOSED || s == LoginState.DISPOSING;
	}

	final void addPendingMessage(PendingMessageImpl<?> pending) {
		if (this.kind == SessionKind.SYSTEM) {
			throw new UnsupportedOperationException("ϵͳ�Ự��֧�ִ˵���");
		}
		synchronized (this) {
			this.checkNotDisposedNoSync();
			PendingMessageImpl<?> tail = this.pendingTail;
			if (tail == null) {
				pending.next = pending;
				pending.prev = pending;
			} else {
				pending.prev = tail;
				pending.next = tail.next;
				tail.next = pending;
				pending.next.prev = pending;
			}
			this.pendingTail = pending;
		}
	}

	final boolean handlePendingMessage() {
		PendingMessageImpl<?> one;
		synchronized (this) {
			if (this.pendingTail == null) {
				return false;
			}
			one = this.removePendingMessageNoSync(this.pendingTail.next);
		}
		one.removeSelfFromSender();
		one.handle();
		return true;
	}

	// //////////////////////////
	private String remoteAddr = "";
	private String remoteHost = "";
	private String remoteMac = "";

	public final String getRemoteAddr() {
		return this.remoteAddr;
	}

	public final String getRemoteHost() {
		return this.remoteHost;
	}

	public final String getRemoteMac() {
		return this.remoteMac;
	}

	public final void setRemoteAddr(String addr) {
		if (addr == null) {
			throw new NullArgumentException("addr");
		}
		this.remoteAddr = addr;
	}

	public final void setRemoteHost(String host) {
		if (host == null) {
			throw new NullArgumentException("host");
		}
		this.remoteHost = host;
	}

	public final void setRemoteMac(String mac) {
		if (mac == null) {
			throw new NullArgumentException("mac");
		}
		this.remoteMac = mac;
	}

	// //////////////////// ���ػ� //////////////////////
	private Locale locate;
	int locateKey;

	public final void setLocale(Locale locale) {
		if (locale == null) {
			throw new NullArgumentException("locale");
		}
		this.locateKey = LocaleInterned.getLocaleKey(locale);
		this.locate = locale;
	}

	public final Locale getLocale() {
		return this.locate;
	}

	// ---------------------------------------------------------------------------------
	// �������

	static final class SessionCacheGroupContainer extends Acquirable {

		private SessionCacheGroupContainer(final Cache cache) {
			this.cache = cache;
		}

		@Override
		final void onTransactionCommit(final Transaction transaction) {
			CacheGroup<?, ?, ?> existGroup = this.firstGroup;
			while (existGroup != null) {
				switch (existGroup.getState()) {
				case CacheGroup.STATE_RESOLVED:
					break;
				case CacheGroup.STATE_CREATED:
					existGroup.resolve(transaction);
					break;
				case CacheGroup.STATE_REMOVED:
					this.disposeGroup(existGroup);
					break;
				case CacheGroup.STATE_DISPOSED:
					break;
				default:
					throw new UnsupportedOperationException();
				}
				existGroup = existGroup.nextInSpace;
			}
		}

		@Override
		final void onTransactionRollback(final Transaction transaction) {
			CacheGroup<?, ?, ?> existGroup = this.firstGroup;
			while (existGroup != null) {
				switch (existGroup.getState()) {
				case CacheGroup.STATE_RESOLVED:
					break;
				case CacheGroup.STATE_CREATED:
					this.disposeGroup(existGroup);
					break;
				case CacheGroup.STATE_REMOVED:
					existGroup.resolve(transaction);
					break;
				case CacheGroup.STATE_DISPOSED:
					break;
				default:
					throw new UnsupportedOperationException();
				}
				existGroup = existGroup.nextInSpace;
			}
		}

		final void reset(final Transaction transaction) {
			synchronized (this) {
				CacheGroup<?, ?, ?> existGroup = this.firstGroup;
				while (existGroup != null) {
					existGroup.reset(transaction);
					existGroup = existGroup.nextInContainer;
				}
			}
		}

		final void dispose() {
			synchronized (this) {
				CacheGroup<?, ?, ?> existGroup = this.firstGroup;
				while (existGroup != null) {
					existGroup.dispose(null);
					existGroup = existGroup.nextInContainer;
				}
			}
		}

		@SuppressWarnings("unchecked")
		final <TFacade> CacheGroup<TFacade, ?, ?> getDefaultGroup(
				final CacheDefine<TFacade, ?, ?> cacheDefine,
				final Transaction transaction) {
			final CacheGroup<TFacade, ?, ?> existGroup = (CacheGroup<TFacade, ?, ?>) (this.findGroup(cacheDefine.facadeClass, null, transaction));
			if (existGroup == null) {
				return this.tryCreateGroup(cacheDefine, null, cacheDefine.title, transaction);
			} else {
				return existGroup;
			}
		}

		@SuppressWarnings("unchecked")
		final <TFacade> CacheGroup<TFacade, ?, ?> findGroup(
				final Class<TFacade> facadeClass, final Object spaceIdentifier,
				final Transaction transaction) {
			final boolean isDefaultGroup = CacheGroupSpace.isPreservedSpaceIdentifier(spaceIdentifier);
			if (this.firstGroup != null) {
				synchronized (this) {
					if (this.firstGroup != null) {
						CacheGroup<?, ?, ?> existGroup = this.firstGroup;
						if (this.isModifiableOnTransaction(transaction)) {
							while (existGroup != null) {
								final byte groupState = existGroup.getState();
								if ((groupState == CacheGroup.STATE_RESOLVED || groupState == CacheGroup.STATE_CREATED) && existGroup.define.facadeClass == facadeClass) {
									if ((CacheGroupSpace.isPreservedSpaceIdentifier(existGroup.ownSpace.identifier) && isDefaultGroup) || existGroup.ownSpace.identifier.equals(spaceIdentifier)) {
										return (CacheGroup<TFacade, ?, ?>) existGroup;
									}
								}
								existGroup = existGroup.nextInContainer;
							}
						} else {
							while (existGroup != null) {
								final byte groupState = existGroup.getState();
								if ((groupState == CacheGroup.STATE_RESOLVED || groupState == CacheGroup.STATE_REMOVED) && existGroup.define.facadeClass == facadeClass) {
									if ((CacheGroupSpace.isPreservedSpaceIdentifier(existGroup.ownSpace.identifier) && isDefaultGroup) || existGroup.ownSpace.identifier.equals(spaceIdentifier)) {
										return (CacheGroup<TFacade, ?, ?>) existGroup;
									}
								}
								existGroup = existGroup.nextInContainer;
							}
						}
					}
				}
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> tryCreateGroup(
				final CacheDefine<TFacade, TImplement, TKeysHolder> cacheDefine,
				final Object spaceIdentifier, final String title,
				final Transaction transaction) {
			transaction.handleAcquirable(this, AcquireFor.MODIFY_ITEMS);
			synchronized (this) {
				final CacheGroup<TFacade, TImplement, TKeysHolder> existGroup = (CacheGroup<TFacade, TImplement, TKeysHolder>) (this.findGroup(cacheDefine.facadeClass, spaceIdentifier, transaction));
				if (existGroup == null) {
					final CacheGroup<TFacade, TImplement, TKeysHolder> newGroup = cacheDefine.newGroup(CacheGroupSpace.isPreservedSpaceIdentifier(spaceIdentifier) ? this.cache.defaultGroupSpace : this.cache.getSpace(spaceIdentifier), title, null, null, null);
					if (CacheGroupSpace.isPreservedSpaceIdentifier(spaceIdentifier)) {
						newGroup.forceSetResolved();
					} else {
						transaction.handleAcquirable(newGroup, AcquireFor.ADD);
					}
					newGroup.nextInContainer = this.firstGroup;
					this.firstGroup = newGroup;
					return newGroup;
				} else {
					return existGroup;
				}
			}
		}

		@SuppressWarnings("unchecked")
		final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> removeGroup(
				final CacheDefine<TFacade, TImplement, TKeysHolder> cacheDefine,
				final Object identifier, final Transaction transaction) {
			transaction.handleAcquirable(this, AcquireFor.MODIFY_ITEMS);
			final CacheGroup<TFacade, TImplement, TKeysHolder> existGroup;
			synchronized (this) {
				existGroup = (CacheGroup<TFacade, TImplement, TKeysHolder>) (this.findGroup(cacheDefine.facadeClass, identifier, transaction));
			}
			if (existGroup != null && existGroup.localRemove(transaction)) {
				this.disposeGroup(existGroup);
			}
			return existGroup;
		}

		private final void disposeGroup(final CacheGroup<?, ?, ?> group) {
			CacheGroup<?, ?, ?> lastExistGroup = null;
			CacheGroup<?, ?, ?> existGroup = this.firstGroup;
			while (existGroup != null) {
				if (existGroup == group) {
					synchronized (this) {
						if (lastExistGroup == null) {
							this.firstGroup = existGroup.nextInContainer;
						} else {
							lastExistGroup.nextInContainer = existGroup.nextInContainer;
						}
					}
					break;
				} else {
					lastExistGroup = existGroup;
					existGroup = existGroup.nextInContainer;
				}
			}
			group.dispose(null);
		}

		private final Cache cache;

		private CacheGroup<?, ?, ?> firstGroup;

	}

	final SessionCacheGroupContainer getCacheGroupContainer() {
		if (this.cacheGroupContainer == null) {
			synchronized (this) {
				if (this.cacheGroupContainer == null) {
					this.cacheGroupContainer = new SessionCacheGroupContainer(this.application.getDefaultSite().cache);
				}
			}
		}
		return this.cacheGroupContainer;
	}

	final SessionCacheGroupContainer tryGetCacheGroupContainer() {
		return this.cacheGroupContainer;
	}

	private volatile SessionCacheGroupContainer cacheGroupContainer;

}
