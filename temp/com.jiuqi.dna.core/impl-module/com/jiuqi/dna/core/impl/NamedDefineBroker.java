package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.service.Publish;

final class NamedDefineBroker<TDefine extends NamedDefineImpl> extends
		ServiceInvokeeBase<TDefine, Context, String, None, None> {

	private static class Entry {
		final int hash;
		NamedDefineImpl define;
		Entry next;

		Entry(int hash, NamedDefineImpl define, Entry next) {
			this.hash = hash;
			this.define = define;
			this.next = next;
		}
	}

	final synchronized void fetchAll(List<NamedDefineImpl> fetchInto) {
		for (Entry e : this.table) {
			while (e != null) {
				fetchInto.add(e.define);
				e = e.next;
			}
		}
	}

	private Entry[] table;
	private int size;
	private final Space space;
	private final Class<?> defineIntfClass;
	private final boolean caseSensitive;

	NamedDefineBroker(Class<?> defineIntfClass, Space space,
			NamedDefineImpl define) {
		if (defineIntfClass == null || space == null) {
			throw new NullPointerException();
		}
		this.publishMode = Publish.Mode.PROTECTED;
		this.defineIntfClass = defineIntfClass;
		this.space = space;
		this.caseSensitive = define.isNameCaseSensitive();
		this.table = new Entry[4];
		this.putDefine(define);
	}

	@SuppressWarnings("unchecked")
	@Override
	final NamedDefineBroker<TDefine> upperMatchBroker() {
		Space space = this.space;
		if (space.site == space) {
			return null;// 到达站点了
		}
		return (NamedDefineBroker<TDefine>) space.space.findInvokeeBase(this.defineIntfClass, String.class, null, null, MASK_DEFINE, InvokeeQueryMode.IN_SITE);
	}

	@Override
	final ServiceBase<?> getService() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	final TDefine provide(Context context, String key1) throws Throwable {
		return (TDefine) this.findDefine(key1);
	}

	@Override
	final Space getSpace() {
		return this.space;
	}

	private final int hash(String name) {
		return this.caseSensitive ? name.hashCode() : name.toUpperCase().hashCode();
	}

	private final boolean equals(Entry e, String name) {
		return e.define.name == name || this.caseSensitive ? e.define.name.equals(name) : e.define.name.equalsIgnoreCase(name);
	}

	final synchronized NamedDefineImpl putDefine(NamedDefineImpl define) {
		final int hash = this.hash(define.name);
		Entry[] table = this.table;
		final int length = table.length;
		int index = hash & (length - 1);
		for (Entry e = this.table[index]; e != null; e = e.next) {
			if (e.hash == hash && this.equals(e, define.name)) {
				NamedDefineImpl old = e.define;
				e.define = define;
				return old;
			}
		}
		if (++this.size > length * 0.75) {
			final int newLen = length * 2;
			final int newH = newLen - 1;
			final Entry[] newTable = new Entry[newLen];
			for (int j = 0; j < length; j++) {
				for (Entry e = this.table[j], next; e != null; e = next) {
					final int i = e.hash & newH;
					next = e.next;
					e.next = newTable[i];
					newTable[i] = e;
				}
			}
			this.table = table = newTable;
			index = hash & (newLen - 1);
		}
		this.table[index] = new Entry(hash, define, this.table[index]);
		return null;
	}

	final synchronized NamedDefineImpl removeDefine(String name) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		int hash = this.hash(name);
		final Entry[] table = this.table;
		final int index = hash & (table.length - 1);
		for (Entry e = table[index], last = null; e != null; last = e, e = e.next) {
			if (e.hash == hash && this.equals(e, name)) {
				NamedDefineImpl old = e.define;
				if (last == null) {
					table[index] = e.next;
				} else {
					last.next = e.next;
				}
				e.next = null;// HELP GC
				return old;
			}
		}
		return null;
	}

	final synchronized NamedDefineImpl findDefine(String name) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		final Entry[] table = this.table;
		final int hash = this.hash(name);
		for (Entry e = table[hash & (table.length - 1)]; e != null; e = e.next) {
			if (e.hash == hash && this.equals(e, name)) {
				return e.define;
			}
		}
		return null;
	}

	final synchronized void fetchDefines(List<NamedDefineImpl> fetchInto) {
		for (Entry e : this.table) {
			while (e != null) {
				fetchInto.add(e.define);
				e = e.next;
			}
		}
	}

	@Override
	final void handle(Context context, TDefine task) throws Throwable {
		this.putDefine(task);
	}

	@Override
	final Class<?> getTargetClass() {
		return this.defineIntfClass;
	}

	@Override
	final boolean match(Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask) {
		return mask == MASK_DEFINE && key1Class == String.class && (key2Class == null);
	}
}
