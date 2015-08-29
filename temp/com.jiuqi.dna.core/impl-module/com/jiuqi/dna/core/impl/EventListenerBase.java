package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.TypeArgFinder;

abstract class EventListenerBase<TEvent, TContext, TKey1, TKey2, TKey3> extends
		ServiceInvokeeBase<TEvent, TContext, TKey1, TKey2, TKey3> {

	protected final float priority;
	protected final Class<?> eventClass;

	protected EventListenerBase(float priority) {
		this.priority = priority;
		this.eventClass = TypeArgFinder.get(this.getClass(), EventListenerBase.class, 0);
	}

	@Override
	protected abstract void occur(TContext context, TEvent event)
			throws Throwable;

	abstract boolean accept(Object key1, Object key2, Object key3);

	@Override
	final Class<?> getTargetClass() {
		return this.eventClass;
	}
}