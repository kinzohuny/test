package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.invoke.SimpleTask;

public final class ClockedRestartAppTask extends SimpleTask {

	final ClockedRebootStrategyBase strategy;

	public ClockedRestartAppTask(ClockedRebootStrategyBase strategy) {
		this.strategy = strategy;
	}

}
