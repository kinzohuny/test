package com.jiuqi.dna.core.impl;

/**
 * 请求者保存者
 * 
 * @author LRJ
 * 
 */
abstract class AcquirerHolder {

	protected Acquirer[] acquirers;
	protected int size;

	abstract int getNodeIndex();

	protected final Acquirer getAcquirer(Acquirable acquirable) {
		if (this.size > 0) {
			final Acquirer[] acquirers = this.acquirers;
			final int index = acquirable.hashCode() & (acquirers.length - 1);
			for (Acquirer a = acquirers[index]; a != null; a = a.nextInHolder) {
				if (acquirable == a.acquirable) {
					return a;
				}
			}
		}
		return null;
	}

	protected final void removeAcquirer(Acquirer acquirer) {
		if (this.size > 0) {
			final Acquirer[] acquirers = this.acquirers;
			final int index = acquirer.acquirable.hashCode() & (acquirers.length - 1);
			for (Acquirer a = acquirers[index], last = null; a != null; last = a, a = a.nextInHolder) {
				if (acquirer == a) {
					if (last == null) {
						acquirers[index] = a.nextInHolder;
					} else {
						last.nextInHolder = a.nextInHolder;
					}
					a.nextInHolder = null;
					this.size--;
					return;
				}
			}
		}
	}

	private final Acquirer[] newArray(int length) {
		return new Acquirer[length];
	}

	protected final void putAcquirer(Acquirer acquirer) {
		int high;
		Acquirer[] acquirers = this.acquirers;
		if (acquirers == null) {
			this.acquirers = acquirers = this.newArray(4);
			high = 3;
		} else {
			final int oldL = acquirers.length;
			if (this.size >= oldL) {
				final int newSize = oldL << 1;
				high = newSize - 1;
				Acquirer[] newSpine = this.newArray(newSize);
				Acquirer a, next;
				int newIndex;
				for (int i = 0; i < oldL; i++) {
					for (a = acquirers[i]; a != null;) {
						next = a.nextInHolder;
						newIndex = a.acquirable.hashCode() & high;
						a.nextInHolder = newSpine[newIndex];
						newSpine[newIndex] = a;
						a = next;
					}
				}
				this.acquirers = acquirers = newSpine;
			} else {
				high = oldL - 1;
			}
		}
		final int index = acquirer.acquirable.hashCode() & high;
		for (Acquirer a = acquirers[index]; a != null; a = a.nextInHolder) {
			if (acquirer == a) {
				return;
			}
		}
		acquirer.nextInHolder = acquirers[index];
		acquirers[index] = acquirer;
		this.size++;
	}

	protected final void clearAcquirers() {
		Acquirer[] acquirers = this.acquirers;
		if (acquirers != null) {
			for (int i = 0, c = acquirers.length; i < c; i++) {
				acquirers[i] = null;
			}
			this.size = 0;
		}
	}
}