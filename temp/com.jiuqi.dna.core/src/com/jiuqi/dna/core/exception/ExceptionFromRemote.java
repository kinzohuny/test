package com.jiuqi.dna.core.exception;

/**
 * 远程异常代理
 * 
 * @author gaojingxin
 * 
 */
public class ExceptionFromRemote extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String remoteExceptionClassName;

	public final String getRemoteExceptionClassName() {
		return this.remoteExceptionClassName;
	}

	public ExceptionFromRemote(String message, String remoteExceptionClassName) {
		super(remoteExceptionClassName + ": " + message);
		this.remoteExceptionClassName = remoteExceptionClassName;
	}

	public ExceptionFromRemote(String message, String remoteExceptionClassName,
			ExceptionFromRemote cause) {
		super(remoteExceptionClassName + ": " + message, cause);
		this.remoteExceptionClassName = remoteExceptionClassName;
	}
}
