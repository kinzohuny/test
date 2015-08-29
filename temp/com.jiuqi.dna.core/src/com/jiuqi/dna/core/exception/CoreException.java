package com.jiuqi.dna.core.exception;

/**
 * Core���������쳣�Ļ���
 * 
 * @author gaojingxin
 * 
 */
public abstract class CoreException extends RuntimeException {

	private static final long serialVersionUID = -4678113765059209215L;

	CoreException() {

	}

	CoreException(String message) {
		super(message);
	}

	CoreException(String message, Throwable cause) {
		super(message, cause);
	}

	CoreException(Throwable cause) {
		super(cause);
	}
}
