package com.jiuqi.dna.core.internal.common;

public final class TwoKeyHashMap<F, S, V> {

	public final V find(F f, S s) {
		return this.get0(f, s, false);
	}

	public final V get(F f, S s) {
		return this.get0(f, s, true);
	}

	public final V put(F f, S s, V v) {
		return this.put0(f, s, v, false);
	}

	public final void putForce(F f, S s, V v) {
		this.put0(f, s, v, true);
	}

	public final V remove(F f, S s) {
		return this.remove0(f, s, false);
	}

	public final V removeForce(F f, S s) {
		return this.remove0(f, s, true);
	}

	public final int size() {
		return this.size;
	}

	public final boolean isEmpty() {
		return this.size == 0;
	}

	private final float loadFactor = 0.75f;
	private Entry<F, S, V>[] table;
	private int size;

	private final int hash(F f, S s) {
		return f.hashCode() ^ s.hashCode();
	}

	private final int indexFor(int hash, int length) {
		return hash & (length - 1);
	}

	private final void checkKey(F f, S s) {
		if (f == null || s == null) {
			throw new IllegalArgumentException();
		}
	}

	private final V get0(F f, S s, boolean force) {
		this.checkKey(f, s);
		final Entry<F, S, V>[] table = this.table;
		if (table == null) {
			if (force) {
				throw new IllegalArgumentException();
			} else {
				return null;
			}
		}
		final int hash = this.hash(f, s);
		final int index = this.indexFor(hash, table.length);
		for (Entry<F, S, V> e = table[index]; e != null; e = e.next) {
			if (hash == e.hash && e.equals(f, s)) {
				return e.value;
			}
		}
		if (force) {
			throw new IllegalArgumentException();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private final V put0(F f, S s, V v, boolean force) {
		this.checkKey(f, s);
		final int hash = this.hash(f, s);
		Entry<F, S, V>[] table = this.table;
		if (table == null) {
			table = new Entry[4];
			table[this.indexFor(hash, table.length)] = new Entry<F, S, V>(hash, f, s, v);
			this.size = 1;
			this.table = table;
			return null;
		}
		final int index = this.indexFor(hash, table.length);
		for (Entry<F, S, V> e = table[index]; e != null; e = e.next) {
			if (hash == e.hash && e.equals(f, s)) {
				if (force) {
					throw new IllegalArgumentException();
				} else {
					V old = e.value;
					e.value = v;
					return old;
				}
			}
		}
		Entry<F, S, V> entry = new Entry<F, S, V>(hash, f, s, v, table[index]);
		table[index] = entry;
		if (++this.size > table.length * this.loadFactor) {
			final int nlength = table.length << 1;
			final Entry<F, S, V>[] ntable = new Entry[nlength];
			for (Entry<F, S, V> e : table) {
				while (e != null) {
					final Entry<F, S, V> next = e.next;
					final int nindex = this.indexFor(e.hash, nlength);
					e.next = ntable[nindex];
					ntable[nindex] = e;
					e = next;
				}
			}
			this.table = ntable;
		}
		return null;
	}

	private final V remove0(F f, S s, boolean force) {
		this.checkKey(f, s);
		final Entry<F, S, V>[] table = this.table;
		if (table == null) {
			if (force) {
				throw new IllegalArgumentException();
			} else {
				return null;
			}
		}
		final int hash = this.hash(f, s);
		final int index = this.indexFor(hash, table.length);
		for (Entry<F, S, V> e = table[index], prev = e; e != null; prev = e, e = e.next) {
			if (hash == e.hash && e.equals(f, s)) {
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
		if (force) {
			throw new IllegalArgumentException();
		}
		return null;
	}

	public static final class Entry<F, S, V> {

		public final int hash;
		public final F f;
		public final S s;
		private V value;
		Entry<F, S, V> next;

		Entry(int hash, F f, S s, V v) {
			this.hash = hash;
			this.f = f;
			this.s = s;
			this.value = v;
		}

		Entry(int hash, F f, S s, V v, Entry<F, S, V> next) {
			this.hash = hash;
			this.f = f;
			this.s = s;
			this.value = v;
			this.next = next;
		}

		final boolean equals(F f, S s) {
			return this.f == f && this.s == s || this.f.equals(f) && this.s.equals(s);
		}
	}
}