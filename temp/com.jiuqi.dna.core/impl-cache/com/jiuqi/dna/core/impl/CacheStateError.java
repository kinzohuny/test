package com.jiuqi.dna.core.impl;

final class CacheStateError extends Error {

	private static final long serialVersionUID = 2731280318002187077L;

	CacheStateError() {
		super("����״̬����");
	}

	CacheStateError(final Object errorState) {
		super("��ʱ״̬��Ӧ��Ϊ[" + errorState + "]��");
	}
}