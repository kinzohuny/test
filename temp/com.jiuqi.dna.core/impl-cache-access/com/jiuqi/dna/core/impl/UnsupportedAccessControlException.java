package com.jiuqi.dna.core.impl;

final class UnsupportedAccessControlException extends RuntimeException {

	private static final long serialVersionUID = 1803296216555873270L;

	UnsupportedAccessControlException(final Class<?> facadeClass) {
		super("�������Ϊ[" + facadeClass + "]�Ļ��涨�岻֧�ַ��ʿ�����صĲ�����");
	}
}