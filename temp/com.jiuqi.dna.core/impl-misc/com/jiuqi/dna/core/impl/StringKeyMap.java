package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.exception.NamedDefineExistingException;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * @author houchunlei
 * 
 * @param <E>
 */
public class StringKeyMap<E> {

	public E find(String key) {
		return this.get(key, false);
	}

	public E get(String key) {
		return this.get(key, true);
	}

	public E get(String key, boolean mandatory) {
		checkKey0(key);
		final Entry<E>[] table = this.table;
		if (table == null) {
			if (mandatory) {
				throw missing(key);
			} else {
				return null;
			}
		}
		if (!this.caseSensitive) {
			key = key.toUpperCase();
		}
		final int hash = key.hashCode();
		final int index = hash & (this.table.length - 1);
		for (Entry<E> e = table[index]; e != null; e = e.next) {
			if (hash == e.hash && (key == e.key || key.equals(e.key))) {
				return e.value;
			}
		}
		if (mandatory) {
			throw missing(key);
		}
		return null;
	}

	public E put(String key, E value) {
		return this.put(key, value, false);
	}

	@SuppressWarnings("unchecked")
	public E put(String key, E value, boolean mandatory) {
		checkKey0(key);
		Entry<E>[] table = this.table;
		if (table == null) {
			table = new Entry[DEFAULT_CAPACITY];
			table[key.hashCode() & (DEFAULT_CAPACITY - 1)] = new Entry<E>(key, value);
			this.table = table;
			this.size = 1;
			return null;
		}
		if (!this.caseSensitive) {
			key = key.toUpperCase();
		}
		final int hash = key.hashCode();
		final int index = hash & (table.length - 1);
		for (Entry<E> e = table[index]; e != null; e = e.next) {
			if (hash == e.hash && (key == e.key || key.equals(e.key))) {
				if (mandatory) {
					throw existing(key);
				} else {
					E old = e.value;
					e.value = value;
					return old;
				}
			}
		}
		Entry<E> entry = new Entry<E>(key, value, table[index]);
		table[index] = entry;
		if (++this.size > table.length * loadfactor) {
			final int newLength = table.length << 1;
			final int newMask = newLength - 1;
			final Entry<E>[] newTable = new Entry[newLength];
			for (Entry<E> e : table) {
				while (e != null) {
					final Entry<E> next = e.next;
					final int newIndex = e.key.hashCode() & newMask;
					e.next = newTable[newIndex];
					newTable[newIndex] = e;
					e = next;
				}
			}
			this.table = newTable;
		}
		return null;
	}

	public E remove(String key) {
		return this.remove(key, false);
	}

	public E remove(String key, boolean mandatory) {
		checkKey0(key);
		final Entry<E>[] table = this.table;
		if (table == null) {
			if (mandatory) {
				throw missing(key);
			} else {
				return null;
			}
		}
		if (!this.caseSensitive) {
			key = key.toUpperCase();
		}
		final int hash = key.hashCode();
		final int index = hash & (table.length - 1);
		for (Entry<E> e = table[index], prev = e; e != null; prev = e, e = e.next) {
			if (hash == e.hash && (key == e.key || key.equals(e.key))) {
				if (prev == e) {
					table[index] = e.next;
				} else {
					prev.next = e.next;
				}
				e.next = null;
				this.size--;
				return e.value;
			}
		}
		if (mandatory) {
			throw missing(key);
		}
		return null;
	}

	public boolean containsKey(String key) {
		checkKey0(key);
		final Entry<E>[] table = this.table;
		if (table == null) {
			return false;
		}
		if (!this.caseSensitive) {
			key = key.toUpperCase();
		}
		final int hash = key.hashCode();
		final int index = hash & (table.length - 1);
		for (Entry<E> e = table[index]; e != null; e = e.next) {
			if (hash == e.hash && (key == e.key || key.equals(e.key))) {
				return true;
			}
		}
		return false;
	}

	public boolean containsValue(E value) {
		final Entry<E>[] table = this.table;
		if (table == null) {
			return false;
		}
		for (int i = 0, c = table.length; i < c; i++) {
			for (Entry<E> e = table[i]; e != null; e = e.next) {
				if (e.value == null) {
					if (value == null) {
						return true;
					} else {
						continue;
					}
				} else if (e.value == value || e.value.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	public void checkKey(String key) {
		if (this.containsKey(key)) {
			throw existing(key);
		}
	}

	public void clear() {
		final Entry<E>[] table = this.table;
		if (table == null) {
			return;
		}
		for (int i = 0; i < table.length; i++) {
			table[i] = null;
		}
		this.size = 0;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public int size() {
		return this.size;
	}

	public static interface Visitor<T> {

		public void doVisit(String key, T value);
	}

	public final void visitAll(Visitor<E> visitor) {
		if (visitor == null) {
			throw new NullArgumentException("访问器");
		}
		final Entry<E>[] table = this.table;
		if (table == null) {
			return;
		}
		for (int i = 0; i < table.length; i++) {
			Entry<E> e = table[i];
			while (e != null) {
				visitor.doVisit(e.key, e.value);
				e = e.next;
			}
		}
	}

	public static final class Entry<TValue> {

		final String key;

		final int hash;

		TValue value;

		Entry<TValue> next;

		Entry(String key, TValue value) {
			this.key = key;
			this.hash = key.hashCode();
			this.value = value;
		}

		Entry(String key, TValue value, Entry<TValue> next) {
			this.key = key;
			this.hash = key.hashCode();
			this.value = value;
			this.next = next;
		}

		@Override
		public final String toString() {
			return this.key.toString() + ":" + (this.value != null ? this.value.toString() : "");
		}
	}

	private static final void checkKey0(String key) {
		if (key == null || key.length() == 0) {
			throw new NullArgumentException("键");
		}
	}

	private static final float loadfactor = 0.75f;

	private static final int MAXIMUM_CAPACITY = 1 << 30;

	private Entry<E>[] table;

	private int size;

	private static final NamedDefineExistingException existing(String name) {
		return new NamedDefineExistingException("名称为[" + name + "]的元素已经存在。");
	}

	private static final MissingDefineException missing(String name) {
		return new MissingDefineException("名称为[" + name + "]的元素不存在。");
	}

	public final boolean caseSensitive;

	protected static final int DEFAULT_CAPACITY = 4;

	public StringKeyMap(boolean caseSensitive) {
		this(caseSensitive, DEFAULT_CAPACITY);
	}

	@SuppressWarnings("unchecked")
	public StringKeyMap(boolean caseSensitive, int initialCapacity) {
		if (initialCapacity > MAXIMUM_CAPACITY) {
			initialCapacity = MAXIMUM_CAPACITY;
			this.table = new Entry[initialCapacity];
		} else {
			int capacity = 1;
			while (capacity < initialCapacity) {
				capacity <<= 1;
			}
			this.table = new Entry[capacity];
		}
		this.caseSensitive = caseSensitive;
	}
}