package com.jiuqi.dna.core.log;

import com.jiuqi.dna.core.Context;

/**
 * 日志记录
 * 
 * @author hanfei
 * 
 */
public interface Logger {

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param massage
	 *            日志信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logFatal(Context context, Object message, boolean isForwardTell);

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param throwable
	 *            异常信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logFatal(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logFatal(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param massage
	 *            日志信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logError(Context context, Object message, boolean isForwardTell);

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param throwable
	 *            异常信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logError(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logError(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param massage
	 *            日志信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logWarn(Context context, Object message, boolean isForwardTell);

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param throwable
	 *            异常信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logWarn(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logWarn(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param massage
	 *            日志信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logInfo(Context context, Object message, boolean isForwardTell);

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param throwable
	 *            异常信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logInfo(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logInfo(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param massage
	 *            日志信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logDebug(Context context, Object message, boolean isForwardTell);

	/**
	 * 记录日志
	 * 
	 * @param context
	 *            上下文
	 * @param throwable
	 *            异常信息
	 * @param isForwardTell
	 *            是否记录到所有上级目录对应的日志文件中
	 */
	public void logDebug(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logDebug(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

}
