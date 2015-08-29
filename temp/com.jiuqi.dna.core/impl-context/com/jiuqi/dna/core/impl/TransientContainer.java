package com.jiuqi.dna.core.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * 自销毁对象容器
 * 
 * @author gaojingxin
 * 
 */
final class TransientContainer extends ReferenceQueue<TransientProxy<?>> {
	/**
	 * 自动销毁对象的基础类
	 * 
	 * @author gaojingxin
	 * 
	 * @param <THandle>
	 */
	static abstract class TransientProvider extends
			WeakReference<TransientProxy<?>> {

		/**
		 * 回收
		 */
		abstract void unuse();

		/**
		 * 返回所有者
		 */
		abstract Object getOwner();

		/**
		 * 所处深度
		 */
		final short depth;
		/**
		 * 所在容器
		 */
		final TransientContainer container;

		final void transientInactive() {
			this.container.deepest = this.remove(this.container.deepest);
		}

		final void transientActive() {
			final TransientProvider deepest = this.container.deepest;
			if (deepest == null) {
				this.container.deepest = this;
			} else {
				this.container.deepest = deepest.putInDeepest(this);
			}
		}

		TransientProvider(TransientProxy<?> proxy,
				TransientContainer container, short depth) {
			super(proxy, container);
			this.depth = depth;
			this.container = container;
			container.getDeepest();
		}

		TransientProvider(TransientProxy<?> proxy, Transaction transaction) {
			this(proxy, transaction.getTransientContainer(), transaction.getCurrentContext().getDepth());
		}

		/**
		 * 深度低的
		 */
		private TransientProvider upper;
		/**
		 * 深度深的
		 */
		private TransientProvider deeper;

		private TransientProvider remove(TransientProvider deepest) {
			final TransientProvider deeper = this.deeper;
			final TransientProvider upper = this.upper;
			if (deeper != null) {
				deeper.upper = upper;
				this.deeper = null;
			}
			if (upper != null) {
				upper.deeper = deeper;
				this.upper = null;
			}
			if (this == deepest) {
				return upper;
			}
			return deepest;
		}

		private TransientProvider releaseByOwner(Object owner) {
			TransientProvider deepest = this;
			TransientProvider one = deepest;
			do {
				TransientProvider upper = one.upper;
				if (one.getOwner() == owner) {
					deepest = one.remove(deepest);
					try {
						this.unuse();
					} catch (Throwable e) {
						// ignore
					}
				}
				one = upper;
			} while (one != null);
			return deepest;
		}

		private TransientProvider release(TransientProvider deepest) {
			deepest = this.remove(deepest);
			try {
				this.unuse();
			} catch (Throwable e) {
				// ignore
			}
			return deepest;
		}

		private TransientProvider putInDeepest(TransientProvider provider) {
			final int newDepth = provider.depth;
			if (newDepth >= this.depth) {
				provider.upper = this;
				this.deeper = provider;
				return provider;
			}
			TransientProvider de = this;
			TransientProvider upper = de.upper;
			while (upper != null && newDepth < upper.depth) {
				de = upper;
				upper = upper.upper;
			}
			provider.upper = upper;
			provider.deeper = de;
			de.upper = provider;
			if (upper != null) {
				upper.deeper = provider;
			}
			return this;
		}

	}

	private TransientProvider deepest;

	private final TransientProvider getDeepest() {
		TransientProvider deepest = this.deepest;
		for (;;) {
			final TransientProvider provider = (TransientProvider) this.poll();
			if (provider == null) {
				break;
			}
			this.deepest = deepest = provider.remove(deepest);
			try {
				provider.unuse();
			} catch (Throwable e) {
				// ignore
			}
		}
		return deepest;
	}

	final void leaveFrame(int depth) {
		TransientProvider deepest = this.getDeepest();
		while (deepest != null && deepest.depth >= depth) {
			deepest = deepest.release(deepest);
		}
		this.deepest = deepest;
	}

	final void clear(Object owner) {
		final TransientProvider deepest = this.getDeepest();
		if (deepest != null) {
			this.deepest = deepest.releaseByOwner(owner);
		}
	}
}
