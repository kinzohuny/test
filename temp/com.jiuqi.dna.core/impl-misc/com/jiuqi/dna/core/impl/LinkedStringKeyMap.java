package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.Iterator;

public final class LinkedStringKeyMap<E> extends StringKeyMap<E> implements
		Iterable<E> {

	private final ArrayList<E> list;

	public LinkedStringKeyMap(boolean caseSensitive) {
		this(caseSensitive, DEFAULT_CAPACITY);
	}

	public LinkedStringKeyMap(boolean caseSensitive, int initialCapacity) {
		super(caseSensitive, initialCapacity);
		this.list = new ArrayList<E>(initialCapacity);
	}

	@Override
	public void clear() {
		super.clear();
		this.list.clear();
	}

	@Override
	public E put(String key, E e) {
		return this.put(key, e, false);
	}

	@Override
	public E put(String key, E e, boolean mandatory) {
		E ov = super.put(key, e, mandatory);
		if (ov != null && !this.list.remove(ov)) {
			// exist in map, not exist in list.
			throw new IllegalStateException();
		}
		this.list.add(e);
		return ov;
	}

	public E get(int index) {
		return this.list.get(index);
	}

	@Override
	public E remove(String key) {
		return this.remove(key, false);
	}

	@Override
	public E remove(String key, boolean mandatory) {
		E ov = super.remove(key, mandatory);
		if (ov != null && !this.list.remove(ov)) {
			throw new IllegalStateException();
		}
		return ov;
	}

	public Iterator<E> iterator() {
		return this.list.iterator();
	}
}