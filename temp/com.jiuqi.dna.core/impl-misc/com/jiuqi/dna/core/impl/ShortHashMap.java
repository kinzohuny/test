package com.jiuqi.dna.core.impl;

/**
 * 用短整型作为键值的哈希表。
 * 
 * @author BO KANG
 * @version 1.0
 */
public class ShortHashMap<T> {

	static class Entry {
		final short key;
		Object value;
		Entry next;

		Entry(short key, Object value, Entry next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}
	}
}
