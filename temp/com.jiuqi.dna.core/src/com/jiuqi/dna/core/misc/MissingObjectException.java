package com.jiuqi.dna.core.misc;
/**
 * �Ҳ���
 * @author gaojingxin
 * 
 */
public class MissingObjectException extends RuntimeException {
	private static final long serialVersionUID = 1283599152796080609L;
	public MissingObjectException() {
		super();
	}
	public MissingObjectException(String message, Throwable cause) {
		super(message, cause);
	}
	public MissingObjectException(String message) {
		super(message);
	}
	public MissingObjectException(Throwable cause) {
		super(cause);
	}
}
