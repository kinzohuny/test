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
	 * 创建会话
	 * 
	 * @param asSituation
	 *            是否做为情景上下文（运行UI主线程）
	 * @throws SessionDisposedException
	 *             会话已经过期
	 * @throws SituationReentrantException
	 *             如果是情景上下文，则报告情景重入异常（已经存在正在运行未退出的UI主线程）
	 */
	public final <TUserData> ContextImpl<?, ?, ?> newContext(boolean asSituation)
			throws SessionDisposedException, SituationReentrantException {
		return this.newContext(asSituation ? ContextKind.SITUATION : ContextKind.NORMAL);
	}

	/**
	 * 获得登陆的状态
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
	 * 标记当会话中还有正在运行的上下文时忽略释放请求
	 */
	final static long IGNORE_IF_HAS_CONTEXT = -1l;

	/**
	 * 请求释放会话
	 * 
	 * @param timeout
	 *            释放会话的延迟，0表示马上释放<br>
	 *            当该值==IGNORE_IF_HAS_CONTEXT时表示如果还有正在运行的上下文则忽略该请求并返回false
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
				return false;// 由会话清理线程负责释放
			default:
				this.state = LoginState.DISPOSING;
				if (timeout == 0 || this.contexts == null) {
					// 避免被重复清理
					this.themeTimeOrDisposeTimeout = Long.MAX_VALUE;
				} else {
					final long now = System.currentTimeMillis();
					this.heartbeatTimeOrDisposingTime = now;
					this.themeTimeOrDisposeTimeout = now + timeout;
					return false;// 由会话清理线程负责释放
				}
			}
		}
		this.doDispose();
		return true;
	}

	/**
	 * 尝试销毁，会保护系统会话
	 */
	public final void dispose(long timeout) {
		if (this.kind == SessionKind.SYSTEM) {
			throw new UnsupportedOperationException("不支持销毁系统会话！");
		}
		this.internalDispose(timeout < 0l ? 0l : timeout);
	}

	/**
	 * 使当前会话无效，但是不销毁（由其自然由于心跳或者过期机制而销毁）
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

	// -------------------------------以下权限相关--------------------------------------

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
			throw new IllegalArgumentException("不支持的用户类型。[" + user.getClass() + "]");
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

	// -------------------------------以上权限相关--------------------------------------
	// --

	/**
	 * 获取Application
	 * 
	 * @return
	 */
	public final ApplicationImpl getApplication() {
		return this.application;
	}

	/**
	 * 判断会话是否过期需要销毁，仅针对普通会话而言
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
	// //////////////////以下是内部方法/////////////////////////////////////////////
	// ////
	// ////////////////
	// //////////////////////////////////////////////////////////////////////
	// ////
	// ////////////////////
	final ApplicationImpl application;
	/**
	 * 登录ID
	 */
	final long id;
	/**
	 * 验证码
	 */
	final long verificationCode;
	/**
	 * 用户
	 */
	private User user;
	/**
	 * 权限相关,用户当前组织映射
	 */
	GUID currentIdentifyIdentifier;
	/**
	 * 状态
	 */
	private volatile LoginState state;
	private volatile boolean invalid;
	private volatile ContextImpl<?, ?, ?> contexts;

	/**
	 * 资源类别
	 */
	final SessionKind kind;
	/**
	 * 创建时间
	 */
	final long createTime;

	/**
	 * 最后一次主线程活动时间或销毁超时时间
	 */
	private long themeTimeOrDisposeTimeout;
	/**
	 * 上次心跳时间或开始销毁时间
	 */
	private long heartbeatTimeOrDisposingTime;
	/**
	 * 心跳延期时间（秒），五分钟
	 */
	private int heartbeatTimeoutSec;

	public final int getHeartbeatTimeoutSec() {
		return this.heartbeatTimeoutSec;
	}

	public final void setHeartbeatTimeoutSec(int heartbeatTimeoutSec) {
		this.heartbeatTimeoutSec = heartbeatTimeoutSec > 0 ? heartbeatTimeoutSec : 0;
	}

	/**
	 * 会话超时时间（秒），三十分钟
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
	 * 重置会话，用于参数同步时重置系统会话
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
					// XXX 通知所有的站点
					disposer = site.sessionDisposing(this, disposer);
				}
			}
		} catch (Throwable e) {
			// 忽略
		} finally {
			if (disposer != null) {
				try {
					disposer.dispose();
				} catch (Throwable e) {
					// 忽略
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
	 * 构造函数
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
	 * 仅供远程调用使用
	 */
	final ContextImpl<?, ?, ?> newContext(Transaction transaction,
			ContextKind kind, NetNodeImpl remoteCaller)
			throws SessionDisposedException, SituationReentrantException {
		return new ContextImpl<Object, Object, Object>(this, transaction.site, kind, transaction, remoteCaller);
	}

	/**
	 * 内部使用，kind总是INIT/INTERNAL
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
	 * 主线的根情景
	 */
	private SituationImpl themeRootSituation;

	public final SituationImpl resetSituation() {
		this.usingSituation();
		if (this.themeRootSituation != null) {
			this.themeRootSituation.internalClose();
			return this.themeRootSituation = new SituationImpl(this);
		} else {
			throw new UnsupportedOperationException("非普通会话不支持重置情景对象");
		}
	}

	/**
	 * 主线的当前上下文
	 */
	volatile ContextImpl<?, ?, ?> themeContext;

	/**
	 * 使用情景上下文
	 */
	final ContextImpl<?, ?, ?> usingSituation() {
		final ContextImpl<?, ?, ?> context = this.themeContext;
		if (context == null || context.thread != Thread.currentThread()) {
			throw new UnsupportedOperationException("非界面线程禁止访问情景接口");
		}
		return context;
	}

	/**
	 * 主线数据
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
	 * 消息队列的尾部
	 */
	private PendingMessageImpl<?> pendingTail;

	/**
	 * 从等待消息列队中移除<br>
	 * 调用该方法前必须锁定当前theme对象
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
			throw new UnsupportedOperationException("系统会话不支持此调用");
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

	// //////////////////// 本地化 //////////////////////
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
	// 缓存相关

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
