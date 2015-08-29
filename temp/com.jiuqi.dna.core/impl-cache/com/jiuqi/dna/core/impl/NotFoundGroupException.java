package com.jiuqi.dna.core.impl;

final class NotFoundGroupException extends RuntimeException {

	private static final long serialVersionUID = -48763467593197085L;

	NotFoundGroupException(final long identifier) {
		super("�Ҳ�����ʶΪ[" + identifier + "]�Ļ����顣");
	}

	NotFoundGroupException(final Class<?> facadeClass, final Object identifier) {
		super("�Ҳ����������Ϊ[" + facadeClass + "]����ռ��ʶΪ[" + identifier + "]�Ļ����顣");
	}
}