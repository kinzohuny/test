package com.jiuqi.dna.core.auth;

/**
 * δ��Ȩ�ķ����쳣
 * 
 * @author houchunlei
 * 
 */
public final class UnauthorizedAccessException extends RuntimeException {

	private static final long serialVersionUID = 8516649194514139891L;

	public UnauthorizedAccessException(String message) {
		super(message);
	}
}