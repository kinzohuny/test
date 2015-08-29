package com.jiuqi.dna.core.spi.application;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.impl.ContextProviderBase;

/**
 * 上下文获取类
 * 
 * @author gaojingxin
 * 
 */
public final class ContextProvider extends ContextProviderBase {
	public ContextProvider(Context context) {
		super(context);
	}

	/**
	 * 获得当前上下文
	 */
	@Override
	public final Context getCurrentContext() throws IllegalStateException {
		return super.getCurrentContext();
	}
}
