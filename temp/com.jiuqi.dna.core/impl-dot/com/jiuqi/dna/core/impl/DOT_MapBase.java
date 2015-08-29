package com.jiuqi.dna.core.impl;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.jiuqi.dna.core.type.DataObjectTranslator;

abstract class DOT_MapBase<TMap extends Map<?, ?>> implements
		DataObjectTranslator<TMap, Object[]> {
	final static short VERSION = 0x0100;

	public final boolean supportAssign() {
		return true;
	}

	@SuppressWarnings("unchecked")
	public final Object[] toDelegateObject(TMap map) {
		final int size = map.size();
		if (size > 0) {
			final Object[] objects = new Object[size * 2];
			final Set<Entry<?, ?>> entrySet = ((Map) map).entrySet();
			int index = 0;
			for (Entry<?, ?> ent : entrySet) {
				objects[index++] = ent.getKey();
				objects[index++] = ent.getValue();
			}
			return objects;
		} else {
			return Utils.emptyObjectArray;
		}
	}

	public short getVersion() {
		return VERSION;
	}

	protected abstract TMap newMap(int cap);

	private final boolean allowNullKey;
	private final boolean allowNullValue;

	DOT_MapBase(boolean allowNullKey, boolean allowNullValue) {
		this.allowNullKey = allowNullKey;
		this.allowNullValue = allowNullValue;
	}

	public final TMap resolveInstance(TMap destHint, Object[] objects,
			short version, boolean forSerial) {
		if (destHint != null) {
			destHint.clear();
			return destHint;
		} else {
			return this.newMap(objects.length / 2);
		}

	};

	@SuppressWarnings("unchecked")
	public final void recoverData(TMap dest, Object[] objects, short version,
			boolean forSerial) {
		final Map m = dest;
		for (int i = 0, c = objects.length; i < c; i += 2) {
			final Object key = objects[i];
			final Object value = objects[i + 1];
			if (key == null && value == null) {
				if (allowNullKey && allowNullValue) {
					m.put(key, value);
				} else if (allowNullKey) {
					Cache.printWarningMessage("反序列化：哈希表不支持插入null值，键为[" + value + "]");
				} else if (allowNullValue) {
					Cache.printWarningMessage("反序列化：哈希表不支持插入null键，值为[" + key + "]");
				}
			} else if (key == null) {
				if (allowNullKey) {
					m.put(key, value);
				} else {
					Cache.printWarningMessage("反序列化：哈希表不支持插入null键，值为[" + key + "]");
				}
			} else if (value == null) {
				if (allowNullValue) {
					m.put(key, value);
				} else {
					Cache.printWarningMessage("反序列化：哈希表不支持插入null值，键为[" + key + "]");
				}
			} else {
				m.put(key, value);
			}
		}
	};

	public final short supportedVerionMin() {
		return VERSION;
	}

}
