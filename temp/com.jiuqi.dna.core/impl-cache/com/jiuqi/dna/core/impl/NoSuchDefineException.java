package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.GUID;

final class NoSuchDefineException extends RuntimeException {

	private static final long serialVersionUID = 1812737042383295571L;

	NoSuchDefineException(final GUID GUIDIdentifier) {
		super("û�б�ʶΪ[" + GUIDIdentifier + "]�Ļ��涨�塣");
	}

	NoSuchDefineException(final Class<?> facadeClass) {
		super("û���������Ϊ[" + facadeClass + "]�Ļ��涨�塣");
	}
}