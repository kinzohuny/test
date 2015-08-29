package com.jiuqi.dna.core.internal.db.sync;

import java.util.HashSet;

public final class DbNamespace {

	final boolean caseSensitive;

	final HashSet<String> names = new HashSet<String>();

	public DbNamespace(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	private final String trans(String name) {
		return this.caseSensitive ? name : name.toLowerCase();
	}

	public final boolean contains(String name) {
		return this.names.contains(this.trans(name));
	}

	public final void add(String name) {
		this.names.add(this.trans(name));
	}

	public final void remove(String name) {
		this.names.remove(this.trans(name));
	}

	public final void clear() {
		this.names.clear();
	}
}