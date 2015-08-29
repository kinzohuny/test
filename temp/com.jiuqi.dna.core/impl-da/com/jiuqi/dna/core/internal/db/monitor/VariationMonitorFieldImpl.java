package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.db.monitor.VariationMonitorField;
import com.jiuqi.dna.core.impl.NamedDefineImpl;
import com.jiuqi.dna.core.misc.SXElement;

final class VariationMonitorFieldImpl extends NamedDefineImpl implements
		VariationMonitorField {

	public final String getWatchFN() {
		return this.watchFN;
	}

	public final String getOldValueFN() {
		return this.oldValueFN;
	}

	public final String getNewValueFN() {
		return this.newValueFN;
	}

	final VariationMonitorImpl monitor;
	final String watchFN;
	final String oldValueFN;
	final String newValueFN;

	VariationMonitorFieldImpl(VariationMonitorImpl monitor, String watchFN,
			String oldValueFN, String newValueFN) {
		super(watchFN);
		this.monitor = monitor;
		this.watchFN = watchFN;
		this.oldValueFN = oldValueFN;
		this.newValueFN = newValueFN;
	}

	/**
	 * 反序列化
	 * 
	 * @param monitor
	 * @param element
	 */
	VariationMonitorFieldImpl(VariationMonitorImpl monitor, SXElement element) {
		super(element.getString(ATTR_WATCH));
		this.monitor = monitor;
		this.watchFN = element.getString(ATTR_WATCH);
		this.oldValueFN = element.getString(ATTR_OLD_VALUE);
		this.newValueFN = element.getString(ATTR_NEW_VALUE);
	}

	/**
	 * 克隆
	 * 
	 * @param monitor
	 * @param sample
	 */
	VariationMonitorFieldImpl(VariationMonitorImpl monitor,
			VariationMonitorFieldImpl sample) {
		super(sample);
		this.monitor = monitor;
		this.watchFN = sample.watchFN;
		this.oldValueFN = sample.oldValueFN;
		this.newValueFN = sample.newValueFN;
	}

	static final String ELEMENT_SELF = "field";

	@Override
	public final String getXMLTagName() {
		return ELEMENT_SELF;
	}

	static final String ATTR_WATCH = "watch";
	static final String ATTR_OLD_VALUE = "oldvalue";
	static final String ATTR_NEW_VALUE = "newvalue";

	@Override
	public final void render(SXElement e) {
		e.setString(ATTR_WATCH, this.watchFN);
		e.setString(ATTR_OLD_VALUE, this.oldValueFN);
		e.setString(ATTR_NEW_VALUE, this.newValueFN);
	}

	@Override
	public final String toString() {
		return "[monitor_field:" + this.name + ", watch:" + this.watchFN + ", old_fn:" + this.oldValueFN + ", new_fn:" + this.newValueFN + "]";
	}
}