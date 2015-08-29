package com.jiuqi.dna.core.internal.db.monitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.jiuqi.dna.core.db.monitor.Variation;
import com.jiuqi.dna.core.db.monitor.VariationSet;
import com.jiuqi.dna.core.db.monitor.VariationVersion;
import com.jiuqi.dna.core.misc.Boundary;

final class VariationSetImpl implements VariationSet {

	final VariationControl loader;

	VariationSetImpl(VariationControl loader) {
		this.loader = loader;
	}

	public final Iterator<Variation> iterator() {
		return new Itr();
	}

	public final int size() {
		return this.list.size();
	}

	public final Variation get(int index) {
		return this.list.get(index);
	}

	final void add(VariationImpl variation) {
		this.list.add(variation);
		final VariationVersion version = variation.version;
		if (this.lower == null || this.lower.compareTo(version) > 0) {
			this.lower = version;
		}
		if (this.upper == null || this.upper.compareTo(version) < 0) {
			this.upper = version;
		}
	}

	private VariationVersion lower;
	private VariationVersion upper;

	private final ArrayList<VariationImpl> list = new ArrayList<VariationImpl>();

	private final class Itr implements Iterator<Variation> {

		private int next = (VariationSetImpl.this.size() == 0 ? -1 : 0);

		public boolean hasNext() {
			return this.next >= 0;
		}

		// no concurrent check

		public Variation next() {
			if (this.next < 0 || this.next >= VariationSetImpl.this.size()) {
				throw new NoSuchElementException();
			}
			Variation get = VariationSetImpl.this.get(this.next);
			int next = this.next + 1;
			if (next < VariationSetImpl.this.size()) {
				this.next = next;
			} else {
				this.next = -1;
			}
			return get;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public final VariationVersion lower() {
		return this.lower;
	}

	public final VariationVersion upper() {
		return this.upper;
	}

	public final VariationSet subset(Boundary<VariationVersion> lower,
			Boundary<VariationVersion> upper) {
		if (lower == null && upper == null) {
			throw new IllegalArgumentException();
		}
		final VariationSetImpl subset = new VariationSetImpl(this.loader);
		if (upper == null) {
			for (VariationImpl var : this.list) {
				int compare = var.version.compareTo(lower.value);
				if (compare > 0 || compare == 0 && lower.include) {
					subset.add(var);
				}
			}
		} else if (lower == null) {
			for (VariationImpl var : this.list) {
				int compare = var.version.compareTo(upper.value);
				if (compare < 0 || compare == 0 && upper.include) {
					subset.add(var);
				}
			}
		} else {
			for (VariationImpl var : this.list) {
				int compareLower = var.version.compareTo(lower.value);
				int compareUpper = var.version.compareTo(upper.value);
				if ((compareLower > 0 || compareLower == 0 && lower.include)
						&& (compareUpper < 0 || compareUpper == 0 && upper.include)) {
					subset.add(var);
				}
			}
		}
		return subset;
	}
}