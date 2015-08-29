package com.jiuqi.dna.core.exception;

/**
 * Core所引发的异常的基类
 * 
 * @author gaojingxin
 * 
 */
public final class AbortException extends CoreException {
	private static final long serialVersionUID = -1L;

	public AbortException() {
	}

	public AbortException(Throwable cause) {
		super(cause);
	}
}
