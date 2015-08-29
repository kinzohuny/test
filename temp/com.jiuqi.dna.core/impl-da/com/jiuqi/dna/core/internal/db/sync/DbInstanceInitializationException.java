package com.jiuqi.dna.core.internal.db.sync;

public final class DbInstanceInitializationException extends RuntimeException {

	private static final long serialVersionUID = -5005907043228606242L;

	public DbInstanceInitializationException(String message) {
		super("���ݿ��ʼ������:" + message);
	}

	public DbInstanceInitializationException(String message, Throwable cause) {
		super("���ݿ��ʼ������:" + message, cause);
	}
}