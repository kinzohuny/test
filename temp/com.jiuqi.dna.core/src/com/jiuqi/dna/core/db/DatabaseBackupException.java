package com.jiuqi.dna.core.db;

/**
 * ���ݿⱸ���쳣
 * 
 * @author houchunlei
 *
 */
public final class DatabaseBackupException extends RuntimeException {

	private static final long serialVersionUID = -6716490019379776121L;

	public DatabaseBackupException(String message) {
		super(message);
	}

	public DatabaseBackupException(String message, Throwable cause) {
		super(message, cause);
	}
}