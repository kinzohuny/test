package com.jiuqi.dna.core.impl;

final class ConflictingDefineException extends RuntimeException {

	private static final long serialVersionUID = -6035497447764540849L;

	ConflictingDefineException(final Class<?> facadeClass) {
		super("�ظ��������������Ϊ[" + facadeClass + "]�Ļ��涨�塣");
	}
}