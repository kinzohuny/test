package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.service.Publish.Mode;

/**
 * 查找调用器的模式
 * 
 * @author gaojingxin
 * 
 */
enum InvokeeQueryMode {

	IN_SPACE {

		@Override
		final ServiceInvokeeBase<?, ?, ?, ?, ?> findInvokeeBase(
				ServiceInvokeeEntry entry, Class<?> key1Class,
				Class<?> key2Class, Class<?> key3Class, int mask) {
			for (ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, mask)) {
					return invokee;
				}
			}
			return null;
		}

		@Override
		final EventListenerChain collectEvent(ServiceInvokeeEntry entry,
				EventListenerChain chain, Object key1, Object key2, Object key3) {
			final Class<?> key1Class = key1 == null ? null : key1.getClass();
			final Class<?> key2Class = key2 == null ? null : key2.getClass();
			final Class<?> key3Class = key3 == null ? null : key3.getClass();
			for (ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, ServiceInvokeeBase.MASK_EVENT)) {
					final EventListenerBase<?, ?, ?, ?, ?> listener = (EventListenerBase<?, ?, ?, ?, ?>) invokee;
					if (listener.accept(key1, key2, key3)) {
						if (chain == null) {
							chain = new EventListenerChain(listener);
						} else {
							chain = chain.putIn(listener);
						}
					}
				}
			}
			return chain;
		}
	},

	IN_SITE {

		@Override
		final ServiceInvokeeBase<?, ?, ?, ?, ?> findInvokeeBase(
				ServiceInvokeeEntry entry, Class<?> key1Class,
				Class<?> key2Class, Class<?> key3Class, int mask) {
			for (ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, mask)) {
					return invokee;
				}
			}
			return null;
		}

		@Override
		final EventListenerChain collectEvent(ServiceInvokeeEntry entry,
				EventListenerChain chain, Object key1, Object key2, Object key3) {
			final Class<?> key1Class = key1 == null ? null : key1.getClass();
			final Class<?> key2Class = key2 == null ? null : key2.getClass();
			final Class<?> key3Class = key3 == null ? null : key3.getClass();
			for (ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, ServiceInvokeeBase.MASK_EVENT)) {
					final EventListenerBase<?, ?, ?, ?, ?> listener = (EventListenerBase<?, ?, ?, ?, ?>) invokee;
					if (listener.accept(key1, key2, key3)) {
						if (chain == null) {
							chain = new EventListenerChain(listener);
						} else {
							chain = chain.putIn(listener);
						}
					}
				}
			}
			return chain;
		}
	},

	FROM_SUB_SITE {

		@Override
		final ServiceInvokeeBase<?, ?, ?, ?, ?> findInvokeeBase(
				ServiceInvokeeEntry entry, Class<?> key1Class,
				Class<?> key2Class, Class<?> key3Class, int mask) {
			for (ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, mask) && (invokee.publishMode == Mode.SITE_PROTECTED || invokee.publishMode == Mode.SITE_PUBLIC)) {
					return invokee;
				}
			}
			return null;
		}

		@Override
		final EventListenerChain collectEvent(ServiceInvokeeEntry entry,
				EventListenerChain chain, Object key1, Object key2, Object key3) {
			final Class<?> key1Class = key1 == null ? null : key1.getClass();
			final Class<?> key2Class = key2 == null ? null : key2.getClass();
			final Class<?> key3Class = key3 == null ? null : key3.getClass();
			for (ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, ServiceInvokeeBase.MASK_EVENT) && (invokee.publishMode == Mode.SITE_PROTECTED || invokee.publishMode == Mode.SITE_PUBLIC)) {
					final EventListenerBase<?, ?, ?, ?, ?> listener = (EventListenerBase<?, ?, ?, ?, ?>) invokee;
					if (listener.accept(key1, key2, key3)) {
						if (chain == null) {
							chain = new EventListenerChain(listener);
						} else {
							chain = chain.putIn(listener);
						}
					}
				}
			}
			return chain;
		}
	},

	FROM_OTHER_SITE {

		@Override
		final ServiceInvokeeBase<?, ?, ?, ?, ?> findInvokeeBase(
				ServiceInvokeeEntry entry, Class<?> key1Class,
				Class<?> key2Class, Class<?> key3Class, int mask) {
			for (ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, mask) && invokee.publishMode == Mode.SITE_PUBLIC) {
					return invokee;
				}
			}
			return null;
		}

		@Override
		final EventListenerChain collectEvent(ServiceInvokeeEntry entry,
				EventListenerChain chain, Object key1, Object key2, Object key3) {
			final Class<?> key1Class = key1 == null ? null : key1.getClass();
			final Class<?> key2Class = key2 == null ? null : key2.getClass();
			final Class<?> key3Class = key3 == null ? null : key3.getClass();
			for (ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = entry.first; invokee != null; invokee = invokee.next) {
				if (invokee.match(key1Class, key2Class, key3Class, ServiceInvokeeBase.MASK_EVENT) && invokee.publishMode == Mode.SITE_PUBLIC) {
					final EventListenerBase<?, ?, ?, ?, ?> listener = (EventListenerBase<?, ?, ?, ?, ?>) invokee;
					if (listener.accept(key1, key2, key3)) {
						if (chain == null) {
							chain = new EventListenerChain(listener);
						} else {
							chain = chain.putIn(listener);
						}
					}
				}
			}
			return chain;
		}
	};
	/**
	 * 根据当前请求模式查找符合条件的调用器
	 * 
	 * @param entry
	 * @param key1Class
	 * @param key2Class
	 * @param key3Class
	 * @param mask
	 * @return
	 */
	abstract ServiceInvokeeBase<?, ?, ?, ?, ?> findInvokeeBase(
			ServiceInvokeeEntry entry, Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask);

	/**
	 * 根据当前请求模式收集符合条件的事件处理器
	 * 
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param mode
	 * @param collector
	 */
	abstract EventListenerChain collectEvent(ServiceInvokeeEntry entry,
			EventListenerChain chain, Object key1, Object key2, Object key3);
}