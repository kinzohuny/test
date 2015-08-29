package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.SiteState;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.exception.UnsupportedContextKindException;
import com.jiuqi.dna.core.info.Info;
import com.jiuqi.dna.core.invoke.AsyncHandle;
import com.jiuqi.dna.core.invoke.Waitable;
import com.jiuqi.dna.core.spi.work.Work;

/**
 * �첽���û���
 * 
 * @author gaojingxin
 * 
 */
abstract class AsyncServiceInvoke extends Work implements AsyncHandle, Waitable {

	public final int fetchInfos(List<Info> to) {
		final ContextImpl<?, ?, ?> context = this.context;
		return context != null ? context.fetchInfos(to) : 0;
	}

	@Override
	protected boolean workBeginning() {
		try {
			this.context = this.session.newContext(this.occurAt, ContextKind.TRANSIENT);
		} catch (UnsupportedContextKindException e) {
			switch (this.occurAt.site.state) {
			case DISPOSED:
			case DISPOSING:
			case LOADING_METADATA:
			case INITING:
				return false;
			default:
				throw e;
			}
		}
		return true;
	}

	@Override
	protected long getStartTime() {
		if (this.occurAt.site.state == SiteState.INITING) {
			return System.currentTimeMillis() + 500L;
		}
		return 0l;
	}

	@Override
	protected void workFinalizing(Throwable e) {
		this.exception = e;
		if (this.context != null) {
			if (e == null) {
				this.finalProgress = 1;
			} else {
				this.finalProgress = -this.context.progress;
			}
			this.context.dispose();
			this.context = null;
		}
	}

	@Override
	public synchronized void waitStop(long timeout) throws InterruptedException {
		if (this.occurAt.site.state == SiteState.INITING) {
			throw new UnsupportedOperationException("��֧�ֵ�վ�����ʼ���ڼ��������첽���ã���Щ���ý���վ���ʼ����ɺ�Ż�����");
		}
		super.waitStop(timeout);
	}

	@Override
	protected final void workCanceling() {
		final ContextImpl<?, ?, ?> context = this.context;
		if (context != null) {
			context.cancel();
		}
	}

	/**
	 * ���������������쳣���򷵻ظ��쳣�����򷵻�null
	 * 
	 * @return �����쳣����null
	 */
	public final Throwable getException() {
		return this.exception;
	}

	/**
	 * ������ȣ�0��ʾ��δ����1��ʾ������ϣ�֮�������ʾ���ȣ�С���������ʾ��;���ִ���
	 * 
	 * @return ���ش������
	 */
	public final float getProgress() {
		ContextImpl<?, ?, ?> context = this.context;
		return context != null ? context.progress : this.finalProgress;
	}

	/**
	 * ִ����δ�ػ���쳣
	 */
	private Throwable exception;
	/**
	 * ���ս���
	 */
	private float finalProgress;
	/**
	 * ��ǰ���е�������
	 */
	protected ContextImpl<?, ?, ?> context;

	/**
	 * �Ự������Ϊ��
	 */
	SessionImpl session;
	/**
	 * ���õĿռ�λ��
	 */
	final SpaceNode occurAt;

	AsyncServiceInvoke(SessionImpl session, SpaceNode occurAt) {
		if (occurAt == null) {
			throw new NullArgumentException("occurAt");
		}
		this.occurAt = occurAt;
		this.session = session;
	}

	/**
	 * ��ʼ�첽����
	 */
	protected final void beginAsync() {
		this.occurAt.site.application.overlappedManager.postWork(this);
	}
}