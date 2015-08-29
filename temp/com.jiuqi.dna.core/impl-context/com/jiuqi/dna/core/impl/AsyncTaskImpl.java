package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.service.AsyncInfo.SessionMode;
import com.jiuqi.dna.core.spi.work.WorkingThread;

/**
 * �첽��������ӿڵ�ʵ��
 * 
 * @author gaojingxin
 * 
 * @param <TTask>
 * @param <TMethod>
 */
final class AsyncTaskImpl<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
		extends AsyncServiceInvoke implements AsyncTask<TTask, TMethod> {

	private static SessionImpl sessionOf(SessionImpl session,
			InternalAsyncInfo asyncInfo) {
		return asyncInfo != null && asyncInfo.sessionMode != SessionMode.SAME ? null : session;
	}

	AsyncTaskImpl(SessionImpl session, SpaceNode occurAt, TTask task,
			ServiceInvokeeBase<TTask, Context, None, None, None> handler,
			InternalAsyncInfo asyncInfo, Object await) {
		super(sessionOf(session, asyncInfo), occurAt);
		this.task = task;
		this.handler = handler;
		if (asyncInfo != null) {
			this.startime = asyncInfo.start;
			this.period = asyncInfo.period;
			switch (asyncInfo.sessionMode) {
			case INDIVIDUAL:
				final User user = session.getUser();
				if (user != BuildInUser.system) {
					this.individualSessionUser = user;
					break;
				}// ϵͳ�û��л�ʱ���������û�
			case INDIVIDUAL_ANONYMOUS:
				this.individualSessionUser = BuildInUser.anonym;
				break;
			default:
				this.individualSessionUser = null;
			}
		} else {
			this.individualSessionUser = null;
		}
		if (await == null) {
			super.beginAsync();
		} else {
			this.occurAt.site.application.overlappedManager.postWork(this, await);
		}
	}

	/**
	 * ����
	 */
	protected long period;
	/**
	 * ��ʼʱ��
	 */
	protected long startime;
	/**
	 * ��Ϊ�����Ựʱ���û�
	 */
	private final User individualSessionUser;

	@Override
	protected final boolean workBeginning() {
		final User individualSessionUser = this.individualSessionUser;
		if (individualSessionUser != null) {
			this.session = this.occurAt.site.application.sessionManager.newSession(SessionKind.TRANSIENT, individualSessionUser, null, null);
		}
		try {
			return super.workBeginning();
		} catch (Throwable e) {
			throw new RuntimeException("�ڿ�ʼ�첽����[" + this.task.getClass().getName() + "]ʱ�����쳣��", e);
		}
	}

	@Override
	protected final void workFinalizing(Throwable e) {
		try {
			super.workFinalizing(e);
		} finally {
			if (this.session != null) {
				switch (this.session.kind) {
				case TRANSIENT:
				case REMOTE:
					this.session.internalDispose(SessionImpl.IGNORE_IF_HAS_CONTEXT);
					break;
				default:
					if (this.individualSessionUser != null) {
						this.session.internalDispose(0l);
					} else {
						return;// û�б�dispose��Session֮��Ҫʹ��
					}
				}
				this.session = null;
			}
		}
	}

	@Override
	protected final long getStartTime() {
		final long st = super.getStartTime();
		if (st >= this.startime) {
			return st;
		}
		return this.startime;
	}

	@Override
	protected final boolean regeneration() {
		if (this.period > 0) {
			this.startime = System.currentTimeMillis() + this.period;
			return true;
		}
		return false;
	}

	@Override
	public
	final ConcurrentController getConcurrentController() {
		return this.handler.getConcurrentController();
	}

	@Override
	public final void waitStop(long timeout) throws InterruptedException {
		if (this.period > 0) {
			throw new UnsupportedOperationException("��֧�ֵȴ�����������");
		}
		super.waitStop(timeout);
	}

	private final ServiceInvokeeBase<TTask, Context, None, None, None> handler;
	private final TTask task;

	@Override
	protected final void workDoing(WorkingThread thread) {
		this.context.serviceHandleTask(this.task, this.handler);
	}

	public final TMethod getMethod() {
		return this.task.getMethod();
	}

	public final TTask getTask() throws IllegalStateException {
		this.checkFinished();
		return this.task;
	}
}
