package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.NamedDefine;

public interface RebootStrategyDefine extends NamedDefine {
	/**
	 * 重启策略类，该类实现了RebootStrategy
	 */
	public Class<?> getStrategyClass();
}
