package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;

/**
 * ��Դ������<br>
 * Ŀ������ڳ�ʼ������Ȩ��Դʱ����������е�һЩ��������
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
	 * ����һ����Դ������
	 * 
	 * @param operation
	 *            ��Դ����������Ϊ��
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
	 * ��Դ����
	 */
	final Operation<?> operation;

	/**
	 * ��Ȩ����
	 */
	final int authorityMask;

	/**
	 * ������Ȩ����
	 */
	final int allowAuthorityCode;

	/**
	 * �ܾ���Ȩ����
	 */
	final int denyAuthorityCode;

}
