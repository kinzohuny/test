package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.exception.NullArgumentException;

//TODO ��ʱ�ṩ����Ҫ����
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
			throw new IllegalStateException("��ǰ�̲߳�����DNA�����ģ�����Core�Ĺ���Χ�ڡ�");
		}
		return c;
	}
}
