package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefine;
import com.jiuqi.dna.core.type.GUID;

abstract class CacheHolderIndex<TFacade, TImplement extends TFacade, TKeysHolder> {

	CacheHolderIndex(final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine) {
		this.keyDefine = keyDefine;
	}

	/**
	 * @return ����null��ʾû�ҵ�����ֵ�򻺴����Ѿ�������
	 */
	abstract CacheHolder<TFacade, TImplement, TKeysHolder> findHolder(
			Object keyValue1, Object keyValue2, Object keyValue3,
			Transaction transaction);

	/**
	 * @return ����null��ʾû�ҵ�����ֵ�򻺴����Ѿ�������
	 */
	abstract CacheHolder<TFacade, TImplement, TKeysHolder> findHolder(
			AccessController accessController,
			Operation<? super TFacade> operation, Object keyValue1,
			Object keyValue2, Object keyValue3, Transaction transaction);

	/**
	 * @return ����null��ʾû�ҵ�����ֵ�򻺴����Ѿ�������
	 */
	abstract AccessControlCacheHolder<TFacade, TImplement, TKeysHolder> findAccessControlHolder(
			GUID keyValue, Transaction transaction);

	final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine;
}