package com.jiuqi.dna.core.impl;

import java.net.Proxy;
import java.net.URL;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.RemoteLoginInfo;
import com.jiuqi.dna.core.RemoteLoginLife;
import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.SiteState;
import com.jiuqi.dna.core.TreeNode;
import com.jiuqi.dna.core.TreeNodeFilter;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.auth.RoleAuthorityChecker;
import com.jiuqi.dna.core.auth.UserAuthorityChecker;
import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.da.RecordIterateAction;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.info.ErrorInfoDefine;
import com.jiuqi.dna.core.def.info.HintInfoDefine;
import com.jiuqi.dna.core.def.info.InfoDefine;
import com.jiuqi.dna.core.def.info.InfoKind;
import com.jiuqi.dna.core.def.info.ProcessInfoDefine;
import com.jiuqi.dna.core.def.info.WarningInfoDefine;
import com.jiuqi.dna.core.def.model.ModelScriptContext;
import com.jiuqi.dna.core.def.model.ModelScriptEngine;
import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDefine;
import com.jiuqi.dna.core.def.query.ModifyStatementDeclarator;
import com.jiuqi.dna.core.def.query.ModifyStatementDefine;
import com.jiuqi.dna.core.def.query.ORMDeclarator;
import com.jiuqi.dna.core.def.query.QueryStatementDeclarator;
import com.jiuqi.dna.core.def.query.QueryStatementDeclare;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.def.query.StatementDeclarator;
import com.jiuqi.dna.core.def.query.StatementDeclare;
import com.jiuqi.dna.core.def.query.StatementDefine;
import com.jiuqi.dna.core.def.query.StoredProcedureDeclarator;
import com.jiuqi.dna.core.def.query.StoredProcedureDefine;
import com.jiuqi.dna.core.def.table.EntityTableDeclarator;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.exception.AbortException;
import com.jiuqi.dna.core.exception.DeadLockException;
import com.jiuqi.dna.core.exception.DisposedException;
import com.jiuqi.dna.core.exception.EndProcessException;
import com.jiuqi.dna.core.exception.NoAccessAuthorityException;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.exception.SessionDisposedException;
import com.jiuqi.dna.core.exception.SituationReentrantException;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefine;
import com.jiuqi.dna.core.impl.CacheDefine.PutPolicy;
import com.jiuqi.dna.core.impl.ServiceBase.CaseTester;
import com.jiuqi.dna.core.impl.SessionImpl.SessionCacheGroupContainer;
import com.jiuqi.dna.core.info.Info;
import com.jiuqi.dna.core.info.InfoInterrupt;
import com.jiuqi.dna.core.internal.db.datasource.DataSourceRef;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.invoke.AsyncHandle;
import com.jiuqi.dna.core.invoke.AsyncState;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.invoke.Asynchronous;
import com.jiuqi.dna.core.invoke.Clustered;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.invoke.Synchronous;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.invoke.TaskState;
import com.jiuqi.dna.core.invoke.Waitable;
import com.jiuqi.dna.core.license.LicenseEntry;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.resource.CategorialResourceModifier;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceHandle;
import com.jiuqi.dna.core.resource.ResourceService.WhenExists;
import com.jiuqi.dna.core.resource.ResourceStub;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.service.AsyncInfo;
import com.jiuqi.dna.core.service.AsyncInfo.AwaitSchedule;
import com.jiuqi.dna.core.service.NativeDeclaratorResolver;
import com.jiuqi.dna.core.service.ReliableRemoteServiceInvoker;
import com.jiuqi.dna.core.service.RemoteServiceInvoker;
import com.jiuqi.dna.core.service.ServiceInvoker;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.core.spi.application.SessionDisposeEvent;
import com.jiuqi.dna.core.testing.TestContext;
import com.jiuqi.dna.core.type.GUID;

/**
 * 上下文实现类
 * 
 * @author gaojingxin
 * 
 */
@SuppressWarnings("deprecation")
class ContextImpl<TFacadeM, TImplM extends TFacadeM, TKeysHolderM> implements
		ResourceContext<TFacadeM, TImplM, TKeysHolderM>,
		CategorialResourceModifier<TFacadeM, TImplM, TKeysHolderM>, ContextSPI,
		NativeDeclaratorResolver, RemoteServiceInvoker,
		ReliableRemoteServiceInvoker {

	/**
	 * 上下文对应的事务
	 */
	final Transaction transaction;

	/**
	 * 会话中的下一个
	 */
	private ContextImpl<?, ?, ?> nextInSession;
	/**
	 * 会话中的前一个
	 */
	private ContextImpl<?, ?, ?> prevInSession;

	final ContextImpl<?, ?, ?> nextInSession() {
		return this.nextInSession;
	}

	final ContextImpl<?, ?, ?> joinChain(ContextImpl<?, ?, ?> one) {
		this.nextInSession = one;
		if (one != null) {
			one.prevInSession = this;
		}
		return this;
	}

	final ContextImpl<?, ?, ?> leaveChain(ContextImpl<?, ?, ?> one) {
		final ContextImpl<?, ?, ?> nextInSession = this.nextInSession;
		final ContextImpl<?, ?, ?> prevInSession = this.prevInSession;
		if (prevInSession != null) {
			prevInSession.nextInSession = nextInSession;
			this.prevInSession = null;
		}
		if (nextInSession != null) {
			nextInSession.prevInSession = prevInSession;
			this.nextInSession = null;
		}
		if (this == one) {
			return nextInSession;
		}
		return one;
	}

	final void cancelChain() {
		ContextImpl<?, ?, ?> context = this;
		do {
			final ContextImpl<?, ?, ?> next = context.nextInSession;
			context.nextInSession = null;// helpGC
			try {
				context.cancel();
			} catch (Throwable e) {
				// 忽略
			}
			context = next;
		} while (context != null);
	}

	final static ContextImpl<?, ?, ?> toContext(Context anInterface) {
		if (anInterface instanceof ContextImpl<?, ?, ?>) {
			return (ContextImpl<?, ?, ?>) anInterface;
		} else if (anInterface instanceof SituationImpl) {
			return ((SituationImpl) anInterface).usingSituation();
		} else if (anInterface == null) {
			return null;
		} else {
			throw new IllegalArgumentException("无效的Context");
		}
	}

	/**
	 * 登陆信息对象
	 */
	public final SessionImpl getLogin() {
		this.checkValid();
		return this.session;
	}

	public final float getResistance() {
		this.checkValid();
		return 0;
	}

	/**
	 * 抛出的异常对象
	 * 
	 * @param throwable
	 *            需要抛出的异常对象
	 */
	public final RuntimeException throwThrowable(Throwable throwable) {
		return Utils.tryThrowException(throwable);
	}

	public final void updateSpace(String spacePath, char spaceSeparator) {
		this.checkValid();
		this.occorAt = this.session.application.getDefaultSite().tryLocateSpace(spacePath, spaceSeparator);
	}

	public final SiteState getSiteState() {
		return this.occorAt.site.state;
	}

	public final GUID getSiteID() {
		return this.occorAt.site.id;
	}

	public final int getSiteSimpleID() {
		return this.occorAt.site.asSimpleID();
	}

	public final <TFacade> void ensureResourceInited(Class<TFacade> facadeClass) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		this.getCacheGroup(facadeClass).ensureInitialized(this.transaction);
	}

	// ///////////////////////////////////////////////////////
	// /////// 任务处理
	// ///////////////////////////////////////////////////////
	/**
	 * 处理任务
	 * 
	 * @param task
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	final void serviceHandleTask(Task<?> task, ServiceInvokeeBase methodHandler) {
		final ServiceBase<?> handlerService = methodHandler.getService();
		final SpaceNode occorAtSave = handlerService.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		TaskState exceptionState = TaskState.PREPARERROR;
		try {
			Utils.taskAccessor.setTaskState(task, TaskState.PREPARING);
			methodHandler.prepare(this, task);
			Utils.taskAccessor.setTaskState(task, TaskState.PREPARED);
			List<Task<?>> subTasks = Utils.taskAccessor.getSubTasks(task);
			if (subTasks != null) {
				for (int i = 0, c = subTasks.size(); i < c; i++) {
					Task<?> subTask = subTasks.get(i);
					this.serviceHandleTask(subTask, handlerService.space.getTaskHandler(subTask.getClass(), subTask.getMethod(), this.getInvokeeQueryMode()));
				}
			}
			Utils.taskAccessor.setTaskState(task, TaskState.PROCESSING);
			exceptionState = TaskState.PROCESSERROR;
			methodHandler.handle(this, task);
			if (this.exception != null) {
				throw this.exception;
			}
			Utils.taskAccessor.setTaskState(task, TaskState.PROCESSED);
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			Utils.taskAccessor.setTaskState(task, exceptionState);
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	/**
	 * 处理某任务
	 * 
	 * @param task
	 *            待处理的任务
	 * @param method
	 *            任务的方法
	 * @param tranMode
	 *            任务的事务模式
	 * @throws Throwable
	 *             处理错误异常
	 */
	public final <TMethod extends Enum<TMethod>> void handle(
			Task<TMethod> task, TMethod method) throws DeadLockException {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		if (method == null) {
			throw new NullArgumentException("method");
		}
		this.checkValid();
		@SuppressWarnings("unchecked")
		ServiceInvokeeBase methodHandler = this.occorAt.getTaskHandler(task.getClass(), method, this.getInvokeeQueryMode());
		method = Utils.taskAccessor.setTaskMethod(task, method);
		try {
			this.serviceHandleTask(task, methodHandler);
		} finally {
			Utils.taskAccessor.setTaskMethod(task, method);
		}
	}

	/**
	 * 处理某任务
	 * 
	 * @param task
	 *            待处理的任务
	 * @param method
	 *            任务的方法
	 * @throws Throwable
	 *             处理错误异常
	 */
	public final void handle(SimpleTask task) throws DeadLockException {
		this.handle(task, None.NONE);
	}

	/**
	 * 异步处理任务
	 */
	@SuppressWarnings("unchecked")
	private final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> doAsyncHandle(
			TTask task, TMethod method, AsyncInfo info) {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		if (method == null) {
			throw new NullArgumentException("method");
		}
		this.checkValid();
		ServiceInvokeeBase methodHandler = this.occorAt.getTaskHandler(task.getClass(), method, this.getInvokeeQueryMode());
		Utils.taskAccessor.setTaskMethod(task, method);
		Utils.taskAccessor.setTaskState(task, TaskState.PREPARING);
		return new AsyncTaskImpl<TTask, TMethod>(this.session, this.occorAt, task, methodHandler, info, info == null ? null : this.awaitOn(info.awaitSchedule));
	}

	private final Object awaitOn(AwaitSchedule awaitSchedule) {
		if (awaitSchedule == null) {
			return null;
		}
		switch (awaitSchedule) {
		case NONE:
			return null;
		case AFTER_CURRENT_CONTEXT_ALWAYS: {
			if (this.afterCurrentAlways == null) {
				return this.afterCurrentAlways = new Object();
			}
			return this.afterCurrentAlways;
		}
		case AFTER_CURRENT_CONTEXT_SUCCESS: {
			if (this.afterCurrentSuccess == null) {
				return this.afterCurrentSuccess = new Object();
			}
			return this.afterCurrentSuccess;
		}
		case AFTER_CURRENT_CONTEXT_EXCEPTION: {
			if (this.afterCurrentException == null) {
				return this.afterCurrentException = new Object();
			}
			return this.afterCurrentException;
		}
		default:
			throw new IllegalStateException();
		}
	}

	private Object afterCurrentAlways;
	private Object afterCurrentSuccess;
	private Object afterCurrentException;

	/**
	 * 异步处理任务
	 */
	public final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method) {
		return this.doAsyncHandle(task, method, null);
	}

	/**
	 * 异步处理简单任务
	 */
	public final <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task) {
		return this.doAsyncHandle(task, None.NONE, null);
	}

	/**
	 * 空事件句柄
	 * 
	 * @author gaojingxin
	 * 
	 */
	private static final class EmptyEventHandle implements Waitable,
			AsyncHandle {

		public final int fetchInfos(List<Info> to) {
			return 0;
		}

		public final void waitStop(long timeout) throws InterruptedException {
		}

		public final void cancel() {
		}

		public final Throwable getException() {
			return null;
		}

		public final float getProgress() {
			return 1f;
		}

		public final AsyncState getState() {
			return AsyncState.FINISHED;
		}
	}

	private static EmptyEventHandle emptyEventHandle = new EmptyEventHandle();

	private final InvokeeQueryMode getInvokeeQueryMode() {
		if (this.depth == 0 && this.session.kind == SessionKind.REMOTE && this.kind != ContextKind.INTERNAL) {
			return InvokeeQueryMode.FROM_OTHER_SITE;
		} else {
			return InvokeeQueryMode.IN_SITE;
		}
	}

	private static void checkEvent(Event event) {
		if (event == null) {
			throw new NullArgumentException("event");
		}
		if (event instanceof SessionDisposeEvent) {
			throw new UnsupportedOperationException("不支持该类别的事件调用");
		}
	}

	/**
	 * 异步的触发事件
	 * 
	 * <p>
	 * 该方法立即返回。
	 * 
	 * @param event
	 *            事件对象
	 */
	public final AsyncHandle occur(Event event) {
		checkEvent(event);
		this.checkValid();
		final Class<?> c = event.getClass();
		if (c.getAnnotation(Synchronous.class) != null) {
			throw new UnsupportedOperationException("不允许异步的触发事件：" + c.getName() + "声明为同步事件。");
		}
		final EventListenerChain chain = this.occorAt.collectEvent(c, null, null, null, this.getInvokeeQueryMode());
		final ApplicationImpl app = this.occorAt.site.application;
		if (chain == null) {
			return emptyEventHandle;
		} else if (c.getAnnotation(Clustered.class) == null) {
			return new AsyncEventImpl(this.session, this.occorAt, chain, event);
		} else if (app.netNodeManager.thisCluster.multiNodes) {
			// 指定为集群异步事件，运行时期望为集群环境。
			// TODO 用户身份？
			for (URL other : app.netNodeManager.thisCluster.otherNodes()) {
				try {
					NetNodeToken token = app.netNodeManager.queryRemoteNodeID(other, null);
					if (!token.appID.equals(app.localNodeID)) {
						this.newReliableRemoteServiceInvoker(other).occurEvent(event);
					}
				} catch (Throwable e) {
					DNALogManager.getLogger("core/invoke").logWarn(this, "未能转发集群事件[" + c.getName() + "]到节点[" + other + "]。", false);
				}
			}
			return new AsyncEventImpl(this.session, this.occorAt, chain, event);
		} else {
			// 指定为集群异步事件，运行时期望为单节点。
			return new AsyncEventImpl(this.session, this.occorAt, chain, event);
		}
	}

	/**
	 * @return 返回在独立事务情况下每步是否发生异常
	 */
	@SuppressWarnings("unchecked")
	final boolean processEvents(EventListenerChain chain, Event event,
			boolean withinTrans) {
		final short invokeDepthSave1 = this.invokeDepth;
		final float contextProgressSave1 = this.contextProgress;
		float progressQuotietySave1 = this.beginContextInvoke();
		final SpaceNode occorAtSave1 = this.occorAt;
		boolean hasException = false;
		try {
			if (chain == null) {
				return true;
			}
			float step = 1f / chain.getChainSize();
			do {
				this.setNextStep(step);
				final ServiceInvokeeBase invokee = chain.listener;
				final SpaceNode occorAtSave = invokee.getService().updateContextSpace(this);
				final short invokeDepthSave = this.invokeDepth;
				final float contextProgressSave = this.contextProgress;
				float progressQuotietySave = this.beginContextInvoke();
				try {
					invokee.occur(this, event);
				} catch (Throwable e) {
					this.exception = e;
					if (withinTrans) {
						progressQuotietySave = -progressQuotietySave;// 标记出过错
						throw Utils.tryThrowException(e);
					} else {
						this.catcher.catchException(e, event);
						hasException = true;
					}
				} finally {
					this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
					if (!withinTrans) {
						this.resolveTrans();
					}
				}
				chain = chain.next;
			} while (chain != null);
		} catch (Throwable e) {
			progressQuotietySave1 = -progressQuotietySave1;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave1, contextProgressSave1, progressQuotietySave1, invokeDepthSave1);
		}
		return hasException;
	}

	public final boolean dispatch(Event event) {
		return this.dispatch(event, true, null);
	}

	public final boolean dispatch(Event event, Object key1) {
		return this.dispatch(event, true, key1);
	}

	/**
	 * 同步的转发事件
	 * 
	 * @param event
	 * @param local
	 * @return
	 */
	final boolean dispatch(Event event, boolean local, Object key1) {
		checkEvent(event);
		this.checkValid();
		final Class<?> clz = event.getClass();
		if (local) {
			if (clz.getAnnotation(Asynchronous.class) != null) {
				throw new UnsupportedOperationException("不允许同步的触发事件：" + clz.getName() + "声明为异步事件。");
			}
			if (clz.getAnnotation(Clustered.class) != null) {
				throw new UnsupportedOperationException("不允许同步的触发集群事件：" + clz.getName() + "。");
			}
		}
		EventListenerChain chain = this.occorAt.collectEvent(event.getClass(), key1, null, null, this.getInvokeeQueryMode());
		if (chain == null) {
			return false;
		}
		this.processEvents(chain, event, true);
		return true;
	}

	/**
	 * 等待异步处理的全部结束
	 */
	public final void waitFor(AsyncHandle one, AsyncHandle... others)
			throws InterruptedException {
		this.waitFor(0, one, others);
	}

	/**
	 * 异步处理简单任务
	 */
	public final <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task, AsyncInfo info) {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		return this.doAsyncHandle(task, None.NONE, info);
	}

	/**
	 * 异步处理任务
	 */
	public final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method, AsyncInfo info) {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		return this.doAsyncHandle(task, method, info);
	}

	public final void dispose() {
		if (!this.disposed) {
			if (this.thread != Thread.currentThread()) {
				throw new UnsupportedOperationException("不允许销毁其他现成创建的上下文");
			}
			this.depth = 0;
			this.invokeDepth = 0;
			try {
				this.transaction.unbindContext(this.exception == null);
			} catch (Throwable e) {
				// 忽略
			}
			try {
				this.disposeModelScriptContexts();
			} catch (Throwable e) {
				// 忽略
			}
			try {
				if (this.afterCurrentAlways != null) {
					this.occorAt.site.application.overlappedManager.signalAwaitingScheduled(this.afterCurrentAlways, true);
				}
			} catch (Throwable e) {
			}
			try {
				if (this.afterCurrentSuccess != null) {
					this.occorAt.site.application.overlappedManager.signalAwaitingScheduled(this.afterCurrentSuccess, this.exception == null);
				}
			} catch (Throwable e) {
			}
			try {
				if (this.afterCurrentException != null) {
					this.occorAt.site.application.overlappedManager.signalAwaitingScheduled(this.afterCurrentException, this.exception != null);
				}
			} catch (Throwable e) {
			}
			this.disposed = true;
			final ThreadLocal<ContextImpl<?, ?, ?>> contextLocal = this.session.application.contextLocal;
			ContextImpl<?, ?, ?> top = contextLocal.get();
			if (top == this) {
				contextLocal.set(this.upperInThread);
			} else {
				ContextImpl<?, ?, ?> upper = top.upperInThread;
				for (;;) {
					if (upper == null) {
						break;
					} else if (upper == this) {
						top.upperInThread = this.upperInThread;
						break;
					} else {
						top = upper;
					}
				}
			}
			this.upperInThread = null;
			this.session.contextDisposed(this);
			ContextVariableIntl.clearContextVariable();
		}
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
	/**
	 * 会话当前所处的深度，包括调用与过程造成的深度增加
	 */
	private short depth;

	/**
	 * 会话当前所处的深度，包括调用与过程造成的深度增加
	 */
	final short getDepth() {
		return this.depth;
	}

	/**
	 * 当前调用深度
	 */
	private short invokeDepth;

	/**
	 * 处理中抛出的异常
	 */
	private Throwable exception;

	final <TResult, TKey1, TKey2, TKey3> TResult serviceProvideResult(
			ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultProvider,
			TKey1 key1, TKey2 key2, TKey3 key3) {
		if (resultProvider == null) {
			throw new NullArgumentException("resultProvider");
		}
		final SpaceNode occorAtSave = resultProvider.getService().updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			TResult result;
			if (key1 == null) {
				result = resultProvider.provide(this);
			} else if (key2 == null) {
				result = resultProvider.provide(this, key1);
			} else if (key3 == null) {
				result = resultProvider.provide(this, key1, key2);
			} else {
				result = resultProvider.provide(this, key1, key2, key3);
			}
			if (this.exception != null) {
				throw this.exception;
			}
			return result;
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	/**
	 * 获取列表结果
	 */
	final <TResult, TKey1, TKey2, TKey3> void serviceProvideList(
			List<TResult> resultList,
			ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultListProvider,
			TKey1 key1, TKey2 key2, TKey3 key3) {
		if (resultListProvider == null) {
			throw new NullArgumentException("resultListProvider");
		}
		if (resultList == null) {
			throw new NullArgumentException("resultListProvider");
		}
		final SpaceNode occorAtSave = resultListProvider.getService().updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			if (key1 == null) {
				resultListProvider.provide(this, resultList);
			} else if (key2 == null) {
				resultListProvider.provide(this, key1, resultList);
			} else if (key3 == null) {
				resultListProvider.provide(this, key1, key2, resultList);
			} else {
				resultListProvider.provide(this, key1, key2, key3, resultList);
			}
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	public final <TObject> TObject newObject(Class<TObject> clazz,
			Object... aditionalArgs) {
		return this.occorAt.newObjectInNode(clazz, this, aditionalArgs);
	}

	public final <TDeclarator extends DeclaratorBase> TDeclarator resolveDeclarator(
			Class<TDeclarator> declaratorClass, Object... aditionalArgs) {
		if (declaratorClass == null) {
			throw new NullArgumentException("declaratorClass");
		}
		final TDeclarator declarator;
		synchronized (DeclaratorBase.class) {
			DeclaratorBase.newInstanceByCore = this;
			try {
				declarator = this.occorAt.newObjectInNode(declaratorClass, this, aditionalArgs);
			} finally {
				DeclaratorBase.newInstanceByCore = null;
			}
		}
		declarator.tryDeclareUseRef(this);
		if (TableDeclarator.class.isAssignableFrom(declaratorClass)) {
			try {
				this.getDBAdapter().syncTable(((TableDefineImpl) ((TableDeclarator) declarator).getDefine()));
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		}
		return declarator;
	}

	@SuppressWarnings("unchecked")
	final void disposeService(ServiceBase service) throws Throwable {
		final SpaceNode occorAtSave = service.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			service.dispose(this);
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;
			throw this.exception = e;
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
			this.resolveTrans();
		}
	}

	@SuppressWarnings("unchecked")
	final void initService(ServiceBase service) throws Throwable {
		final SpaceNode occorAtSave = service.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			try {
				service.resolveNativeDeclarator(this, this);
			} catch (Throwable e) {
				this.exception = e;
				throw e;
			} finally {
				this.resolveTrans();
			}
			service.init(this);
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;
			throw this.exception = e;
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
			this.resolveTrans();
		}
	}

	public final User changeLoginUser(User user) {
		if (user == null) {
			throw new NullArgumentException("user");
		}
		final SessionImpl session = this.session;
		switch (session.kind) {
		case SYSTEM:
			throw new UnsupportedOperationException("系统会话不支持切换用户。");
		case NORMAL:
			if (this.kind != ContextKind.SITUATION) {
				throw new UnsupportedOperationException("普通会话只支持在主线程中切换用户。");
			}
			break;
		default:
			break;
		}
		if (user.getState() == ActorState.DISABLE) {
			if (user == BuildInUser.debugger) {
				throw new IllegalStateException("调试账号已被禁用");
			} else {
				throw new IllegalStateException("账号[" + user.getName() + "]已被禁用。");
			}
		}
		
		if(!AppUtil.getDefaultApp().getLoginController().tryLogin(user)){
			throw new UnsupportedOperationException("系统中用户数量已到达上限，请稍后再尝试登录.");
		}
		
		final User oldUser = session.changeUser(user);
		this.currentACVersion = session.currentIdentifyIdentifier;
		this.resetACLCache();
		return oldUser;
	}

	public final void remoteLogin(String username, GUID passwordMD5) {
		final float contextProgressSave = this.contextProgress;
		final short invokeDepthSave = this.invokeDepth;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			final User u = this.find(User.class, username);
			if (u == null || !u.validatePassword(passwordMD5)) {
				throw new IllegalArgumentException("远程调用：用户名或者密码不正确");
			}
			this.changeLoginUser(u);
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(this.occorAt, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	final long createTime;
	long pm_cputime;
	/**
	 * 会话
	 */
	final SessionImpl session;
	// 创建请求的线程
	final Thread thread;
	// 当前被调用者
	SpaceNode occorAt;
	// 当前资源管理器
	ResourceServiceBase<TFacadeM, TImplM, TKeysHolderM> occorAtResourceService;

	// 异常收集器
	final ExceptionCatcher catcher;

	public static final void internalWaitFor(long timeout, AsyncHandle one,
			AsyncHandle[] others) throws InterruptedException {
		if (timeout > 0) {
			long endT = System.currentTimeMillis() + timeout;
			if (one instanceof Waitable) {
				((Waitable) one).waitStop(timeout);
			}
			if (others != null) {
				for (AsyncHandle other : others) {
					if (other instanceof Waitable && (timeout = endT - System.currentTimeMillis()) > 0) {
						((Waitable) other).waitStop(timeout);
					}
				}
			}
		} else {
			if (one instanceof Waitable) {
				((Waitable) one).waitStop(0);
			}
			if (others != null) {
				for (AsyncHandle other : others) {
					if (other instanceof Waitable) {
						((Waitable) other).waitStop(0);
					}
				}
			}
		}
	}

	/**
	 * 等待异步处理的全部结束
	 * 
	 * @param nanosTimeout
	 *            超时纳秒数，0代表永远不超时
	 */
	public final void waitFor(long timeout, AsyncHandle one,
			AsyncHandle... others) throws InterruptedException {
		this.checkValid();
		internalWaitFor(timeout, one, others);
	}

	// final LocalCluster localCluster;

	final ContextKind kind;

	public final ContextKind getKind() {
		return this.kind;
	}

	/**
	 * 上下文栈
	 */
	private ContextImpl<?, ?, ?> upperInThread;
	/**
	 * 远程调用的发起者，null表示当前调用是本地调用
	 */
	final NetNodeImpl remoteCaller;

	ContextImpl(SessionImpl session, SpaceNode occorAt, ContextKind kind,
			Transaction transaction, NetNodeImpl remoteCaller)
			throws SessionDisposedException, SituationReentrantException {
		if (session == null) {
			throw new NullArgumentException("session");
		}
		if (occorAt == null) {
			throw new NullArgumentException("occorAt");
		}
		if (kind == null) {
			throw new NullArgumentException("kind");
		}
		if (transaction == null) {
			throw new NullArgumentException("transaction");
		}
		this.remoteCaller = remoteCaller;
		occorAt.site.state.checkContextKind(session.kind, kind);
		this.session = session;
		occorAt.updateContextSpace(this);
		this.catcher = session.application.catcher;
		this.thread = Thread.currentThread();
		this.createTime = System.currentTimeMillis();
		this.progressQuotiety = 1f;
		switch (this.kind = kind) {
		case TRANSIENT:
		case INITER:
			this.contextProgressNextStep = 1f;
			break;
		}
		this.currentACVersion = session.currentIdentifyIdentifier;
		this.transaction = transaction;
		this.upperInThread = session.contextCreated(this);
		transaction.bindContext(this);
	}

	// ////////////////////////
	// ////进度相关
	// ////////////////////////
	float progress;
	// 进度系数
	private float progressQuotiety;
	// 当前上下文下一步完成的位置
	private float contextProgressNextStep;
	// 当前上下文的进度
	private float contextProgress;
	// 正在被外界取消
	private volatile boolean canceling;

	/**
	 * 外部线程通知内部终止
	 */
	final void cancel() {
		this.canceling = true;
		final RuntimeException ex = new RuntimeException("请求[" + this.thread.getName() + "]被终止。");
		try {
			DNALogManagerInternal.getLogger("core/context").logFatal(this, ex, false);
		} catch (Throwable e) {
			this.catcher.catchException(e, this);
		}
		if (this.thread != Thread.currentThread()) {
			this.transaction.interupt();
			// 终止其他等待，诸如异步等待，网络连接等待等
			try {
				this.thread.interrupt();
			} catch (Throwable e) {
				this.catcher.catchException(e, this);
			}
		}
	}

	private boolean disposed;

	/**
	 * 进入子过程
	 * 
	 * @return progressQuotiety
	 */
	private final float enterFrame() {
		float progressQuotietySave = this.progressQuotiety;
		this.progressQuotiety *= this.contextProgressNextStep;
		this.contextProgress = 0;
		this.contextProgressNextStep = 0;
		this.depth++;
		return progressQuotietySave;
	}

	/**
	 * 进入子调用
	 * 
	 * @return progressQuotiety
	 */
	final float beginContextInvoke() {
		float f = this.enterFrame();
		this.invokeDepth = this.depth;
		return f;
	}

	final void endContextInvoke(SpaceNode occorAtSave,
			float contextProgressSave, float progressQuotietySave,
			short invokeDepthSave) {
		try {
			while (this.depth > this.invokeDepth) {
				this.endProcess();
			}
		} finally {
			this.depth = this.invokeDepth;// 防止上面的循环中出现异常
			this.leaveFrame(contextProgressSave, progressQuotietySave);
			occorAtSave.updateContextSpace(this);
			this.invokeDepth = invokeDepthSave;
			if (invokeDepthSave == 0 && this.exception instanceof AbortException) {
				// 在最外层针对AbortException回滚事务
				this.resolveTrans();
			}
		}
	}

	/**
	 * 离开子过程
	 * 
	 * @param occorAtSave
	 *            如果为空，表示是process否则为invoke
	 */
	private final void leaveFrame(float contextProgressSave,
			float progressQuotietySave) {
		this.transaction.leaveFrame(this.depth);
		if (progressQuotietySave > 0f) {// 成功返回
			float p = this.progress + (1 - this.contextProgress) * this.progressQuotiety;
			this.progress = p <= 1 ? p : 0.9999f;
			this.contextProgress = contextProgressSave + this.progressQuotiety / progressQuotietySave;
			this.contextProgressNextStep = 0f;
		} else if (progressQuotietySave < 0f) {// 有错误
			progressQuotietySave = -progressQuotietySave;
			float contextProgressStep = this.progressQuotiety / progressQuotietySave;
			// 出错后的部分放入下一步中，如果用户越过异常则进度会记入下一次
			this.contextProgressNextStep = contextProgressStep * (1 - this.contextProgress);
			// 出错后的调整上下文进度到出错位置
			this.contextProgress = contextProgressSave + contextProgressStep * this.contextProgress;
		} else {// 0子进度为零
			this.contextProgress = contextProgressSave;
			this.contextProgressNextStep = 0f;
		}
		this.progressQuotiety = progressQuotietySave;
		this.depth--;
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// 查询资源，如果返回空，查询结果。
	// 不允许返回空（null）值，如果最终结果为空，抛出MissingObjectException异常。
	// //////////////////////////////////////////////////////////////////////
	// ////
	public final <TFacade> TFacade get(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass);
		return this.internalGet(null, facadeClass, null, null, null, null, null, null, null);
	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalGet(null, facadeClass, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalGet(null, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalGet(null, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		return this.internalGet(null, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException,
			MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalGet(operation, facadeClass, null, null, null, null, null, null, null);
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalGet(operation, facadeClass, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException, MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalGet(operation, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException, MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalGet(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws UnsupportedOperationException,
			MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		return this.internalGet(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// 查询资源，如果返回空，查询结果。允许返回空（null）值。
	// //////////////////////////////////////////////////////////////////////
	// ////
	public final <TFacade> TFacade find(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass);
		return this.internalFind(null, facadeClass, null, null, null, null, null, null, null);
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalFind(null, facadeClass, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalFind(null, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalFind(null, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		return this.internalFind(null, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		return this.internalFind(operation, facadeClass, null, null, null, null, null, null, null);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalFind(operation, facadeClass, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalFind(operation, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalFind(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		return this.internalFind(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	final <TFacade> TFacade internalGet(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		final TFacade result = this.internalFind(operation, facadeClass, key1Class, key2Class, key3Class, key1, key2, key3, otherKeys);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3 + "]对象");
		}
		return result;
	}

	final <TFacade> TFacade internalFind(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		this.checkValid();
		final CacheGroup<TFacade, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final CacheHolderIndex<TFacade, ?, ?> itemIndex = group.findIndex(key1Class, key2Class, key3Class);
			if (itemIndex != null) {
				final CacheHolder<TFacade, ?, ?> item = itemIndex.findHolder(key1, key2, key3, this.transaction);
				if (item != null) {
					if (operation != null) {
						final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
						final boolean hasAuthority;
						resourceService.callBeforeAccessAuthorityResource(this);
						try {
							hasAuthority = this.getOperationAuthorityChecker().hasAuthority(operation, item);
						} finally {
							resourceService.callEndAccessAuthorityResource(this);
						}
						if (!hasAuthority) {
							return null;
						}
					}
					return item.tryGetValue(this.transaction);
				}
			}
		}
		if (operation != null) {
			return null;
		}
		if (otherKeys == null || otherKeys.length == 0) {
			final InvokeeQueryMode mode = this.getInvokeeQueryMode();
			ServiceInvokeeBase<TFacade, Context, Object, Object, Object> provider = this.occorAt.findResultProvider(facadeClass, key1, key2, key3, mode);
			if (provider != null) {
				return this.serviceProvideResult(provider, key1, key2, key3);
			} else {
				return this.occorAt.tryFindResult(facadeClass, key1, key2, key3, mode);
			}
		} else {
			return null;
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass);
		return this.internalGetList(null, facadeClass, null, null, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalGetList(null, facadeClass, null, null, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalGetList(null, facadeClass, null, null, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalGetList(null, facadeClass, null, null, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		return this.internalGetList(null, facadeClass, null, null, key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(null, facadeClass, filter, null, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(null, facadeClass, filter, null, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(null, facadeClass, filter, null, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(null, facadeClass, filter, null, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(null, facadeClass, filter, null, key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, filter, sortComparator, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, filter, sortComparator, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, filter, sortComparator, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, filter, sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, filter, sortComparator, key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, null, sortComparator, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, null, sortComparator, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, null, sortComparator, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, null, sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(null, facadeClass, null, sortComparator, key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		return this.internalGetList(operation, facadeClass, null, null, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalGetList(operation, facadeClass, null, null, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalGetList(operation, facadeClass, null, null, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalGetList(operation, facadeClass, null, null, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		return this.internalGetList(operation, facadeClass, null, null, key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(null, facadeClass, filter, null, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(operation, facadeClass, filter, null, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(operation, facadeClass, filter, null, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(operation, facadeClass, filter, null, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetList(operation, facadeClass, filter, null, key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, null, sortComparator, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, null, sortComparator, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, null, sortComparator, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, null, sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, null, sortComparator, key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, filter, sortComparator, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, filter, sortComparator, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, filter, sortComparator, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, filter, sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetList(operation, facadeClass, filter, sortComparator, key1, key2, key3, otherKeys);
	}

	final <TFacade> List<TFacade> internalGetList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		this.checkValid();
		if (key1 == null) {
			final List<TFacade> result = this.tryGetCacheValueList(operation, resultClass, filter, sortComparator);
			if (result != null) {
				return result;
			} else if (operation != null) {
				throw ServiceInvokeeBase.noResourceListException(resultClass, key1, key2, key3, otherKeys);
			}
		}
		final DnaArrayList<TFacade> list = new DnaArrayList<TFacade>();
		int ls = -1;
		fillList: {
			if (otherKeys != null && otherKeys.length != 0) {
				throw ServiceInvokeeBase.noResourceListException(resultClass, key1, key2, key3, otherKeys);
			}
			final InvokeeQueryMode mode = this.getInvokeeQueryMode();
			ServiceInvokeeBase<TFacade, Context, Object, Object, Object> listProvider = this.occorAt.findResultListProvider(resultClass, key1, key2, key3, mode);
			if (listProvider != null) {
				this.serviceProvideList(list, listProvider, key1, key2, key3);
				if ((ls = list.size()) > 0) {
					break fillList;
				}
			}
			if (this.occorAt.tryFillList(list, resultClass, key1, key2, key3, mode)) {
				ls = list.size();
			} else if (ls < 0) {
				throw ServiceInvokeeBase.noListProviderException(resultClass, key1, key2, key3);
			}
		}
		if (ls > 0) {
			list.adjust(filter, sortComparator);
		}
		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final <TFacade> List<TFacade> tryGetCacheValueList(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> comparator) {
		ResourceServiceBase resourceService = this.occorAtResourceService;
		if (resourceService == null || resourceService.facadeClass != facadeClass) {
			resourceService = this.occorAt.findResourceService(facadeClass, this.getInvokeeQueryMode());
			if (resourceService == null) {
				return null;
			}
			resourceService.ensureBuiltCacheDefine();
		}
		final CacheGroup group = this.findCacheGroup(facadeClass);
		if (group == null) {
			return null;
		}
		final List<TFacade> result;
		if (operation != null) {
			resourceService.callBeforeAccessAuthorityResource(this);
			try {
				result = group.tryGetValueList(this.getOperationAuthorityChecker(), operation, this.transaction, filter, comparator);
			} finally {
				resourceService.callEndAccessAuthorityResource(this);
			}
		} else {
			result = group.tryGetValueList(this.transaction, filter, comparator);
		}
		if (result == null) {
			throw new DisposedException("外观类型为[" + this.getFacadeClass() + "]，标识为[" + null + "]的缓存组已被销毁。");
		} else {
			return result;
		}
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass) throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		return this.internalGetTreeNode(null, facadeClass, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, null, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalGetTreeNode(null, facadeClass, null, null, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalGetTreeNode(null, facadeClass, null, null, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalGetTreeNode(null, facadeClass, null, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		return this.internalGetTreeNode(null, facadeClass, null, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, null, sortComparator, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, null, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, null, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		CacheArgumentChecker.check(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(null, facadeClass, filter, sortComparator, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		return this.internalGetTreeNode(operation, facadeClass, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalGetTreeNode(operation, facadeClass, null, null, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalGetTreeNode(operation, facadeClass, null, null, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalGetTreeNode(operation, facadeClass, null, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, null, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, null, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, null, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, sortComparator, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, null, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, sortComparator, key.getClass(), null, null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		if (otherKeys == null) {
			throw new NullArgumentException("otherKeys");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetTreeNode(operation, facadeClass, filter, sortComparator, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3, otherKeys);
	}

	final <TFacade> TreeNodeImpl<TFacade> internalGetTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		this.checkValid();
		final TreeNodeImpl<TFacade> root;
		final boolean cacheDefined;
		final CacheGroup<TFacade, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final CacheTree<TFacade, ?, ?> tree = group.getBindTree();
			if (operation == null) {
				root = tree.tryGetTreeValue(filter, sortComparator, this.transaction);
			} else {
				final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
				resourceService.callBeforeAccessAuthorityResource(this);
				try {
					root = tree.tryGetTreeValue(this.getOperationAuthorityChecker(), operation, filter, sortComparator, this.transaction);
				} finally {
					resourceService.callEndAccessAuthorityResource(this);
				}
			}
			if (root.getElement() != null || root.getChildCount() != 0) {
				return root;
			}
			cacheDefined = true;
		} else {
			root = new TreeNodeImpl<TFacade>(null, null);
			cacheDefined = false;
		}
		if (operation != null) {
			return root;
		}
		this.fillTreeNode(root, facadeClass, filter, sortComparator, null, null, null, cacheDefined);
		return root;
	}

	final <TFacade> TreeNodeImpl<TFacade> internalGetTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator,
			final Class<?> key1Class, final Class<?> key2Class,
			final Class<?> key3Class, Object key1, Object key2, Object key3,
			Object[] otherKeys) {
		this.checkValid();
		final TreeNodeImpl<TFacade> root;
		final boolean cacheDefined;
		final CacheGroup<TFacade, ?, ?> group = this.findCacheGroup(facadeClass);
		findCacheDefine: {
			if (group != null) {
				final CacheHolderIndex<TFacade, ?, ?> itemIndex = group.findIndex(key1Class, key2Class, key3Class);
				if (itemIndex != null) {
					final CacheHolder<TFacade, ?, ?> item = itemIndex.findHolder(key1, key2, key3, this.transaction);
					if (item != null) {
						final CacheTree<TFacade, ?, ?> tree = group.getBindTree();
						if (operation == null) {
							root = tree.tryGetTreeValue(filter, sortComparator, item, this.transaction);
						} else {
							final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
							resourceService.callBeforeAccessAuthorityResource(this);
							try {
								root = tree.tryGetTreeValue(this.getOperationAuthorityChecker(), operation, filter, sortComparator, item, this.transaction);
							} finally {
								resourceService.callEndAccessAuthorityResource(this);
							}
						}
						if (root.getElement() != null || root.getChildCount() != 0) {
							return root;
						}
					} else {
						root = new TreeNodeImpl<TFacade>(null, null);
						if (operation != null) {
							return root;
						}
					}
					cacheDefined = true;
					break findCacheDefine;
				}
			}
			root = new TreeNodeImpl<TFacade>(null, null);
			cacheDefined = false;
		}
		if (otherKeys == null || otherKeys.length == 0) {
			this.fillTreeNode(root, facadeClass, filter, sortComparator, key1, key2, key3, cacheDefined);
		} else {
			throw ServiceInvokeeBase.noResourceTreeException(facadeClass, key1, key2, key3, otherKeys);
		}
		return root;
	}

	private final <TFacade> void fillTreeNode(TreeNodeImpl<TFacade> root,
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, boolean cacheDefined) {
		int absoluteLevel = 0;
		fillTree: {
			final InvokeeQueryMode mode = this.getInvokeeQueryMode();
			ServiceInvokeeBase<TFacade, Context, Object, Object, Object> provider = this.occorAt.findTreeNodeProvider(facadeClass, key1, key2, key3, mode);
			if (provider != null) {
				absoluteLevel = this.serviceProvideTree(root, provider, key1, key2, key3);
				if (!root.isEmpty()) {
					break fillTree;
				}
			}
			absoluteLevel = this.occorAt.tryFillTree(root, facadeClass, key1, key2, key3, mode);
			if (absoluteLevel < 0 && !cacheDefined) {
				throw ServiceInvokeeBase.noTreeProviderException(facadeClass, key1, key2, key3);
			} else if (root.isEmpty()) {
				return;
			}
		}
		root.filterAndSortRecursively(filter, absoluteLevel, 0, sortComparator);
	}

	public final <TResult> AsyncResultImpl<TResult, ?, ?, ?> asyncGet(
			Class<TResult> resultClass) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		return this.internalAsyncGet(resultClass, null, null, null);
	}

	public final <TResult, TKey> AsyncResultImpl<TResult, TKey, ?, ?> asyncGet(
			Class<TResult> resultClass, TKey key) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalAsyncGet(resultClass, key, null, null);
	}

	public final <TResult, TKey1, TKey2> AsyncResultImpl<TResult, TKey1, TKey2, ?> asyncGet(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalAsyncGet(resultClass, key1, key2, null);
	}

	public final <TResult, TKey1, TKey2, TKey3> AsyncResultImpl<TResult, TKey1, TKey2, TKey3> asyncGet(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalAsyncGet(resultClass, key1, key2, key3);
	}

	@SuppressWarnings("unchecked")
	private final <TResult, TKey1, TKey2, TKey3> AsyncResultImpl<TResult, TKey1, TKey2, TKey3> internalAsyncGet(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		this.checkValid();
		ServiceInvokeeBase resultProvider = this.occorAt.findResultProvider(resultClass, key1, key2, key3, this.getInvokeeQueryMode());
		if (resultProvider == null) {
			throw ServiceInvokeeBase.noResultProviderException(resultClass, key1, key2, key3);
		}
		return new AsyncResultImpl<TResult, TKey1, TKey2, TKey3>(this.session, this.occorAt, resultClass, key1, key2, key3, resultProvider);
	}

	public final <TResult> AsyncResultListImpl<TResult, ?, ?, ?> asyncGetList(
			Class<TResult> resultClass) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		return this.internalAsyncGetList(resultClass, null, null, null);
	}

	public final <TResult, TKey1> AsyncResultListImpl<TResult, TKey1, ?, ?> asyncGetList(
			Class<TResult> resultClass, TKey1 key) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalAsyncGetList(resultClass, key, null, null);
	}

	public final <TResult, TKey1, TKey2> AsyncResultListImpl<TResult, TKey1, TKey2, ?> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalAsyncGetList(resultClass, key1, key2, null);
	}

	public final <TResult, TKey1, TKey2, TKey3> AsyncResultListImpl<TResult, TKey1, TKey2, TKey3> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalAsyncGetList(resultClass, key1, key2, key3);
	}

	@SuppressWarnings("unchecked")
	private final <TResult, TKey1, TKey2, TKey3> AsyncResultListImpl<TResult, TKey1, TKey2, TKey3> internalAsyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		this.checkValid();
		ServiceInvokeeBase resultListProvider = this.occorAt.findResultListProvider(resultClass, key1, key2, key3, this.getInvokeeQueryMode());
		if (resultListProvider == null) {
			throw ServiceInvokeeBase.noListProviderException(resultClass, key1, key2, key3);
		}
		return new AsyncResultListImpl<TResult, TKey1, TKey2, TKey3>(this.session, this.occorAt, resultClass, key1, key2, key3, resultListProvider);

	}

	public final <TResult> AsyncResultTreeNodeImpl<TResult, ?, ?, ?> asyncGetTreeNode(
			Class<TResult> resultClass) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		return this.internalAsyncGetTree(resultClass, null, null, null);
	}

	public final <TResult, TKey1> AsyncResultTreeNodeImpl<TResult, TKey1, ?, ?> asyncGetTreeNode(
			Class<TResult> resultClass, TKey1 key) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalAsyncGetTree(resultClass, key, null, null);
	}

	public final <TResult, TKey1, TKey2> AsyncResultTreeNodeImpl<TResult, TKey1, TKey2, ?> asyncGetTreeNode(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalAsyncGetTree(resultClass, key1, key2, null);
	}

	public final <TResult, TKey1, TKey2, TKey3> AsyncResultTreeNodeImpl<TResult, TKey1, TKey2, TKey3> asyncGetTreeNode(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalAsyncGetTree(resultClass, key1, key2, key3);
	}

	// ======================================================================
	// ===
	// Methods from ResourcesModifier
	// ----------------------------------------------------------------------
	// ---

	@SuppressWarnings("unchecked")
	final <TResult, TKey1, TKey2, TKey3> AsyncResultTreeNodeImpl<TResult, TKey1, TKey2, TKey3> internalAsyncGetTree(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		this.checkValid();
		final ServiceInvokeeBase resultTreeProvider = this.occorAt.findTreeNodeProvider(resultClass, key1, key2, key3, this.getInvokeeQueryMode());
		if (resultTreeProvider == null) {
			throw ServiceInvokeeBase.noTreeProviderException(resultClass, key1, key2, key3);
		}
		return new AsyncResultTreeNodeImpl<TResult, TKey1, TKey2, TKey3>(this.session, this.occorAt, resultClass, key1, key2, key3, resultTreeProvider);
	}

	final DBAdapterImpl getDBAdapter() {
		this.checkValid();
		try {
			return this.transaction.getDBAdapter(this.occorAt.getDataSourceRef());
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	final DataSourceRef getDataSourceRef() {
		return this.occorAt.getDataSourceRef();
	}

	final DbMetadata getDbMetadata() throws SQLException {
		return this.occorAt.getDataSourceRef().getDbMetadata();
	}

	/**
	 * 提交或者回滚事务
	 * 
	 * @param catcher
	 */
	public final Throwable resolveTrans() {
		final Throwable e = this.exception;
		this.exception = null;
		this.transaction.finish(e == null);
		return e;
	}

	public final void exception(Throwable exception) {
		if (exception != null) {
			this.exception = exception;
		}
	}

	// /////////////////////////////////////////
	// /////////// DBAdapter
	// /////////////////////////////////////////

	public final GUID newRECID() {
		return this.occorAt.site.application.newRECID();
	}

	public final long newRECVER() {
		return this.occorAt.site.application.newRECVER();
	}

	public final QueryStatementImpl newQueryStatement() {
		return new QueryStatementImpl("runtime");
	}

	public final QueryStatementDeclare newQueryStatement(
			QueryStatementDefine sample) {
		if (sample instanceof MappingQueryStatementImpl) {
			QueryStatementImpl q = new QueryStatementImpl("runtime");
			((MappingQueryStatementImpl) sample).cloneTo(q);
			return q;
		}
		return ((QueryStatementImpl) sample).clone();
	}

	public final InsertStatementImpl newInsertStatement(TableDefine table) {
		this.checkValid();
		return new InsertStatementImpl("runtime", (TableDefineImpl) table);
	}

	public final InsertStatementImpl newInsertStatement(TableDeclarator table) {
		return this.newInsertStatement(table.getDefine());
	}

	public final DeleteStatementImpl newDeleteStatement(TableDefine table) {
		this.checkValid();
		return new DeleteStatementImpl("runtime", table.getName(), (TableDefineImpl) table);
	}

	public final DeleteStatementImpl newDeleteStatement(TableDeclarator table) {
		return this.newDeleteStatement(table.getDefine());
	}

	public final UpdateStatementImpl newUpdateStatement(TableDefine table) {
		this.checkValid();
		return new UpdateStatementImpl("runtime", table.getName(), (TableDefineImpl) table);
	}

	public final UpdateStatementImpl newUpdateStatement(TableDefine table,
			String name) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("名称");
		}
		this.checkValid();
		return new UpdateStatementImpl(name, table.getName(), (TableDefineImpl) table);
	}

	public final UpdateStatementImpl newUpdateStatement(TableDeclarator table) {
		return this.newUpdateStatement(table.getDefine());
	}

	public final UpdateStatementImpl newUpdateStatement(TableDeclarator table,
			String name) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("名称");
		}
		this.checkValid();
		return new UpdateStatementImpl(name, table.getDefine().getName(), (TableDefineImpl) table.getDefine());
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			Class<?> entityClass) {
		if (entityClass == null) {
			throw new NullArgumentException("entityClass");
		}
		this.checkValid();
		return new MappingQueryStatementImpl("runtime", DataTypeBase.getStaticStructDefine(entityClass));
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			Class<?> entityClass, String name) {
		if (entityClass == null) {
			throw new NullArgumentException("entityClass");
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		this.checkValid();
		return new MappingQueryStatementImpl(name, DataTypeBase.getStaticStructDefine(entityClass));
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			EntityTableDeclarator<?> table) {
		if (table == null) {
			throw new NullArgumentException("table");
		}
		this.checkValid();
		MappingQueryStatementImpl sample = (MappingQueryStatementImpl) table.getMappingQueryDefine();
		return new MappingQueryStatementImpl(sample.name, sample.getMappingTarget());
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			StructDefine model) {
		if (model == null) {
			throw new NullArgumentException("model");
		}
		this.checkValid();
		return new MappingQueryStatementImpl("runtime", (StructDefineImpl) model);
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			StructDefine model, String name) {
		if (model == null) {
			throw new NullArgumentException("model");
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("名称");
		}
		this.checkValid();
		return new MappingQueryStatementImpl(name, (StructDefineImpl) model);
	}

	public final DBCommandProxy prepareStatement(StatementDefine statement) {
		return DBAdapterImpl.prepareStatement(this, (IStatement) statement);
	}

	public final DBCommandProxy prepareStatement(CharSequence dnaSql) {
		return DBAdapterImpl.prepareStatement(this, this.parseStatement(dnaSql));
	}

	public final IStatement parseStatement(CharSequence dnaSql) {
		return DNASql.parseDefine(CharSequenceReader.newReader(dnaSql), this, IStatement.class);
	}

	public final <TStatement extends StatementDeclare> TStatement parseStatement(
			CharSequence dnaSql, Class<TStatement> clz) {
		return DNASql.parseDefine(CharSequenceReader.newReader(dnaSql), this, clz);
	}

	public final DBCommandProxy prepareStatement(
			StatementDeclarator<?> statement) {
		return this.prepareStatement(statement.getDefine());
	}

	public final RecordSetImpl openQuery(QueryStatementDefine query,
			Object... argValues) {
		return DBAdapterImpl.openQuery(this, query, argValues);
	}

	public final RecordSetImpl openQuery(QueryStatementDeclarator query,
			Object... argValues) {
		return this.openQuery(query.getDefine(), argValues);
	}

	public final RecordSet openQueryLimit(QueryStatementDefine query,
			long offset, long rowCount, Object... argValues) {
		return DBAdapterImpl.openQueryLimit(this, query, offset, rowCount, argValues);
	}

	public final RecordSet openQueryLimit(QueryStatementDeclarator query,
			long offset, long rowCount, Object... argValues) {
		return this.openQueryLimit(query.getDefine(), offset, rowCount, argValues);
	}

	public final void iterateQuery(QueryStatementDefine query,
			RecordIterateAction action, Object... argValues) {
		DBAdapterImpl.iterateQuery(this, query, action, argValues);
	}

	public final void iterateQuery(QueryStatementDeclarator query,
			RecordIterateAction action, Object... argValues) {
		DBAdapterImpl.iterateQuery(this, query.getDefine(), action, argValues);
	}

	public final void iterateQueryLimit(QueryStatementDefine query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues) {
		DBAdapterImpl.iterateQueryLimit(this, query, offset, rowCount, action, argValues);
	}

	public final void iterateQueryLimit(QueryStatementDeclarator query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues) {
		DBAdapterImpl.iterateQueryLimit(this, query.getDefine(), offset, rowCount, action, argValues);
	}

	public final int rowCountOf(QueryStatementDefine query, Object... argValues) {
		return (int) DBAdapterImpl.rowCountOf(this, (QueryStatementImpl) query, argValues);
	}

	public final int rowCountOf(QueryStatementDeclarator query,
			Object... argValues) {
		return this.rowCountOf(query.getDefine(), argValues);
	}

	public final long rowCountOfL(QueryStatementDefine query,
			Object... argValues) {
		return DBAdapterImpl.rowCountOf(this, (QueryStatementImpl) query, argValues);
	}

	public final long rowCountOfL(QueryStatementDeclarator query,
			Object... argValues) {
		return this.rowCountOfL(query.getDefine(), argValues);
	}

	public final Object executeScalar(QueryStatementDefine query,
			Object... argValues) {
		return DBAdapterImpl.executeScalar(this, (QueryStatementImpl) query, argValues);
	}

	public final Object executeScalar(QueryStatementDeclarator query,
			Object... argValues) {
		return this.executeScalar(query.getDefine(), argValues);
	}

	public final int executeUpdate(ModifyStatementDefine statement,
			Object... argValues) {
		return DBAdapterImpl.executeUpdate(this, statement, argValues);
	}

	public final int executeUpdate(ModifyStatementDeclarator<?> statement,
			Object... argValues) {
		return this.executeUpdate(statement.getDefine(), argValues);
	}

	public final void executeUpdate(StoredProcedureDeclarator procedure,
			Object... argValues) {
		this.executeProcedure(procedure.getDefine(), argValues);
	}

	public final void executeUpdate(StoredProcedureDefine procedure,
			Object... argValues) {
		this.executeProcedure(procedure, argValues);
	}

	public final RecordSet[] executeProcedure(
			StoredProcedureDeclarator procedure, Object... argValues) {
		return this.executeProcedure(procedure.getDefine(), argValues);
	}

	public final RecordSet[] executeProcedure(StoredProcedureDefine procedure,
			Object... argValues) {
		return DBAdapterImpl.executeProcedure(this, procedure, argValues);
	}

	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			ORMDeclarator<TEntity> orm) {
		return DBAdapterImpl.newORMAccessor(this, (MappingQueryStatementImpl) orm.getDefine());
	}

	public final ORMAccessorProxy<Object> newORMAccessor(
			MappingQueryStatementDefine mappingQuery) {
		return DBAdapterImpl.newORMAccessor(this, (MappingQueryStatementImpl) mappingQuery);
	}

	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			Class<TEntity> entityClass, MappingQueryStatementDefine query) {
		MappingQueryStatementImpl mq = (MappingQueryStatementImpl) query;
		if (mq.mapping.soClass != entityClass) {
			throw new IllegalArgumentException("实体类型与ORM定义不符");
		}
		return DBAdapterImpl.newORMAccessor(this, mq);
	}

	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			EntityTableDeclarator<TEntity> table) {
		if (table == null) {
			throw new NullArgumentException("table");
		}
		return DBAdapterImpl.newORMAccessor(this, (MappingQueryStatementImpl) table.getMappingQueryDefine());
	}

	public final int getMaxColumnsInSelect() {
		return this.getDBAdapter().dbMetadata.getMaxColumnsInSelect();
	}

	public final DbProduct dbProduct() {
		return DBAdapterImpl.dbProduct(this);
	}

	final <TElement, TElementMeta extends NamedFactoryElement> TElement newElement(
			Context userContext,
			NamedFactoryElementGather<TElement, TElementMeta> factory,
			TElementMeta meta, Object[] adArgs) {
		if (factory == null) {
			throw new NullArgumentException("factory");
		}
		if (meta == null) {
			throw new NullArgumentException("meta");
		}
		final SpaceNode occorAtSave = meta.space.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			return factory.doNewElement(userContext, meta, adArgs);
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	// /////////////////////////////////////////
	// /////////// 进展通知器
	// /////////////////////////////////////////

	public final float getNextStep() {
		return this.contextProgressNextStep;
	}

	/**
	 * 设置下一步的步长，同时增加进度
	 * 
	 * @param step
	 *            下一步的步长
	 */
	public final float setNextStep(float nextStep) {
		this.checkValid();
		float contextProgress;
		final float lastStep = this.contextProgressNextStep;
		if (lastStep > 0f) {
			contextProgress = this.contextProgress += lastStep;
			float progressQuotiety = this.progressQuotiety;
			if (progressQuotiety > 0) {
				final float p = this.progress + lastStep * progressQuotiety;
				this.progress = p < 0.9999f ? p : 0.9999f;
			}
		} else {
			contextProgress = this.contextProgress;
		}
		if (nextStep <= 0f) {
			this.contextProgressNextStep = 0f;
		} else {
			final float rest = 1f - contextProgress;
			if (nextStep > rest) {
				this.contextProgressNextStep = rest;
			} else {
				this.contextProgressNextStep = nextStep;
			}
		}
		return contextProgress;
	}

	public final float setNextPartialProgress(float nextProgress) {
		this.checkValid();
		float contextProgress;
		final float lastStep = this.contextProgressNextStep;
		if (lastStep > 0f) {
			contextProgress = this.contextProgress += lastStep;
			float progressQuotiety = this.progressQuotiety;
			if (progressQuotiety > 0) {
				final float p = this.progress + lastStep * progressQuotiety;
				this.progress = p < 0.9999f ? p : 0.9999f;
			}
		} else {
			contextProgress = this.contextProgress;
		}
		if (nextProgress >= 1f) {
			this.contextProgressNextStep = 1f - contextProgress;
		} else if (nextProgress <= contextProgress) {
			this.contextProgressNextStep = 0f;
		} else {
			this.contextProgressNextStep = nextProgress - contextProgress;
		}
		return contextProgress;
	}

	/**
	 * 获得当前上下文处理的进度
	 * 
	 * @return 返回当前上下文处理的进度
	 */
	public final float getPartialProgress() {
		this.checkValid();
		return this.contextProgress;
	}

	public final float setPartialProgress(float progress) {
		this.checkValid();
		final float lastContextProgress = this.contextProgress;
		float newProgress = lastContextProgress + this.contextProgressNextStep;
		if (newProgress >= 1f) {
			newProgress = 1f;
		} else if (newProgress < progress) {
			if (progress > 1f) {
				newProgress = 1f;
			} else {
				newProgress = progress;
			}
		}
		this.contextProgress = newProgress;
		this.contextProgressNextStep = 0f;
		float lastStep = newProgress - lastContextProgress;
		if (lastStep > 0) {
			float progressQuotiety = this.progressQuotiety;
			if (progressQuotiety > 0) {
				final float p = this.progress + lastStep * progressQuotiety;
				this.progress = p < 0.9999f ? p : 0.9999f;
			}
		}
		return newProgress;
	}

	public final float getRestPartialProgress() {
		this.checkValid();
		return 1f - this.contextProgress - this.contextProgressNextStep;
	}

	/**
	 * 获得当前请求处理的总进度
	 * 
	 * @return 返回当前请求处理的总进度
	 */
	public final float getTotalProgress() {
		this.checkValid();
		return this.progress;
	}

	/**
	 * 获得当前上下文整个处理所占当前请求整个处理的比例。
	 * 
	 * @return 返回当前上下文整个处理所占当前请求整个处理的比例。
	 */
	public final float getPartialProgressQuotiety() {
		this.checkValid();
		return this.progressQuotiety;
	}

	/**
	 * 报告Hint信息
	 * 
	 */
	public final void reportHint(HintInfoDefine infoDefine) {
		this.internalReport(infoDefine, InfoKind.HINT, null, null, null, null, 0);
	}

	/**
	 * 报告Hint信息
	 */
	public final void reportHint(HintInfoDefine infoDefine, Object param1) {
		this.internalReport(infoDefine, InfoKind.HINT, param1, null, null, null, 0);
	}

	/**
	 * 报告Hint信息
	 */

	public final void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2) {
		this.internalReport(infoDefine, InfoKind.HINT, param1, param2, null, null, 0);
	}

	/**
	 * 报告Hint信息
	 */

	public final void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2, Object param3) {
		this.internalReport(infoDefine, InfoKind.HINT, param1, param2, param3, null, 0);
	}

	/**
	 * 报告Hint信息
	 */
	public final void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others) {
		this.internalReport(infoDefine, InfoKind.HINT, param1, param2, param3, others, 0);
	}

	/**
	 * 报告Error信息
	 * 
	 */
	public final void reportError(ErrorInfoDefine infoDefine) {
		this.internalReport(infoDefine, InfoKind.ERROR, null, null, null, null, 0);
	}

	/**
	 * 报告Error信息
	 */
	public final void reportError(ErrorInfoDefine infoDefine, Object param1) {
		this.internalReport(infoDefine, InfoKind.ERROR, param1, null, null, null, 0);
	}

	/**
	 * 报告Error信息
	 */

	public final void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2) {
		this.internalReport(infoDefine, InfoKind.ERROR, param1, param2, null, null, 0);
	}

	/**
	 * 报告Error信息
	 */

	public final void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2, Object param3) {
		this.internalReport(infoDefine, InfoKind.ERROR, param1, param2, param3, null, 0);
	}

	/**
	 * 报告Error信息
	 */
	public final void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others) {
		this.internalReport(infoDefine, InfoKind.ERROR, param1, param2, param3, others, 0);
	}

	/**
	 * 报告Done信息
	 * 
	 */
	public final void reportWarning(WarningInfoDefine infoDefine) {
		this.internalReport(infoDefine, InfoKind.WARNING, null, null, null, null, 0);
	}

	/**
	 * 报告Done信息
	 */
	public final void reportWarning(WarningInfoDefine infoDefine, Object param1) {
		this.internalReport(infoDefine, InfoKind.WARNING, param1, null, null, null, 0);
	}

	/**
	 * 报告Done信息
	 */

	public final void reportWarning(WarningInfoDefine infoDefine,
			Object param1, Object param2) {
		this.internalReport(infoDefine, InfoKind.WARNING, param1, param2, null, null, 0);
	}

	/**
	 * 报告Done信息
	 */

	public final void reportWarning(WarningInfoDefine infoDefine,
			Object param1, Object param2, Object param3) {
		this.internalReport(infoDefine, InfoKind.WARNING, param1, param2, param3, null, 0);
	}

	/**
	 * 报告Done信息
	 */
	public final void reportWarning(WarningInfoDefine infoDefine,
			Object param1, Object param2, Object param3, Object... others) {
		this.internalReport(infoDefine, InfoKind.WARNING, param1, param2, param3, others, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine) {
		this.internalReport(infoDefine, InfoKind.PROCESS, null, null, null, null, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1) {
		this.internalReport(infoDefine, InfoKind.PROCESS, param1, null, null, null, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2) {
		this.internalReport(infoDefine, InfoKind.PROCESS, param1, param2, null, null, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3) {
		this.internalReport(infoDefine, InfoKind.PROCESS, param1, param2, param3, null, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others) {
		this.internalReport(infoDefine, InfoKind.PROCESS, param1, param2, param3, others, 0);
	}

	/**
	 * 尝试记录日志
	 */
	private final boolean tryLogInfo(InfoImpl info) {
		if (info.define.isNeedLog()) {
			this.occorAt.site.logManager.log(this.session, info);
			return true;
		}
		return false;
	}

	public final void endProcess() {
		this.checkValid();
		final InfoImpl lastInfo = this.lastInfo;
		final short invokeDepth = this.invokeDepth;
		final ProcessInfoImpl process = lastInfo != null ? lastInfo.finishRealProcess(invokeDepth) : null;
		if (process == null) {
			throw new EndProcessException();
		}
		this.lastInfo = process;
		this.leaveFrame(process.contextProgressSave, process.progressQuotietySave);
		this.tryLogInfo(process);
	}

	/**
	 * 当前信息环的尾部
	 */
	private InfoImpl lastInfo;

	final int fetchInfos(List<Info> to) {
		return 0;
	}

	/**
	 * 内部信息报告方法
	 */
	final void internalReport(InfoDefine infoDefineIntf, InfoKind kind,
			Object param1, Object param2, Object param3, Object[] others,
			int otherOffset) {
		this.checkValid();
		InfoDefineImpl infoDefine = (InfoDefineImpl) infoDefineIntf;
		if (infoDefine == null) {
			throw new NullArgumentException("infoDefine");
		}
		if (kind != infoDefine.kind) {
			throw new IllegalArgumentException("infoDefine 类型不符");
		}
		final InfoImpl newInfo;
		final InfoImpl lastInfo = this.lastInfo;
		final ProcessInfoImpl process = lastInfo == null ? null : lastInfo.getRealProcess();
		if (kind == InfoKind.PROCESS) {
			final float contextProgressSave = this.contextProgress;
			final float progressQuotietySave = this.enterFrame();
			this.lastInfo = newInfo = new ProcessInfoImpl(infoDefine, process, param1, param2, param3, others, otherOffset, contextProgressSave, progressQuotietySave, this.depth);
			if (process == null) {
				newInfo.insertAfter(lastInfo);
			}
		} else {
			this.lastInfo = newInfo = new InfoImpl(infoDefine, process, param1, param2, param3, others, otherOffset);
			if (process == null) {
				newInfo.insertAfter(lastInfo);
			}
			this.tryLogInfo(newInfo);
			if (kind == InfoKind.ERROR) {
				throw new InfoInterrupt(newInfo);
			}
		}
	}

	/**
	 * 获取列表结果
	 */
	final <TResult, TKey1, TKey2, TKey3> int serviceProvideTree(
			TreeNode<TResult> root,
			ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> treeNodeProvider,
			TKey1 key1, TKey2 key2, TKey3 key3) {
		if (treeNodeProvider == null) {
			throw new NullArgumentException("treeNodeProvider");
		}
		final SpaceNode occorAtSave = treeNodeProvider.getService().updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			int absoluteLevel = 0;
			if (key1 == null) {
				absoluteLevel = treeNodeProvider.provide(this, root);
			} else if (key2 == null) {
				absoluteLevel = treeNodeProvider.provide(this, key1, root);
			} else if (key3 == null) {
				absoluteLevel = treeNodeProvider.provide(this, key1, key2, root);
			} else {
				absoluteLevel = treeNodeProvider.provide(this, key1, key2, key3, root);
			}
			if (this.exception != null) {
				throw this.exception;
			}
			return absoluteLevel;
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	public final boolean isValid() {
		return !(this.thread != Thread.currentThread() || this.disposed || this.canceling);
	}

	public final boolean isDBAccessible() {
		return this.occorAt.isDBValid();
	}

	public final void checkValid() {
		if (this.thread != Thread.currentThread()) {
			throw new UnsupportedOperationException("不允许通过其他线程访问上下文");
		}
		if (this.disposed) {
			throw new DisposedException("上下文已经被销毁");
		}
		if (this.canceling) {
			throw Utils.tryThrowException(new InterruptedException());
		}
	}

	public final boolean isCanceling() {
		if (this.thread != Thread.currentThread()) {
			throw new UnsupportedOperationException("不允许通过其他线程访问上下文");
		}
		if (this.disposed) {
			throw new DisposedException("上下文已经被销毁");
		}
		return this.canceling;
	}

	public final void throwIfCanceling() {
		if (this.isCanceling()) {
			throw Utils.tryThrowException(new InterruptedException());
		}
	}

	public final Object getCategory() {
		return None.NONE;
	}

	public final void setCategory(Object category) {
		throw new UnsupportedOperationException("上下文不支持设置资源类别");
	}

	public final CategorialResourceModifier<TFacadeM, TImplM, TKeysHolderM> usingResourceCategory(
			Object category) {
		if (category == null || category == None.NONE || (category instanceof Long && category.equals(new Long(0)))) {
			return this;
		}
		return new CategorialResContextAdapter<TFacadeM, TImplM, TKeysHolderM>(this, category);
	}

	// /////////////////////////////////////////////////////////////////////
	// ///// Script
	// /////////////////////////////////////////////////////////////////////
	private final void disposeModelScriptContexts() {
		ModelScriptContextHolder lastMSContext = this.lastMSContext;
		if (lastMSContext != null) {
			ModelScriptContextHolder msContext = lastMSContext;
			do {
				msContext.msContext.release();
				ModelScriptContextHolder last = msContext;
				msContext = msContext.nextInContext;
				last.nextInContext = null;// help GC
			} while (msContext != lastMSContext);
			this.lastMSContext = null;
		}
	}

	private static class ModelScriptContextHolder {
		final ModelScriptEngine<?> engine;
		final ModelScriptContext<?> msContext;

		ModelScriptContextHolder(ModelScriptContext<?> msContext,
				ModelScriptContextHolder nextInContext) {
			this.engine = msContext.getEngine();
			this.msContext = msContext;
			this.nextInContext = nextInContext == null ? this : nextInContext;
		}

		ModelScriptContextHolder nextInContext;
	}

	private ModelScriptContextHolder lastMSContext;

	final ModelScriptContext<?> getScriptContext(ModelScriptEngine<?> engine) {
		if (engine == null) {
			throw new NullArgumentException("engine");
		}
		ModelScriptContextHolder lastMSContext = this.lastMSContext;
		if (lastMSContext != null) {
			if (lastMSContext.engine != engine) {

				for (ModelScriptContextHolder msContext = lastMSContext.nextInContext; msContext != lastMSContext; msContext = msContext.nextInContext) {
					if (msContext.engine == engine) {
						this.lastMSContext = msContext;
						return msContext.msContext;
					}
				}
				lastMSContext = this.lastMSContext = lastMSContext.nextInContext = new ModelScriptContextHolder(engine.allocContext(this), lastMSContext.nextInContext);
			}
		} else {
			lastMSContext = this.lastMSContext = new ModelScriptContextHolder(engine.allocContext(this), null);
		}
		return lastMSContext.msContext;
	}

	/**
	 * 找不到返回null
	 */
	final ModelScriptContext<?> tryGetScriptContext(String language) {
		ModelScriptEngine<?> engine = this.session.application.mseManager.findEngine(language);
		return engine == null ? null : this.getScriptContext(engine);
	}

	// ////////////////
	// // test
	// ////////////////
	@SuppressWarnings("unchecked")
	final void testCase(TestContext testContext, CaseTester tester)
			throws Throwable {
		this.resolveTrans();
		SpaceNode old = tester.getService().updateContextSpace(this);
		try {
			tester.testCase(this, testContext);
		} finally {
			this.resolveTrans();
			old.updateContextSpace(this);
		}
	}

	// /////////////////////
	// // 远程调用
	// //////////////////////
	public final ServiceInvoker usingRemoteInvoker(
			RemoteLoginInfo remoteLoginInfo) {
		throw new UnsupportedOperationException("不支持的远程调用方法");
	}

	/**
	 * 获取远程调用信息
	 */
	public final RemoteLoginInfoImpl allocRemoteLoginInfo(String host, int port) {
		throw new UnsupportedOperationException("不支持的远程调用方法");
	}

	/**
	 * 获取远程调用信息
	 */
	public final RemoteLoginInfoImpl allocRemoteLoginInfo(String host,
			int port, String user, String password) {
		throw new UnsupportedOperationException("不支持的远程调用方法");
	}

	/**
	 * 获取远程调用信息
	 */
	public final RemoteLoginInfoImpl allocRemoteLoginInfo(String host,
			int port, String user, String password, RemoteLoginLife life) {
		throw new UnsupportedOperationException("不支持的远程调用方法");
	}

	// /////////////////本地化//////////////////////////////
	public final Locale getLocale() {
		return this.session.getLocale();
	}

	final String internalLocalize(InfoDefine info, Object p1, Object p2,
			Object p3, Object[] others) {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		this.checkValid();
		return ((InfoDefineImpl) info).formatMessage(this.session.locateKey, p1, p2, p3, others);
	}

	final void internalLocalize(InfoDefine info, Appendable to, Object p1,
			Object p2, Object p3, Object[] others) {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		if (to == null) {
			throw new NullArgumentException("to");
		}
		this.checkValid();
		((InfoDefineImpl) info).formatMessage(this.session.locateKey, to, p1, p2, p3, others);

	}

	public final String localize(InfoDefine info) {
		return this.internalLocalize(info, null, null, null, null);
	}

	public final String localize(InfoDefine info, Object param1) {
		return this.internalLocalize(info, param1, null, null, null);
	}

	public final String localize(InfoDefine info, Object param1, Object param2) {
		return this.internalLocalize(info, param1, param2, null, null);
	}

	public final String localize(InfoDefine info, Object param1, Object param2,
			Object param3) {
		return this.internalLocalize(info, param1, param2, param3, null);
	}

	public final String localize(InfoDefine info, Object param1, Object param2,
			Object param3, Object... others) {
		return this.internalLocalize(info, param1, param2, param3, others);
	}

	public final void localize(Appendable to, InfoDefine info) {
		this.internalLocalize(info, to, null, null, null, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1) {
		this.internalLocalize(info, to, param1, null, null, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1,
			Object param2) {
		this.internalLocalize(info, to, param1, param2, null, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1,
			Object param2, Object param3) {
		this.internalLocalize(info, to, param1, param2, param3, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1,
			Object param2, Object param3, Object... others) {
		this.internalLocalize(info, to, param1, param2, param3, others);
	}

	public final void abort() throws AbortException {
		this.checkValid();
		AbortException ae;
		if (this.exception != null) {
			this.exception = ae = new AbortException(this.exception);
		} else {
			this.exception = ae = new AbortException();
		}
		if (this.depth == 0) {// 在最外层回滚事务
			this.resolveTrans();
		}
		throw ae;
	}

	// --------------------------以下资源相关-----------------------------------

	private static final String PRINT_INIT_CACHE_GROUPS = System.getProperty("com.jiuqi.dna.print-init-cache-groups");

	private static final boolean printInitCacheGroups(
			CacheDefine<?, ?, ?> define) {
		if (PRINT_INIT_CACHE_GROUPS == null) {
			return false;
		}
		return PRINT_INIT_CACHE_GROUPS.indexOf(define.facadeClass.getName()) != -1;
	}

	@SuppressWarnings("unchecked")
	final void initializeCacheGroup(final CacheGroup group,
			final Transaction transaction) {
		final ResourceServiceBase resourceService = group.define.resourceService;
		final SpaceNode occorAtSave = resourceService.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			if (group.inCluster) {
				resourceService.initializeClusterGroup(group, transaction);
			} else {
				resourceService.initializeNoneClusterGroup(group, transaction);
			}
			if (printInitCacheGroups(group.define)) {
				System.out.println("初始化缓存分组，缓存类型[" + group.define.facadeClass.getName() + "]，分组标识[" + group.ownSpace.identifier + "]，缓存项数量[" + group.getHolderCount() + "]");
			}
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;
			throw Utils.tryThrowException(e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	@SuppressWarnings("unchecked")
	final void loadCacheHolder(final KeyDefine keyDefine,
			final CacheGroup group, final Object key1Value,
			final Object key2Value, final Object key3Value) {
		final CacheDefine define = group.define;
		final SpaceNode occorAtSave = define.resourceService.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			keyDefine.provider.provideProxy(this, define.resourceService.new HolderSetter(group, this.transaction), key1Value, key2Value, key3Value);
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	final void restoreCacheHolderUserData(
			final ResourceServiceBase resourceService, final Object userData,
			final Object newValue, final Object newKeysHolder) {
		final SpaceNode occorAtSave = resourceService.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			resourceService.internalRestoreSerialUserData(userData, newValue, newKeysHolder, this);
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	final void beforeAccessAuthorityResource(
			final ResourceServiceBase<?, ?, ?> resourceService) {
		final SpaceNode occorAtSave = resourceService.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			resourceService.beforeAccessAuthorityResource(this);
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	final void endAccessAuthorityResource(
			final ResourceServiceBase<?, ?, ?> resourceService) {
		final SpaceNode occorAtSave = resourceService.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			resourceService.endAccessAuthorityResource(this);
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave, progressQuotietySave, invokeDepthSave);
		}
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Class<TFacade> facadeClass) {
		CacheArgumentChecker.check(facadeClass);
		return this.internalGetResourceToken(facadeClass, null, null, null, null, null, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Class<TFacade> facadeClass, Object key) {
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalGetResourceToken(facadeClass, key.getClass(), null, null, key, null, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2) {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalGetResourceToken(facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3) {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalGetResourceToken(facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		return this.internalGetResourceToken(operation, facadeClass, null, null, null, null, null, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalGetResourceToken(operation, facadeClass, key.getClass(), null, null, key, null, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalGetResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalGetResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws MissingObjectException {
		throw new UnsupportedOperationException();
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass) {
		CacheArgumentChecker.check(facadeClass);
		return this.internalFindResourceToken(facadeClass, null, null, null, null, null, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass, Object key) {
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalFindResourceToken(facadeClass, key.getClass(), null, null, key, null, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2) {
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalFindResourceToken(facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3) {
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalFindResourceToken(facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass);
		return this.internalFindResourceToken(operation, facadeClass, null, null, null, null, null, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key);
		return this.internalFindResourceToken(operation, facadeClass, key.getClass(), null, null, key, null, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2);
		return this.internalFindResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		CacheArgumentChecker.check(facadeClass, key1, key2, key3);
		return this.internalFindResourceToken(operation, facadeClass, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public final <TFacade> CacheHolder<TFacade, ?, ?> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	private final <TFacade> CacheHolder<TFacade, ?, ?> internalGetResourceToken(
			final Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class,
			final Object key1, final Object key2, final Object key3) {
		final CacheHolderIndex<TFacade, ?, ?> itemIndex = this.getCacheGroup(facadeClass).findIndex(key1Class, key2Class, key3Class);
		if (itemIndex != null) {
			CacheHolder<TFacade, ?, ?> result = itemIndex.findHolder(key1, key2, key3, this.transaction);
			if (result != null) {
				return result;
			}
		}
		throw new MissingObjectException("找不到[" + facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3 + "]对象");
	}

	final <TFacade> CacheHolder<TFacade, ?, ?> internalFindResourceToken(
			final Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class,
			final Object key1, final Object key2, final Object key3) {
		final CacheHolderIndex<TFacade, ?, ?> itemIndex = this.getCacheGroup(facadeClass).findIndex(key1Class, key2Class, key3Class);
		if (itemIndex == null) {
			return null;
		} else {
			return itemIndex.findHolder(key1, key2, key3, this.transaction);
		}
	}

	private final <TFacade> CacheHolder<TFacade, ?, ?> internalGetResourceToken(
			final Operation<? super TFacade> operation,
			final Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class,
			final Object key1, final Object key2, final Object key3) {
		final CacheGroup<TFacade, ?, ?> group = this.getCacheGroup(facadeClass);
		final CacheHolderIndex<TFacade, ?, ?> itemIndex = group.findIndex(key1Class, key2Class, key3Class);
		if (itemIndex != null) {
			final CacheHolder<TFacade, ?, ?> result;
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			resourceService.callBeforeAccessAuthorityResource(this);
			try {
				result = itemIndex.findHolder(this.getOperationAuthorityChecker(), operation, key1, key2, key3, this.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(this);
			}
			if (result != null) {
				return result;
			}
		}
		throw new MissingObjectException("找不到[" + facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3 + "]对象");
	}

	private final <TFacade> CacheHolder<TFacade, ?, ?> internalFindResourceToken(
			final Operation<? super TFacade> operation,
			final Class<TFacade> facadeClass, final Class<?> key1Class,
			final Class<?> key2Class, final Class<?> key3Class,
			final Object key1, final Object key2, final Object key3) {
		final CacheGroup<TFacade, ?, ?> group = this.getCacheGroup(facadeClass);
		final CacheHolderIndex<TFacade, ?, ?> itemIndex = group.findIndex(key1Class, key2Class, key3Class);
		if (itemIndex == null) {
			return null;
		} else {
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			resourceService.callBeforeAccessAuthorityResource(this);
			try {
				return itemIndex.findHolder(this.getOperationAuthorityChecker(), operation, key1, key2, key3, this.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(this);
			}
		}
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass, ResourceToken<THolderFacade> holderToken) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (holderToken == null) {
			throw new NullArgumentException("holderToken");
		}
		return this.internalGetResourceReferences(null, facadeClass, holderToken, null, null);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (holderToken == null) {
			throw new NullArgumentException("holderToken");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetResourceReferences(null, facadeClass, holderToken, filter, sortComparator);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (holderToken == null) {
			throw new NullArgumentException("holderToken");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetResourceReferences(null, facadeClass, holderToken, filter, null);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (holderToken == null) {
			throw new NullArgumentException("holderToken");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetResourceReferences(null, facadeClass, holderToken, null, sortComparator);

	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (holderToken == null) {
			throw new NullArgumentException("holderToken");
		}
		return this.internalGetResourceReferences(operation, facadeClass, holderToken, null, null);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (holderToken == null) {
			throw new NullArgumentException("holderToken");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return this.internalGetResourceReferences(operation, facadeClass, holderToken, filter, null);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (holderToken == null) {
			throw new NullArgumentException("holderToken");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetResourceReferences(operation, facadeClass, holderToken, null, sortComparator);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (holderToken == null) {
			throw new NullArgumentException("holderToken");
		}
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return this.internalGetResourceReferences(operation, facadeClass, holderToken, filter, sortComparator);
	}

	@SuppressWarnings("unchecked")
	private final <TFacade, THolderFacade> List<TFacade> internalGetResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		this.checkValid();
		if (operation == null) {
			return ((CacheHolder<THolderFacade, ?, ?>) holderToken).tryGetReferences(facadeClass, filter, sortComparator, this.transaction);
		} else {
			final CacheHolder<THolderFacade, ?, ?> holder = (CacheHolder<THolderFacade, ?, ?>) holderToken;
			final ResourceServiceBase<?, ?, ?> resourceService = holder.ownGroup.define.resourceService;
			resourceService.callBeforeAccessAuthorityResource(this);
			try {
				return holder.tryGetReferences(this.getOperationAuthorityChecker(), operation, facadeClass, filter, sortComparator, this.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(this);
			}
		}
	}

	// --------------------------以下资源相关-----------------------------------

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceHandle<TFacade> lockResourceS(
			ResourceToken<TFacade> resourceToken) {
		this.checkValid();
		final CacheHolder<TFacade, ?, ?> resource = (CacheHolder<TFacade, ?, ?>) resourceToken;
		this.transaction.handleAcquirable(resource, AcquireFor.READ);
		return new ResourceHandleImplement<TFacade>(resource, this, this.transaction);
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceHandle<TFacade> lockResourceU(
			ResourceToken<TFacade> resourceToken) {
		this.checkValid();
		final CacheHolder<TFacade, ?, ?> resource = (CacheHolder<TFacade, ?, ?>) resourceToken;
		this.transaction.handleAcquirable(resource, AcquireFor.MODIFY);
		return new ResourceHandleImplement<TFacade>(resource, this, this.transaction);
	}

	@SuppressWarnings("unchecked")
	public final TImplM cloneResource(ResourceToken<TFacadeM> token) {
		if (token == null) {
			throw new NullArgumentException("item");
		}
		final CacheHolder<TFacadeM, ?, ?> cacheItem = (CacheHolder<TFacadeM, ?, ?>) token;
		return OBJAContext.clone((TImplM) (cacheItem.tryGetValue(this.transaction)), null, cacheItem.ownGroup.define.implementStruct);
	}

	@SuppressWarnings("unchecked")
	public final TImplM cloneResource(ResourceToken<TFacadeM> token,
			TImplM tryReuse) {
		if (token == null) {
			throw new NullArgumentException("item");
		}
		final CacheHolder<TFacadeM, ?, ?> cacheItem = (CacheHolder<TFacadeM, ?, ?>) token;
		return OBJAContext.clone((TImplM) (cacheItem.tryGetValue(this.transaction)), tryReuse, cacheItem.ownGroup.define.implementStruct);
	}

	@SuppressWarnings("unchecked")
	public final TImplM cloneResource(Operation<? super TFacadeM> operation,
			ResourceToken<TFacadeM> token, TImplM tryReuse) {
		if (token == null) {
			throw new NullArgumentException("token");
		}
		final CacheHolder<TFacadeM, ?, ?> cacheItem = (CacheHolder<TFacadeM, ?, ?>) token;
		final ResourceServiceBase<?, ?, ?> resourceService = cacheItem.ownGroup.define.resourceService;
		resourceService.callBeforeAccessAuthorityResource(this);
		final boolean hasAuthority;
		try {
			hasAuthority = this.getOperationAuthorityChecker().hasAuthority(operation, cacheItem);
		} finally {
			resourceService.callEndAccessAuthorityResource(this);
		}
		if (hasAuthority) {
			return OBJAContext.clone((TImplM) (cacheItem.tryGetValue(this.transaction)), tryReuse, cacheItem.ownGroup.define.implementStruct);
		} else {
			throw new NoAccessAuthorityException("无访问权限。");
		}
	}

	@SuppressWarnings("unchecked")
	public final TImplM cloneResource(Operation<? super TFacadeM> operation,
			ResourceToken<TFacadeM> token) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (token == null) {
			throw new NullArgumentException("token");
		}
		final CacheHolder<TFacadeM, ?, ?> cacheItem = (CacheHolder<TFacadeM, ?, ?>) token;
		final ResourceServiceBase<?, ?, ?> resourceService = cacheItem.ownGroup.define.resourceService;
		resourceService.callBeforeAccessAuthorityResource(this);
		final boolean hasAuthority;
		try {
			hasAuthority = this.getOperationAuthorityChecker().hasAuthority(operation, cacheItem);
		} finally {
			resourceService.callEndAccessAuthorityResource(this);
		}
		if (hasAuthority) {
			return OBJAContext.clone((TImplM) (cacheItem.tryGetValue(this.transaction)), null, cacheItem.ownGroup.define.implementStruct);
		} else {
			throw new NoAccessAuthorityException("无访问权限。");
		}
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken<TFacadeM> putResource(TImplM resource) {
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		final CacheGroup<TFacadeM, TImplM, TKeysHolderM> group = (CacheGroup<TFacadeM, TImplM, TKeysHolderM>) this.getCacheGroup(this.getFacadeClass());
		// DIST
		if (group.define.resourceService.isFilterExcluded(this, resource)) {
			return null;
		}
		this.checkCacheModifiable(group.define, null);
		return group.localTryCreateHolder(resource, (TKeysHolderM) resource, PutPolicy.REPLACE, this.transaction);
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken<TFacadeM> putResource(TImplM resource,
			TKeysHolderM keysHolder) {
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (keysHolder == null) {
			throw new NullArgumentException("keysHolder");
		}
		final CacheGroup<TFacadeM, TImplM, TKeysHolderM> group = (CacheGroup<TFacadeM, TImplM, TKeysHolderM>) this.getCacheGroup(this.getFacadeClass());
		// DIST
		if (group.define.resourceService.isFilterExcluded(this, resource)) {
			return null;
		}
		this.checkCacheModifiable(group.define, null);
		return group.localTryCreateHolder(resource, keysHolder, PutPolicy.REPLACE, this.transaction);
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken<TFacadeM> putResource(TImplM resource,
			TKeysHolderM keysHolder, WhenExists policy) {
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (keysHolder == null) {
			throw new NullArgumentException("keysHolder");
		}
		if (policy == null) {
			throw new NullArgumentException("policy");
		}
		final CacheGroup<TFacadeM, TImplM, TKeysHolderM> group = (CacheGroup<TFacadeM, TImplM, TKeysHolderM>) this.getCacheGroup(this.getFacadeClass());
		// DIST
		if (group.define.resourceService.isFilterExcluded(this, resource)) {
			return null;
		}
		this.checkCacheModifiable(group.define, null);
		return group.localTryCreateHolder(resource, keysHolder, CacheDefine.WhenExistPolicyPutPolicyTranslator.toPutPolicy(policy), this.transaction);
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken<TFacadeM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource) {
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		final CacheGroup<TFacadeM, TImplM, TKeysHolderM> group = (CacheGroup<TFacadeM, TImplM, TKeysHolderM>) this.getCacheGroup(this.getFacadeClass());
		// DIST
		if (group.define.resourceService.isFilterExcluded(this, resource)) {
			return null;
		}
		this.checkCacheModifiable(group.define, null);
		final CacheHolder<TFacadeM, TImplM, TKeysHolderM> item = group.localTryCreateHolder(resource, (TKeysHolderM) resource, PutPolicy.REPLACE, this.transaction);
		final CacheTree tree = group.getBindTree();
		tree.localTryCreateNode(treeParent == null ? null : (CacheHolder<TFacadeM, ?, ?>) treeParent, item, this.transaction);
		return item;
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken<TFacadeM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource,
			TKeysHolderM keysHolder) {
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (keysHolder == null) {
			throw new NullArgumentException("keysHolder");
		}
		final CacheGroup<TFacadeM, TImplM, TKeysHolderM> group = (CacheGroup<TFacadeM, TImplM, TKeysHolderM>) this.getCacheGroup(this.getFacadeClass());
		// DIST
		if (group.define.resourceService.isFilterExcluded(this, resource)) {
			return null;
		}
		this.checkCacheModifiable(group.define, null);
		final CacheHolder<TFacadeM, TImplM, TKeysHolderM> item = group.localTryCreateHolder(resource, keysHolder, PutPolicy.REPLACE, this.transaction);
		final CacheTree tree = group.getBindTree();
		tree.localTryCreateNode(treeParent == null ? null : (CacheHolder<TFacadeM, ?, ?>) treeParent, item, this.transaction);
		return item;
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken<TFacadeM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource,
			TKeysHolderM keysHolder, WhenExists policy) {
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (keysHolder == null) {
			throw new NullArgumentException("keysHolder");
		}
		if (policy == null) {
			throw new NullArgumentException("policy");
		}
		final CacheGroup<TFacadeM, TImplM, TKeysHolderM> group = (CacheGroup<TFacadeM, TImplM, TKeysHolderM>) this.getCacheGroup(this.getFacadeClass());
		// DIST
		if (group.define.resourceService.isFilterExcluded(this, resource)) {
			return null;
		}
		this.checkCacheModifiable(group.define, null);
		final CacheHolder<TFacadeM, TImplM, TKeysHolderM> item = group.localTryCreateHolder(resource, keysHolder, CacheDefine.WhenExistPolicyPutPolicyTranslator.toPutPolicy(policy), this.transaction);
		final CacheTree tree = group.getBindTree();
		tree.localTryCreateNode(treeParent == null ? null : (CacheHolder<TFacadeM, ?, ?>) treeParent, item, this.transaction);
		return item;
	}

	@SuppressWarnings("unchecked")
	public final void putResource(ResourceToken<TFacadeM> treeParent,
			ResourceToken<TFacadeM> child) {
		if (child == null) {
			throw new NullArgumentException("child");
		}
		this.checkValid();
		final CacheTree tree = this.getCacheGroup(this.getFacadeClass()).getBindTree();
		tree.localTryCreateNode((CacheHolder<TFacadeM, ?, ?>) treeParent, (CacheHolder<TFacadeM, ?, ?>) child, this.transaction);
	}

	public void invalidResource() throws DeadLockException {
		this.internalInvalidResource(null, null, null, null, null, null);
	}

	public <TKey> void invalidResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.internalInvalidResource(key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> void invalidResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		this.internalInvalidResource(key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.internalInvalidResource(key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	public void invalidResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		this.internalInvalidResource(operation, null, null, null, null, null, null);
	}

	public <TKey> void invalidResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.internalInvalidResource(operation, key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		this.internalInvalidResource(operation, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.internalInvalidResource(operation, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	private final void internalInvalidResource(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3) {
		final Class<TFacadeM> facadeClass = this.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			group.localTryInvalidHolder(keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, this.transaction);
		}
	}

	private final void internalInvalidResource(
			final Operation<? super TFacadeM> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3) {
		final Class<TFacadeM> facadeClass = this.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			resourceService.callBeforeAccessAuthorityResource(this);
			try {
				group.localTryInvalidHolder(this.getOperationAuthorityChecker(), operation, keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, this.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(this);
			}
		}
	}

	public void reloadResource() throws DeadLockException {
		this.internalReloadResource(null, null, null, null, null, null);
	}

	public <TKey> void reloadResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.internalReloadResource(key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> void reloadResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		this.internalReloadResource(key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> void reloadResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.internalReloadResource(key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> void reloadResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	public void reloadResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		this.internalReloadResource(operation, null, null, null, null, null, null);
	}

	public <TKey> void reloadResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.internalReloadResource(operation, key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> void reloadResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		this.internalReloadResource(operation, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> void reloadResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.internalReloadResource(operation, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> void reloadResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	private final void internalReloadResource(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3) {
		final Class<TFacadeM> facadeClass = this.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			group.localTryReloadHolder(keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, this.transaction);
		}
	}

	private final void internalReloadResource(
			final Operation<? super TFacadeM> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3) {
		final Class<TFacadeM> facadeClass = this.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			resourceService.callBeforeAccessAuthorityResource(this);
			try {
				group.localTryReloadHolder(this.getOperationAuthorityChecker(), operation, keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, this.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(this);
			}
		}
	}

	public TImplM modifyResource() throws DeadLockException {
		return this.internalModifyResource(null, null, null, null, null, null);
	}

	public <TKey> TImplM modifyResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalModifyResource(key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> TImplM modifyResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalModifyResource(key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalModifyResource(key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	public TImplM modifyResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalModifyResource(operation, null, null, null, null, null, null);
	}

	public <TKey> TImplM modifyResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalModifyResource(operation, key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalModifyResource(operation, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalModifyResource(operation, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	public final void postModifiedResource(TImplM modifiedResource) {
		if (modifiedResource == null) {
			throw new NullArgumentException("modifiedResource");
		}
		this.transaction.postedModifiedValue(modifiedResource);
	}

	@SuppressWarnings("unchecked")
	private final TImplM internalModifyResource(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3) {
		final Class<TFacadeM> facadeClass = this.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			// DIST
			this.checkCacheModifiable(group.define, null);
			final Object value = group.localTryModifyHolder(keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, this.transaction);
			if (value != null) {
				return (TImplM) value;
			}
		}
		throw new MissingObjectException(ServiceInvokeeBase.noResourceException(facadeClass, keyValue1, keyValue2, keyValue3, null).getMessage());
	}

	@SuppressWarnings("unchecked")
	private final TImplM internalModifyResource(
			final Operation<? super TFacadeM> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3) {
		final Class<TFacadeM> facadeClass = this.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final Object value;
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			// DIST
			this.checkCacheModifiable(group.define, null);
			resourceService.callBeforeAccessAuthorityResource(this);
			try {
				value = group.localTryModifyHolder(this.getOperationAuthorityChecker(), operation, keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, this.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(this);
			}
			if (value != null) {
				return (TImplM) value;
			}
		}
		throw new MissingObjectException(ServiceInvokeeBase.noResourceException(facadeClass, keyValue1, keyValue2, keyValue3, null).getMessage());
	}

	public TImplM removeResource() throws DeadLockException {
		return this.internalRemoveResource(null, null, null, null, null, null);
	}

	public <TKey> TImplM removeResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalRemoveResource(key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> TImplM removeResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalRemoveResource(key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalRemoveResource(key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	public TImplM removeResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalRemoveResource(operation, null, null, null, null, null, null);
	}

	public <TKey> TImplM removeResource(Operation<? super TFacadeM> operation,
			TKey key) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		return this.internalRemoveResource(operation, key.getClass(), null, null, key, null, null);
	}

	public <TKey1, TKey2> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		return this.internalRemoveResource(operation, key1.getClass(), key2.getClass(), null, key1, key2, null);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		return this.internalRemoveResource(operation, key1.getClass(), key2.getClass(), key3.getClass(), key1, key2, key3);
	}

	public <TKey1, TKey2, TKey3> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	private final TImplM internalRemoveResource(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3) {
		final Class<TFacadeM> facadeClass = this.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			// DIST
			this.checkCacheModifiable(group.define, null);
			final Object value = group.localTryRemoveHolder(keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, this.transaction);
			if (value != null) {
				return (TImplM) value;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private final TImplM internalRemoveResource(
			final Operation<? super TFacadeM> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3) {
		final Class<TFacadeM> facadeClass = this.getFacadeClass();
		final CacheGroup<TFacadeM, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group != null) {
			final Object value;
			final ResourceServiceBase<?, ?, ?> resourceService = group.define.resourceService;
			// DIST
			this.checkCacheModifiable(group.define, null);
			resourceService.callBeforeAccessAuthorityResource(this);
			try {
				value = group.localTryRemoveHolder(this.getOperationAuthorityChecker(), operation, keyValueClass1, keyValueClass2, keyValueClass3, keyValue1, keyValue2, keyValue3, this.transaction);
			} finally {
				resourceService.callEndAccessAuthorityResource(this);
			}
			if (value != null) {
				return (TImplM) value;
			}
		}
		return null;
	}

	public final <THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (reference == null) {
			throw new NullArgumentException("reference");
		}
		this.checkValid();
		((CacheHolder<?, ?, ?>) holder).localTryCreateReference((CacheHolder<?, ?, ?>) reference, this.transaction);
	}

	public final <TReferenceFacade> void putResourceReferenceBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (reference == null) {
			throw new NullArgumentException("reference");
		}
		this.checkValid();
		((CacheHolder<?, ?, ?>) holder).localTryCreateReference((CacheHolder<?, ?, ?>) reference, this.transaction);
	}

	public final <THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (reference == null) {
			throw new NullArgumentException("reference");
		}
		((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
	}

	public final <TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (reference == null) {
			throw new NullArgumentException("reference");
		}
		((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final <THolderFacade> void removeResourceReference(
			Operation<? super TFacadeM> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (reference == null) {
			throw new NullArgumentException("reference");
		}
		final CacheHolder item = (CacheHolder) holder;
		final ResourceServiceBase<?, ?, ?> resourceService = item.ownGroup.define.resourceService;
		final boolean hasAuthority;
		resourceService.callBeforeAccessAuthorityResource(this);
		try {
			hasAuthority = this.getOperationAuthorityChecker().hasAuthority(operation, item);
		} finally {
			resourceService.callEndAccessAuthorityResource(this);
		}
		if (hasAuthority) {
			item.localTryRemoveReference((CacheHolder) reference, this.transaction);
		} else {
			throw new NoAccessAuthorityException("无访问权限。");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final <TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (reference == null) {
			throw new NullArgumentException("reference");
		}
		final CacheHolder item = (CacheHolder) holder;
		final ResourceServiceBase<?, ?, ?> resourceService = item.ownGroup.define.resourceService;
		final boolean hasAuthority;
		resourceService.callBeforeAccessAuthorityResource(this);
		try {
			hasAuthority = this.getOperationAuthorityChecker().hasAuthority(operation, item);
		} finally {
			resourceService.callEndAccessAuthorityResource(this);
		}
		if (hasAuthority) {
			item.localTryRemoveReference((CacheHolder) reference, this.transaction);
		} else {
			throw new NoAccessAuthorityException("无访问权限。");
		}
	}

	public final <THolderFacade> void clearResourceReferences(
			ResourceToken<THolderFacade> holder, boolean absolutely) {
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		this.checkValid();
		((CacheHolder<?, ?, ?>) holder).tryRemoveAllReference(this.transaction);
	}

	private final <TFacade> CacheGroup<TFacade, ?, ?> getCacheGroup(
			final Class<TFacade> facadeClass) {
		final CacheGroup<TFacade, ?, ?> group = this.findCacheGroup(facadeClass);
		if (group == null) {
			throw new NotFoundGroupException(facadeClass, null);
		} else {
			return group;
		}
	}

	@SuppressWarnings("unchecked")
	final <TFacade> CacheGroup<TFacade, ?, ?> findCacheGroup(
			final Class<TFacade> facadeClass) {
		ResourceServiceBase<?, ?, ?> resourceService = this.occorAtResourceService;
		if (resourceService == null || facadeClass != resourceService.facadeClass) {
			resourceService = this.occorAt.findResourceService(facadeClass, this.getInvokeeQueryMode());
			if (resourceService == null) {
				return null;
			}
		}
		final CacheDefine<?, ?, ?> define = resourceService.getCacheDefine();
		final CacheGroup<?, ?, ?> group;
		if (define.kind.inSession) {
			final SessionCacheGroupContainer sessionGroupContainer = this.session.getCacheGroupContainer();
			group = sessionGroupContainer.getDefaultGroup(define, this.transaction);
		} else {
			group = define.ownCache.defaultGroupSpace.findGroup(facadeClass, this.transaction);
		}
		return (CacheGroup<TFacade, ?, ?>) group;
	}

	@SuppressWarnings("unchecked")
	final Class<TFacadeM> getFacadeClass() {
		return (Class<TFacadeM>) (this.occorAtResourceService.facadeClass);
	}

	private GUID currentACVersion;

	private volatile UserAccessController operationAuthorityChecker;

	private volatile UserAccessController accreditAuthorityChecker;

	final void resetACLCache() {
		this.operationAuthorityChecker = null;
		this.accreditAuthorityChecker = null;
	}

	final UserAccessController getOperationAuthorityChecker() {
		synchronized (this) {
			UserAccessController accessController = this.operationAuthorityChecker;
			final GUID currentACVersion = this.currentACVersion;
			if (accessController == null || !(AccessControlConstants.isDefaultACVersion(currentACVersion) ? AccessControlConstants.isDefaultACVersion(accessController.getOrgID()) : currentACVersion.equals(accessController.getOrgID()))) {
				accessController = this.operationAuthorityChecker = UserAccessController.allocUserAccessController(this.session.getUser(), this.currentACVersion, true, this);
			}
			return accessController;
		}
	}

	final UserAccessController getAccreditAuthorityChecker() {
		synchronized (this) {
			UserAccessController accessController = this.accreditAuthorityChecker;
			final GUID currentACVersion = this.currentACVersion;
			if (accessController == null || !(AccessControlConstants.isDefaultACVersion(currentACVersion) ? AccessControlConstants.isDefaultACVersion(accessController.getOrgID()) : currentACVersion.equals(accessController.getOrgID()))) {
				accessController = this.accreditAuthorityChecker = UserAccessController.allocUserAccessController(this.session.getUser(), this.currentACVersion, false, this);
			}
			return accessController;
		}
		// if (this.accreditAuthorityChecker == null) {
		// synchronized (this) {
		// if (this.accreditAuthorityChecker == null) {
		// this.accreditAuthorityChecker = UserAccessController
		// .allocUserAccessController(this.session.getUser(),
		// this.currentACVersion, this.transaction,
		// false);
		// }
		// }
		// }
		// return this.accreditAuthorityChecker;
	}

	public final void setUserCurrentOrg(GUID orgID) {
		if (this.session.getUser().isBuildInUser()) {
			throw new UnsupportedOperationException("当前用户不支持权限相关的操作");
		} else {
			final GUID ACVersion = SessionImpl.getAdjuestedUserIdentify(this, this.session.getUser(), orgID);
			synchronized (this) {
				if (this.currentACVersion == null) {
					if (orgID == null) {
						return;
					}
				} else if (this.currentACVersion.equals(orgID)) {
					return;
				}
				this.currentACVersion = ACVersion;
			}
		}
	}

	public final GUID getUserCurrentOrg() {
		return this.currentACVersion;
	}

	@SuppressWarnings("unchecked")
	public final <TResFacade> Authority getAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (resource instanceof CacheHolder) {
			final CacheHolder<TResFacade, ?, ?> holder = (CacheHolder<TResFacade, ?, ?>) resource;
			final ResourceServiceBase<?, ?, ?> resouceService = holder.ownGroup.define.resourceService;
			resouceService.callBeforeAccessAuthorityResource(this);
			try {
				return this.getOperationAuthorityChecker().getAuthority(operation, holder);
			} finally {
				resouceService.callEndAccessAuthorityResource(this);
			}
		} else {
			throw new UnsupportedAccessControlException(resource.getFacadeClass());
		}
	}

	@SuppressWarnings("unchecked")
	public final <TResFacade> boolean hasAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (resource instanceof CacheHolder) {
			final CacheHolder<TResFacade, ?, ?> holder = (CacheHolder<TResFacade, ?, ?>) resource;
			final ResourceServiceBase<?, ?, ?> resouceService = holder.ownGroup.define.resourceService;
			resouceService.callBeforeAccessAuthorityResource(this);
			try {
				return this.getOperationAuthorityChecker().hasAuthority(operation, holder);
			} finally {
				resouceService.callEndAccessAuthorityResource(this);
			}
		} else {
			throw new UnsupportedAccessControlException(resource.getFacadeClass());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final <TResFacade> Authority getAccreditAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (resource instanceof CacheHolder) {
			final CacheHolder<TResFacade, ?, ?> holder = (CacheHolder<TResFacade, ?, ?>) resource;
			final ResourceServiceBase<?, ?, ?> resouceService = holder.ownGroup.define.resourceService;
			resouceService.callBeforeAccessAuthorityResource(this);
			try {
				return this.getAccreditAuthorityChecker().getAuthority(operation, (ResourceToken) resource);
			} finally {
				resouceService.callEndAccessAuthorityResource(this);
			}
		} else {
			throw new UnsupportedAccessControlException(resource.getFacadeClass());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final <TResFacade> boolean hasAccreditAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resource == null) {
			throw new NullArgumentException("resource");
		}
		if (resource instanceof CacheHolder) {
			final CacheHolder<TResFacade, ?, ?> holder = (CacheHolder<TResFacade, ?, ?>) resource;
			final ResourceServiceBase<?, ?, ?> resouceService = holder.ownGroup.define.resourceService;
			resouceService.callBeforeAccessAuthorityResource(this);
			try {
				return this.getAccreditAuthorityChecker().hasAuthority(operation, (ResourceToken) resource);
			} finally {
				resouceService.callEndAccessAuthorityResource(this);
			}
		} else {
			throw new UnsupportedAccessControlException(resource.getFacadeClass());
		}
	}

	/**
	 * @param orgID
	 *            该参数已失效
	 */
	public final RoleAuthorityChecker newRoleAuthorityChecker(Role role,
			GUID orgID, boolean operationAuthority) {
		if (role == null) {
			throw new NullArgumentException("role");
		}
		if (role.getClass() == RoleCacheHolder.class) {
			return AccessControlPolicy.CURRENT_POLICY.newRoleAccessController((RoleCacheHolder) role, operationAuthority, this.transaction);
		} else {
			return this.newRoleAuthorityChecker(role.getID(), orgID, operationAuthority);
		}
	}

	/**
	 * @param orgID
	 *            该参数已失效
	 */
	public final RoleAuthorityChecker newRoleAuthorityChecker(GUID roleID,
			GUID orgID, boolean operationAuthority) {
		if (roleID == null) {
			throw new NullArgumentException("roleID");
		}
		final CacheHolder<?, ?, ?> role = this.getCacheGroup(Role.class).accessControlInformation.accessControlIndex.findAccessControlHolder(roleID, this.transaction);
		if (role == null) {
			throw new NotFoundItemException(Role.class, roleID);
		} else {
			return AccessControlPolicy.CURRENT_POLICY.newRoleAccessController((RoleCacheHolder) role, operationAuthority, this.transaction);
		}
	}

	public final UserAuthorityChecker newUserAuthorityChecker(User user,
			GUID orgID, boolean operationAuthority) {
		if (user == null) {
			throw new NullArgumentException("user");
		}
		if (user instanceof BuildInUser) {
			return ((BuildInUser) user).getAccessController();
		} else if (user instanceof UserImplement) {
			return AccessControlCompatible.newUserAccessController(AccessControlPolicy.CURRENT_POLICY, ((UserImplement) user).userHolder, orgID, operationAuthority, this);
			// return
			// AccessControlPolicy.CURRENT_POLICY.newUserAccessController(
			// ((UserImplement) user).userHolder, orgID,
			// operationAuthority, this.transaction);
		} else {
			return this.newUserAuthorityChecker(user.getID(), orgID, operationAuthority);
		}
	}

	public final UserAuthorityChecker newUserAuthorityChecker(GUID userID,
			GUID orgID, boolean operationAuthority) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		final CacheHolder<?, ?, ?> user = this.getCacheGroup(User.class).accessControlInformation.accessControlIndex.findAccessControlHolder(userID, this.transaction);
		if (user == null) {
			throw new NotFoundItemException(User.class, userID);
		} else {
			return AccessControlCompatible.newUserAccessController(AccessControlPolicy.CURRENT_POLICY, (UserCacheHolder) user, orgID, operationAuthority, this);
			// return
			// AccessControlPolicy.CURRENT_POLICY.newUserAccessController(
			// (UserCacheHolder) user, orgID, operationAuthority,
			// this.transaction);
		}
	}

	// --------------------------以上资源相关-----------------------------------

	// --------------------------以下授权信息相关-----------------------------------
	/**
	 * 查找资授权项
	 * 
	 * @param licenseEntryName
	 *            授权项名称
	 * @return 返回找到的授权项或null
	 */
	public final LicenseEntry findLicenseEntry(String licenseEntryName) {
		return this.occorAt.site.findLicenseEntry(licenseEntryName, this);
	}

	// --------------------------以上授权信息相关-----------------------------------

	// ---------------------------以下远程调用相关------------------------------
	@Deprecated
	public final RemoteServiceInvoker newRemoteServiceInvoker(URL url) {
		return this.newRemoteServiceInvoker(url, null, null, null);
	}

	@Deprecated
	public final RemoteServiceInvoker newRemoteServiceInvoker(URL url,
			Proxy proxy) {
		return this.newRemoteServiceInvoker(url, null, null, proxy);
	}

	@Deprecated
	public final RemoteServiceInvoker newRemoteServiceInvoker(URL url,
			String userName, GUID passwordMD5) {
		return this.newRemoteServiceInvoker(url, userName, passwordMD5, null);
	}

	@Deprecated
	public final RemoteServiceInvoker newRemoteServiceInvoker(URL url,
			String userName, GUID passwordMD5, Proxy proxy) {
		return this.newEfficientRemoteServiceInvoker(url, userName, passwordMD5, proxy);
	}

	public final RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url) {
		return this.newEfficientRemoteServiceInvoker(url, null, null, null);
	}

	public final RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url,
			Proxy proxy) {
		return this.newEfficientRemoteServiceInvoker(url, null, null, proxy);
	}

	public final RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url,
			String userName, GUID passwordMD5) {
		return this.newEfficientRemoteServiceInvoker(url, userName, passwordMD5, null);
	}

	public final RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url,
			String userName, GUID passwordMD5, Proxy proxy) {
		if (url == null) {
			throw new NullArgumentException("url");
		}
		ApplicationImpl app = this.session.application;
		final NetNodeToken remoteNodeInfo = app.netNodeManager.queryRemoteNodeID(url, proxy);
		if (remoteNodeInfo.appID.equals(app.localNodeID)) {
			return this;
		}
		return new RemoteServiceInvokerImpl(this.transaction, remoteNodeInfo, userName, passwordMD5, true);
	}

	final RemoteServiceInvoker newCallerRemoteServiceInvoker(String userName,
			GUID passwordMD5, boolean newTrans) {
		if (this.remoteCaller == null) {
			throw new UnsupportedOperationException("当前上下文非远程调用上下文，不能构造回调接口");
		}
		return new RemoteServiceInvokerImpl(this.transaction, userName, passwordMD5, this.remoteCaller, newTrans);
	}

	// ---------------------------以上远程调用相关------------------------------

	public final Proxy getProxy() {
		return null;
	}

	public final URL getURL() {
		return null;
	}

	public final String getUserName() {
		return null;
	}

	public final void unuse() {
	}

	public final ReliableRSInvokerImpl newReliableRemoteServiceInvoker(
			final URL url) {
		return this.newReliableRemoteServiceInvoker(url, null, null, null);
	}

	public final ReliableRSInvokerImpl newReliableRemoteServiceInvoker(
			final URL url, final Proxy proxy) {
		return this.newReliableRemoteServiceInvoker(url, null, null, proxy);
	}

	public final ReliableRSInvokerImpl newReliableRemoteServiceInvoker(
			final URL url, final String userName, final GUID passwordMD5) {
		return this.newReliableRemoteServiceInvoker(url, userName, passwordMD5, null);
	}

	public final ReliableRSInvokerImpl newReliableRemoteServiceInvoker(
			final URL url, final String userName, final GUID passwordMD5,
			final Proxy proxy) {
		if (url == null) {
			throw new NullArgumentException("url");
		}
		// final ApplicationImpl application = this.session.application;
		// final NetNodeToken remoteNodeInfo = application.netNodeManager
		// .queryRemoteNodeID(url, proxy);
		// if (remoteNodeInfo.id.equals(application.localNodeID)) {
		// return this;
		// }
		// final NSerializerFactory serializerFactory = NSerializer
		// .getRemoteCompatibleFactory(this.session.application.netChannelManager
		// .ensureChannel(remoteNodeInfo)
		// .getRemoteSerializeVersion());
		return new ReliableRSInvokerImpl(url, userName, passwordMD5, proxy);
	}

	final void checkCacheModifiable(CacheDefine<?, ?, ?> define,
			Object spaceIdentifier) {
		if (this.kind != ContextKind.INITER) {
			define.checkModfiable(spaceIdentifier);
		}
	}
}
