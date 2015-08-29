package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.db.monitor.Variation;
import com.jiuqi.dna.core.db.monitor.VariationContext;
import com.jiuqi.dna.core.db.monitor.VariationVersion;

final class VariationContextImpl implements VariationContext {

	final DBAdapter context;
	final VariationControl control;

	VariationContextImpl(DBAdapter context, VariationControl loader) {
		this.context = context;
		this.control = loader;
	}

	public final VariationSetImpl get() {
		return this.control.get(this.context, null);
	}

	public final VariationSetImpl getAfter(long low) {
		return this.control.get(this.context, new Long(low));
	}

	public final int removeOutdated(long upper) {
		return this.control.remove(this.context, upper);
	}

	public final VariationVersion max() {
		return this.control.max(this.context);
	}

	public final int removeSpecified(Iterable<Variation> it) {
		return this.control.removeSpecified(this.context, it);
	}
}