package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.spi.log.LogEntry;

/**
 * 日志任务（内部）
 * 
 * @author gaojingxin
 * 
 */
public abstract class LogTaskInternal extends SimpleTask {
	private final LogManager logManager;

	public LogTaskInternal(Object logManager) {
		if (logManager == null) {
			throw new NullArgumentException("logManager");
		}
		this.logManager = (LogManager) logManager;
	}

	protected LogEntry nextLogEntry() {
		return this.logManager.loggerGetToLog();
	}
}