package com.jiuqi.dna.core.misc;

import java.util.Iterator;
/**
 * �յĿɵ�����
 * @author gaojingxin
 * 
 */
public final class EmptyIterable implements Iterable<Object> {
	public Iterator<Object> iterator() {
		return EmptyIterator.emptyIterator;
	}
	private EmptyIterable() {
	}
	public static final Iterable<Object> emptyIterable = new EmptyIterable();
	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> emptyIterable(Class<T> clazz) {
		return (Iterable<T>) emptyIterable;
	}
}