package com.jiuqi.dna.core.impl;

final class NotFoundItemException extends RuntimeException {

	private static final long serialVersionUID = 333143711599293975L;

	NotFoundItemException(final long identifier) {
		super("�Ҳ�����ʶΪ[" + identifier + "]�Ļ����");
	}

	NotFoundItemException(final Class<?> facadeClass) {
		super("�Ҳ����������Ϊ[" + facadeClass + "]�ĵ��������");
	}

	NotFoundItemException(final Class<?> facadeClass, final Object key) {
		super("�Ҳ����������Ϊ[" + facadeClass + "]����ֵΪ[" + key + "]�Ļ����");
	}

	NotFoundItemException(final Class<?> facadeClass, final Object key1,
			final Object key2) {
		super("�Ҳ����������Ϊ[" + facadeClass + "]����ֵΪ[" + key1 + "��" + key2 + "]�Ļ����");
	}

	NotFoundItemException(final Class<?> facadeClass, final Object key1,
			final Object key2, final Object key3) {
		super("�Ҳ����������Ϊ[" + facadeClass + "]����ֵΪ[" + key1 + "��" + key2 + "��" + key3 + "]�Ļ����");
	}

}
