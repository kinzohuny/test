package com.jiuqi.dna.core.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.Login;
import com.jiuqi.dna.core.exception.NullArgumentException;

final class DNALogger implements com.jiuqi.dna.core.log.Logger {
	private static final String buildLogMassage(final Context context,
			final Object message) {
		final StringBuffer stringBuffer = new StringBuffer();
		if (context != null) {
			final Login login = context.getLogin();
			stringBuffer.append("LoginUser: ").append(login.getUser().getName()).append("\r\n").append("SessionID: ").append(login.getID()).append("\r\n");
		}
		stringBuffer.append("Message: ");
		stringBuffer.append(message.toString());
		return stringBuffer.toString();
	}

	DNALogger(final Logger fourJLogger) {
		this.category = fourJLogger.getName();
		this.fourJLogger = fourJLogger;
	}

	public final void logFatal(final Context context, final Object message,
			final boolean isForwardTell) {
		this.log(context, Level.FATAL, message, null, isForwardTell);
	}

	public final void logFatal(final Context context,
			final Throwable throwable, final boolean isForwardTell) {
		this.log(context, Level.FATAL, null, throwable, isForwardTell);
	}

	public void logFatal(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
		this.log(context, Level.FATAL, message, throwable, isForwardTell);
	}

	public final void logError(final Context context, final Object message,
			final boolean isForwardTell) {
		this.log(context, Level.ERROR, message, null, isForwardTell);
	}

	public final void logError(final Context context,
			final Throwable throwable, final boolean isForwardTell) {
		this.log(context, Level.ERROR, null, throwable, isForwardTell);
	}

	public void logError(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
		this.log(context, Level.ERROR, message, throwable, isForwardTell);
	}

	public final void logWarn(final Context context, final Object message,
			final boolean isForwardTell) {
		this.log(context, Level.WARN, message, null, isForwardTell);
	}

	public final void logWarn(final Context context, final Throwable throwable,
			final boolean isForwardTell) {
		this.log(context, Level.WARN, null, throwable, isForwardTell);
	}

	public void logWarn(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
		this.log(context, Level.WARN, message, throwable, isForwardTell);
	}

	public final void logInfo(final Context context, final Object message,
			final boolean isForwardTell) {
		this.log(context, Level.INFO, message, null, isForwardTell);
	}

	public final void logInfo(final Context context, final Throwable throwable,
			final boolean isForwardTell) {
		this.log(context, Level.INFO, null, throwable, isForwardTell);
	}

	public void logInfo(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
		this.log(context, Level.INFO, message, throwable, isForwardTell);
	}

	public final void logDebug(final Context context, final Object message,
			final boolean isForwardTell) {
		this.log(context, Level.DEBUG, message, null, isForwardTell);
	}

	public final void logDebug(final Context context,
			final Throwable throwable, final boolean isForwardTell) {
		this.log(context, Level.DEBUG, null, throwable, isForwardTell);
	}

	public void logDebug(Context context, Object message, Throwable throwable,
			boolean isForwardTell) {
		this.log(context, Level.DEBUG, message, throwable, isForwardTell);
	}

	/**
	 * @param isForwardTell
	 *            是否向上级目录记录
	 */
	private final void log(final Context context, final Level level,
			Object message, final Throwable throwable,
			final boolean isForwardTell) {
		if (level == null) {
			throw new NullArgumentException("level");
		}
		if (message == null && throwable == null) {
			throw new NullArgumentException("message|throwable");
		}
		if (message != null) {
			message = buildLogMassage(context, message);
		}
		this.fourJLogger.log(level, message, throwable);
		// 如果要向上级目录记录
		if (isForwardTell) {
			DNALogger superior = this.Superior;
			while (superior != null) {
				superior.fourJLogger.log(level, message, throwable);
				superior = superior.Superior;
			}
		}
	}

	final String category;

	final Logger fourJLogger;

	volatile DNALogger Superior;

}
