package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * ��������������
 * 
 * @author gaojingxin
 * 
 * @param <TRef>
 */
class RefStartupEntry<TRef> extends StartupEntry {
	final TRef ref;

	RefStartupEntry(TRef ref) {
		if (ref == null) {
			throw new NullArgumentException("ref");
		}
		this.ref = ref;
	}
}
