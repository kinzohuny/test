/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File Linkable.java
 * Date 2009-3-10
 */
package com.jiuqi.dna.core.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
public interface Linkable<T extends Linkable<T>> {

	T next();

	void setNext(T next);

	public static interface Helper {

		<E extends Linkable<E>> Iterator<E> iterate(E head);
	}

	public static final Helper helper = new Helper() {

		public <E extends Linkable<E>> Iterator<E> iterate(final E head) {
			return new Iterator<E>() {
				private E next = head;

				public boolean hasNext() {
					return this.next != null;
				}

				public E next() {
					E current = this.next;
					if (current == null) {
						throw new NoSuchElementException();
					}
					this.next = current.next();
					return current;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

	};

}
