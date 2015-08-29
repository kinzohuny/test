package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.def.obja.StructField;

@StructClass
class RemoteQueryDataEx<TResult, TKey1, TKey2, TKey3> extends
		NetQueryRequestImpl.RemoteQueryData<TResult, TKey1, TKey2, TKey3>
		implements RSIPropertySet {
	@StructField
	private RSIProperties properties;

	@Override
	public final Object getProp(int key) {
		return this.properties != null ? this.properties.get(key) : null;
	}

	@Override
	public final Object setProp(int key, Object value) {
		if (this.properties != null) {
			return this.properties.set(key, value);
		} else if (value != null) {
			this.properties = new RSIProperties();
			return this.properties.set(key, value);
		} else {
			return null;
		}
	}

	public RemoteQueryDataEx(byte resultType, Class<TResult> resultClass,
			Operation<? super TResult> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] otherKeys) {
		super(resultType, resultClass, operation, key1, key2, key3, otherKeys);
	}

}
