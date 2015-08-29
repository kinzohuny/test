package com.jiuqi.dna.core.exception;

import com.jiuqi.dna.core.auth.Operation;

/**
 * 无访问权限异常<br>
 * 如果用户试图对某个资源进行未授权的操作，则抛出此异常。
 * 
 * @see com.jiuqi.dna.core.exception.CoreException
 * @author LiuZhi 2010-01-08
 */
public final class NoAccessAuthorityException extends CoreException {

	private static final long serialVersionUID = 9053494390777139222L;

	public NoAccessAuthorityException() {
		super("无访问权限。");
	}

	/**
	 * 抛出无访问权限异常<br>
	 * 如果用户试图对某个资源进行未授权的操作，则抛出此异常。
	 * 
	 * @param message
	 *            异常信息
	 */
	public NoAccessAuthorityException(String message) {
		super(message);
	}

	/**
	 * 抛出无访问权限异常<br>
	 * 如果用户试图对某个资源进行未授权的操作，则抛出此异常。
	 * 
	 * @param resourceDescription
	 *            资源的描述信息
	 * @param operation
	 *            对资源的操作
	 */
	public NoAccessAuthorityException(String resourceDescription,
			Operation<?> operation) {
		super("当前用户没有对资源[" + resourceDescription + "]的[" + operation.getTitle()
				+ "]操作权限。");
	}

}
