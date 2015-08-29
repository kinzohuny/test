package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * �������Ի���
 */
public abstract class RebootStrategy {
	/**
	 * ��ϵͳ����ʱ���ø÷�������ʼ���������ԡ�<br>
	 */
	protected void init(Context context, SXElement config) {
	}

	/**
	 * ȫ�������ø���ȷ����ͬ����������֮���ǻ���ģ�ͬһ��ʱ��ֻ��һ���������Կ��Խ�����������
	 */
	protected final void lock(Context context) {
		context.lockResourceU(context.getResourceToken(SynClusterClock.class));
	}

	/**
	 * �˳�ϵͳ��������
	 */
	protected final void shutdown(Context context, boolean restart)
			throws Throwable {
		this.lock(context);
		ContextImpl<?, ?, ?> c = (ContextImpl<?, ?, ?>) context;
		c.occorAt.site.shutdown(c, restart);
	}
}
