package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.TransientContainer.TransientProvider;

/**
 * 代理对象
 * 
 * @author gaojingxin
 * 
 */
public interface TransientProxy<TProvider extends TransientProvider> {
	public TProvider getProvider();
}
