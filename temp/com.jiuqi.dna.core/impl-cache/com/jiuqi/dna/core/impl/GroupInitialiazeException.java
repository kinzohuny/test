package com.jiuqi.dna.core.impl;

final class GroupInitialiazeException extends RuntimeException {

	private static final long serialVersionUID = -5530222222870583443L;

	GroupInitialiazeException(final Class<?> facadeClass,
			final Object spaceIdentifier, final Throwable e) {
		super("�������ʼ���Ĺ����г����쳣���������Ϊ[" + facadeClass + "]����ռ��ʶ[" + spaceIdentifier + "]", e);
	}

	GroupInitialiazeException(final Class<?> facadeClass,
			final Object spaceIdentifier) {
		super("�������ʼ���Ĺ����г����쳣���������Ϊ[" + facadeClass + "]����ռ��ʶ[" + spaceIdentifier + "]");
	}

	GroupInitialiazeException(final Class<?> facadeClass,
			final Object spaceIdentifier, final String message) {
		super(message + "�������Ϊ[" + facadeClass + "]����ռ��ʶ[" + spaceIdentifier + "]");
	}

}
