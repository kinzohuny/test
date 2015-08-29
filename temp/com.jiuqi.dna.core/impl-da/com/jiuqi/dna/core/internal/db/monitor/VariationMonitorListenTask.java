package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.invoke.SimpleTask;

final class VariationMonitorListenTask extends SimpleTask {

	final String monitor;

	VariationMonitorListenTask(String monitor) {
		this.monitor = monitor;
	}

	Throwable exception;
}