package com.jiuqi.dna.core.spi.application;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.impl.ContextProviderBase;

/**
 * �����Ļ�ȡ��
 * 
 * @author gaojingxin
 * 
 */
public final class ContextProvider extends ContextProviderBase {
	public ContextProvider(Context context) {
		super(context);
	}

	/**
	 * ��õ�ǰ������
	 */
	@Override
	public final Context getCurrentContext() throws IllegalStateException {
		return super.getCurrentContext();
	}
}
