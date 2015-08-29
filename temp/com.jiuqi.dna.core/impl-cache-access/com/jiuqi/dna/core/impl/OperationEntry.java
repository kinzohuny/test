package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;

/**
 * 资源操作项<br>
 * 目标的在于初始化可授权资源时，计算操作中的一些常用量。
 * 
 * @see com.jiuqi.dna.core.auth.Operation
 * @author LiuZhi 2009-12
 */
final class OperationEntry implements Operation<Object> {

	static final OperationEntry operationEntryOf(final Operation<?> operation) {
		return new OperationEntry(operation);
	}

	static final OperationEntry operationEntryOf(final Operation<?> operation,
			final OperationEntry[] from) {
		return from[((Enum<?>) operation).ordinal()];
	}

	/**
	 * 构造一个资源操作项
	 * 
	 * @param operation
	 *            资源操作，不能为空
	 */
	OperationEntry(final Operation<?> operation) {
		this.operation = operation;
		final int operationMask = operation.getMask();
		this.authorityMask = AccessControlHelper.toAuthorityMask(operationMask);
		this.allowAuthorityCode = AccessControlHelper.toAuthorityCode(operationMask, Authority.ALLOW.code);
		this.denyAuthorityCode = AccessControlHelper.toAuthorityCode(operationMask, Authority.DENY.code);
		this.index = ((Enum<?>) operation).ordinal();
	}

	public final String getTitle() {
		return this.operation.getTitle();
	}

	public final int getMask() {
		return this.operation.getMask();
	}

	final int index;

	/**
	 * 资源操作
	 */
	final Operation<?> operation;

	/**
	 * 授权掩码
	 */
	final int authorityMask;

	/**
	 * 允许授权编码
	 */
	final int allowAuthorityCode;

	/**
	 * 拒绝授权编码
	 */
	final int denyAuthorityCode;

}
