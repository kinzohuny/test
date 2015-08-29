package com.jiuqi.dna.core.impl;

/**
 * ÊÂ¼ş¼àÌıÆ÷Á´±í
 * 
 * @author gaojingxin
 */
final class EventListenerChain {

	EventListenerChain(EventListenerBase<?, ?, ?, ?, ?> eventListener,
			EventListenerChain next) {
		this.listener = eventListener;
		this.next = next;
	}

	EventListenerChain(EventListenerBase<?, ?, ?, ?, ?> eventListener) {
		this.listener = eventListener;
	}

	final EventListenerBase<?, ?, ?, ?, ?> listener;

	final int getChainSize() {
		int size = 0;
		EventListenerChain c = this;
		do {
			size++;
			c = c.next;
		} while (c != null);
		return size;
	}

	EventListenerChain next;

	final EventListenerChain putIn(
			EventListenerBase<?, ?, ?, ?, ?> listener) {
		final float ep = listener.priority;
		EventListenerBase<?, ?, ?, ?, ?> el = this.listener;
		if (el.priority > ep) {
			return new EventListenerChain(listener, this);
		} else if (el == listener) {
			return this;
		}
		EventListenerChain pre = this;
		EventListenerChain next = this.next;
		while (next != null) {
			el = next.listener;
			if (el == listener) {
				return this;
			}
			if (el.priority > ep) {
				break;
			}
			pre = next;
			next = next.next;
		}
		pre.next = new EventListenerChain(listener, next);
		return this;
	}
}