package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.db.monitor.VariationMonitor;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXElementBuilder;
import com.jiuqi.dna.core.type.GUID;

@StructClass
public final class VariationMonitorImpl implements VariationMonitor, Cloneable {

	@SuppressWarnings("unused")
	private static final Logger logger = DNALogManager.getLogger("core/db/monitor");

	public final GUID getId() {
		return this.id;
	}

	public final String getName() {
		return this.name;
	}

	public final String getTarget() {
		return this.target;
	}

	public final String getTargetName() {
		return this.target;
	}

	public final String getVariationName() {
		return this.variation;
	}

	public final NamedDefineContainerImpl<VariationMonitorFieldImpl> getWatches() {
		return this.watches;
	}

	final GUID id;
	final String name;
	final String target;
	final String variation;
	final String trigger;

	private long version;
	// MonitorVariation variation;
	final NamedDefineContainerImpl<VariationMonitorFieldImpl> watches = new NamedDefineContainerImpl<VariationMonitorFieldImpl>(false);

	VariationMonitorImpl(GUID id, String name, String target, String variation,
			String trigger) {
		this.id = id;
		this.name = name;
		this.target = target;
		this.variation = variation;
		this.trigger = trigger;
	}

	static final VariationMonitorImpl load(Context context,
			VariationMonitorMetadata mmd) {
		final TableDefine target = context.find(TableDefine.class, mmd.target);
		if (target == null) {
			throw new VariationMonitorInitializationException("无法实例化监视器：目标表[" + mmd.target + "]不存在。");
		}
		final TableDefine variation = context.find(TableDefine.class, mmd.variation);
		if (variation == null) {
			throw new VariationMonitorInitializationException("无法实例化监视器：变化量表[" + mmd.target + "]不存在。");
		}
		VariationMonitorImpl monitor = new VariationMonitorImpl(mmd.id, mmd.name, mmd.target, mmd.variation, mmd.trigger);
		monitor.version = mmd.version;
		// TODO 检查元数据和数据库结构的一致，触发器状态，变化量表结构
		// TODO 重建触发器，数据库迁移时不会迁移触发器。
		monitor.deserialize(mmd.setting);
		monitor.control = new VariationControl(monitor, (TableDefineImpl) target, (TableDefineImpl) variation);
		return monitor;
	}

	final long getVersion() {
		return this.version;
	}

	final void setVersion(long version) {
		this.version = version;
	}

	private volatile boolean disposed;

	final void dispose(Context context) throws Throwable {
		assert !this.disposed;
		this.disposed = true;
	}

	final void initializeWatches(
			NamedElementContainer<VariationMonitorFieldMapping> watches) {
		for (VariationMonitorFieldMapping watch : watches) {
			this.watch(watch);
		}
	}

	final VariationMonitorFieldImpl watch(VariationMonitorFieldMapping map) {
		if (this.watches.find(map.field.getName()) != null) {
			throw new IllegalStateException();
		}
		VariationMonitorFieldImpl mf = new VariationMonitorFieldImpl(this, map.field.getName(), map.oldValueFN, map.newValueFN);
		this.watches.add(mf);
		return mf;
	}

	// XXX
	// final void initialize(Context context, TableDefineImpl varition,
	// Iterable<VariationMonitorFieldMapping> watches, String trigger) {
	// this.version = context.newRECVER();
	// this.variation = new MonitorVariation(this, varition);
	// this.trigger = trigger;
	// for (VariationMonitorFieldMapping fm : watches) {
	// final TableFieldDefineImpl watch =
	// this.target.getColumn(fm.field.getName());
	// final TableFieldDefineImpl oldVal = varition.getColumn(fm.oldValueFN);
	// final TableFieldDefineImpl newVal = varition.getColumn(fm.newValueFN);
	// final VariationMonitorFieldImpl mf = new VariationMonitorFieldImpl(this,
	// watch, oldVal, newVal);
	// this.watches.add(mf);
	// }
	// this.control = new VariationControl(this);
	// }

	final VariationMonitorMetadata getMetadata() {
		final VariationMonitorMetadata mmd = new VariationMonitorMetadata(this.id, this.name);
		mmd.version = this.version;
		mmd.target = this.target;
		mmd.variation = this.variation;
		mmd.trigger = this.trigger;
		mmd.setting = this.serialize();
		return mmd;
	}

	static final String ELEMENT_SELF = "monitor";
	static final String ELEMENT_FIELDS = "fields";
	static final String MONITOR_ELEMENT_FIELD = "field";

	private final String serialize() {
		final SXElement e = SXElement.newDoc();
		this.render(e.append(ELEMENT_SELF));
		return e.toString();
	}

	private final void render(SXElement e) {
		final SXElement c = e.append(ELEMENT_FIELDS);
		for (VariationMonitorFieldImpl mf : this.watches) {
			mf.render(c.append(VariationMonitorFieldImpl.ELEMENT_SELF));
		}
	}

	private final void deserialize(String setting) {
		try {
			SXElement e = new SXElementBuilder().build(setting);
			this.merge(e.firstChild(ELEMENT_SELF));
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final void merge(SXElement e) {
		for (SXElement fe = e.firstChild(ELEMENT_FIELDS).firstChild(VariationMonitorFieldImpl.ELEMENT_SELF); fe != null; fe = fe.nextSibling(VariationMonitorFieldImpl.ELEMENT_SELF)) {
			this.watches.add(new VariationMonitorFieldImpl(this, fe));
		}
	}

	@Override
	protected final VariationMonitorImpl clone()
			throws CloneNotSupportedException {
		return clone(this);
	}

	public static final VariationMonitorImpl clone(VariationMonitorImpl source) {
		final VariationMonitorImpl clone = new VariationMonitorImpl(source.id, source.name, source.target, source.variation, source.trigger);
		clone.version = source.version;
		for (VariationMonitorFieldImpl mf : source.watches) {
			clone.watches.add(new VariationMonitorFieldImpl(clone, mf));
		}
		return clone;
	}

	VariationControl control;

	@Override
	public final String toString() {
		return "[monitor:" + this.name + "]";
	}
}