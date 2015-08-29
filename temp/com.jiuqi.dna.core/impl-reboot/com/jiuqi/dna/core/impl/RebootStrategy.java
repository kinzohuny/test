package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 重启策略基类
 */
public abstract class RebootStrategy {
	/**
	 * 在系统启动时调用该方法，初始化重启策略。<br>
	 */
	protected void init(Context context, SXElement config) {
	}

	/**
	 * 全局锁，用该锁确保不同的重启策略之间是互斥的，同一个时刻只有一个重启策略可以进行重启操作
	 */
	protected final void lock(Context context) {
		context.lockResourceU(context.getResourceToken(SynClusterClock.class));
	}

	/**
	 * 退出系统或者重启
	 */
	protected final void shutdown(Context context, boolean restart)
			throws Throwable {
		this.lock(context);
		ContextImpl<?, ?, ?> c = (ContextImpl<?, ?, ?>) context;
		c.occorAt.site.shutdown(c, restart);
	}
}
