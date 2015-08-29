package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.exception.NullArgumentException;

//TODO 临时提供，需要整改
public abstract class ContextProviderBase {
	final ApplicationImpl app;

	public ContextProviderBase(Context context) {
		if (context == null) {
			throw new NullArgumentException("context");
		}
		this.app = ContextImpl.toContext(context).session.application;
	}

	protected Context getCurrentContext() {
		ContextImpl<?, ?, ?> c = this.app.contextLocal.get();
		if (c == null) {
			throw new IllegalStateException("当前线程不存在DNA上下文，不在Core的管理范围内。");
		}
		return c;
	}
}
