package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.CacheDefine.KeyDefine;

final class ConflictingKeyValueException extends RuntimeException {

	private static final <TKeysHolder> String buildKeyValueInformation(
			final KeyDefine<?, ?, TKeysHolder> keyDefine,
			final TKeysHolder keysHolder) {
		return keyDefine.getKeyValue1(keysHolder) + "��" + keyDefine.getKeyValue2(keysHolder) + "��" + keyDefine.getKeyValue3(keysHolder);
	}

	private static final long serialVersionUID = 1932212758821289628L;

	ConflictingKeyValueException(final Class<?> facadeClass) {
		super("�������Ϊ[" + facadeClass + "]�����������Ѵ��ڡ�");
	}

	<TKeysHolder> ConflictingKeyValueException(final Class<?> facadeClass,
			final KeyDefine<?, ?, TKeysHolder> keyDefine,
			final TKeysHolder keysHolder) {
		super("�������Ϊ[" + facadeClass + "]����ֵΪ[" + buildKeyValueInformation(keyDefine, keysHolder) + "]�Ļ������Ѵ��ڡ�");
	}

}
