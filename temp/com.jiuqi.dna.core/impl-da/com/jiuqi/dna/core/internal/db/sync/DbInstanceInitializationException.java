package com.jiuqi.dna.core.internal.db.sync;

public final class DbInstanceInitializationException extends RuntimeException {

	private static final long serialVersionUID = -5005907043228606242L;

	public DbInstanceInitializationException(String message) {
		super("数据库初始化错误:" + message);
	}

	public DbInstanceInitializationException(String message, Throwable cause) {
		super("数据库初始化错误:" + message, cause);
	}
}