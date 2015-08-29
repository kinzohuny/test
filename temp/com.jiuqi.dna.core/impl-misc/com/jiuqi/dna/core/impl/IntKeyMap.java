/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File AbstractMap.java
 * Date 2009-5-11
 */
package com.jiuqi.dna.core.impl;

import java.util.ConcurrentModificationException;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * 用整数作为键值的哈希表。
 * 
 * @author LRJ
 * @version 1.0
 */
public class IntKeyMap<TValue> {
	private static final float loadFactor = 2.0f;

	transient volatile int modCount;

	protected int modCount() {
		return this.modCount;
	}

	public int size() {
		return this.size;
	}

	public static class IntMapEntry<TValue> {
		public final int key;
		private TValue value;

		private IntMapEntry<TValue> next;

		private IntMapEntry(int key, TValue value, IntMapEntry<TValue> next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		public TValue getValue() {
			return this.value;
		}

		@Override
		public String toString() {
			return this.key + "=" + this.value;
		}
	}

	private volatile IntMapEntry<TValue>[] entries;
	private volatile int size;

	public boolean isEmpty() {
		return (this.size == 0);
	}

	public void clear() {
		this.modCount++;
		this.size = 0;
		this.entries = null;
	}

	public TValue get(int key) {
		if (this.size > 0) {
			int index = UtilHelper.indexForIntKey(key, this.entries.length);
			IntMapEntry<TValue> e = this.entries[index];
			while (e != null) {
				if (e.key == key) {
					return e.value;
				}
				e = e.next;
			}
		}
		return null;
	}

	public TValue put(int key, TValue value) {
		this.ensureCapacity();
		TValue old;
		int index = UtilHelper.indexForIntKey(key, this.entries.length);
		IntMapEntry<TValue> e = this.entries[index];
		while (e != null) {
			if (e.key == key) {
				old = e.value;
				e.value = value;
				return old;
			}
			e = e.next;
		}

		e = new IntMapEntry<TValue>(key, value, this.entries[index]);
		this.modCount++;
		this.entries[index] = e;
		this.size++;
		return null;
	}

	@SuppressWarnings("unchecked")
	private void ensureCapacity() {
		if (this.entries == null) {
			this.entries = new IntMapEntry[4];
			return;
		}
		if (this.size >= this.entries.length * loadFactor) {
			this.modCount++;
			final int newSize = this.entries.length << 1;
			IntMapEntry<TValue>[] newSpine = new IntMapEntry[newSize];
			IntMapEntry<TValue> e, temp;
			int newIndex;
			for (int i = 0, len = this.entries.length; i < len; i++) {
				e = this.entries[i];
				while (e != null) {
					temp = e.next;
					newIndex = UtilHelper.indexForIntKey(e.key, newSize);
					e.next = newSpine[newIndex];
					newSpine[newIndex] = e;
					e = temp;
				}
			}
			this.entries = newSpine;
		}
	}

	public TValue remove(int key) {
		TValue removed = null;
		if (this.size > 0) {
			int index = UtilHelper.indexForIntKey(key, this.entries.length);
			IntMapEntry<TValue> e = this.entries[index], last = null;
			while (e != null) {
				if (e.key == key) {
					this.modCount++;
					if (last == null) {
						this.entries[index] = e.next;
					} else {
						last.next = e.next;
					}
					this.size--;
					removed = e.value;
					break;
				}
				last = e;
				e = e.next;
			}
		}
		// if (entries != null && this.size < entries.length / loadFactor) {
		// this.trim();
		// }
		return removed;
	}

	@SuppressWarnings("unchecked")
	public IntKeyMap<TValue> copy() {
		IntKeyMap<TValue> newMap = new IntKeyMap<TValue>();
		newMap.size = this.size;
		if (this.entries != null) {
			newMap.entries = new IntMapEntry[this.entries.length];
			for (int i = 0, c = this.entries.length; i < c; i++) {
				IntMapEntry<TValue> entry = this.entries[i];
				if (entry != null) {
					IntMapEntry<TValue> newEntry = newMap.entries[i] = new IntMapEntry<TValue>(entry.key, entry.value, null);
					for (entry = entry.next; entry != null; entry = entry.next) {
						newEntry.next = new IntMapEntry<TValue>(entry.key, entry.value, null);
						newEntry = newEntry.next;
					}
				}
			}
		}
		return newMap;
	}

	// /////////////////////////////////////////////////////////////////////

	public void visitAll(ValueVisitor<TValue> visitor) {
		if (visitor == null) {
			throw new NullArgumentException("visitor");
		}
		if (this.size > 0) {
			final int expectedModCount = this.modCount;
			IntMapEntry<TValue> entry;
			for (int i = 0, len = this.entries.length; i < len; i++) {
				if (expectedModCount != this.modCount) {
					throw new ConcurrentModificationException();
				}
				entry = this.entries[i];
				while (entry != null) {
					visitor.visit(entry.key, entry.value);
					if (expectedModCount != this.modCount) {
						throw new ConcurrentModificationException();
					}
					entry = entry.next;
				}
			}
		}
	}

	/**
	 * 遍历所有项，对每一项先用visitor访问，再用toRemove过滤，删除被过滤出来的项。
	 * 
	 * @param visitor
	 * @param toRemove
	 */
	public void removeAndVisit(ValueVisitor<TValue> visitor,
			ValueVisitorFilter<TValue> toRemove) {
		if (visitor == null) {
			throw new NullArgumentException("visitor");
		}
		if (toRemove == null) {
			throw new NullArgumentException("filter");
		}
		if (this.size > 0) {
			final int expectedModCount = this.modCount;
			IntMapEntry<TValue> entry;
			for (int i = 0, len = this.entries.length; i < len; i++) {
				if (expectedModCount != this.modCount) {
					throw new ConcurrentModificationException();
				}
				entry = this.entries[i];
				IntMapEntry<TValue> last = null;
				while (entry != null) {
					if (toRemove.canVisit(entry.key, entry.value)) {
						// 先删除再调用visitor.visit
						if (last == null) {
							this.entries[i] = entry.next;
						} else {
							last.next = entry.next;
						}
						this.size--;
						visitor.visit(entry.key, entry.value);
						if (expectedModCount != this.modCount) {
							throw new ConcurrentModificationException();
						}
					} else {
						last = entry;
					}
					entry = entry.next;
				}
			}
			this.modCount++;
		}
	}

	// ////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder map = new StringBuilder("{");
		if (this.size > 0) {
			final int expectedModCount = this.modCount;
			IntMapEntry<TValue> entry;
			for (int i = 0, len = this.entries.length; i < len; i++) {
				if (expectedModCount != this.modCount) {
					throw new ConcurrentModificationException();
				}
				entry = this.entries[i];
				while (entry != null) {
					map.append(entry.key);
					map.append('=');
					map.append(entry.value == this ? "(this map)" : entry.value);
					map.append(", ");
					if (expectedModCount != this.modCount) {
						throw new ConcurrentModificationException();
					}
					entry = entry.next;
				}
			}
			if (map.length() >= 3) {
				map.deleteCharAt(map.length() - 1);
				map.deleteCharAt(map.length() - 1);
			}
		}
		map.append("}");
		return map.toString();
	}
}
