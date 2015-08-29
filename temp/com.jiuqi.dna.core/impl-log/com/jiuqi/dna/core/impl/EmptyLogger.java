package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.log.Logger;

public final class EmptyLogger implements Logger {

	private EmptyLogger() {
	}

	public static final EmptyLogger INSTANCE = new EmptyLogger();

	public void logFatal(Context context, Object message, boolean isForwardTell) {
	}

	public void logFatal(Context context, Throwable throwable,
			boolean isForwardTell) {
	}

	public void logFatal(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
	}

	public void logError(Context context, Object message, boolean isForwardTell) {
	}

	public void logError(Context context, Throwable throwable,
			boolean isForwardTell) {
	}

	public void logError(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
	}

	public void logWarn(Context context, Object message, boolean isForwardTell) {
	}

	public void logWarn(Context context, Throwable throwable,
			boolean isForwardTell) {
	}

	public void logWarn(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
	}

	public void logInfo(Context context, Object message, boolean isForwardTell) {
	}

	public void logInfo(Context context, Throwable throwable,
			boolean isForwardTell) {
	}

	public void logInfo(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
	}

	public void logDebug(Context context, Object message, boolean isForwardTell) {
	}

	public void logDebug(Context context, Throwable throwable,
			boolean isForwardTell) {
	}

	public void logDebug(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
	}
}