package com.jiuqi.dna.core.db.monitor;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.internal.db.monitor.VariationMonitorFieldMapping;
import com.jiuqi.dna.core.internal.db.monitor.VariationMonitorImpl;
import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * 修改监视器任务
 * 
 * <p>
 * 允许修改监视器的监视字段，修改驱动策略。
 * 
 * @author houchunlei
 * 
 */
public final class VariationMonitorModifyTask extends SimpleTask {

	public final VariationMonitor monitor;

	public VariationMonitorModifyTask(Context context, VariationMonitor monitor) {
		if (monitor == null) {
			throw new NullPointerException();
		}
		this.monitor = VariationMonitorImpl.clone((VariationMonitorImpl) monitor);
		this.target = context.get(TableDefine.class, monitor.getTargetName());
		// this.variation = context.get(TableDefine.class,
		// monitor.getVariationName());
	}

	private final TableDefine target;
	// private final TableDefine variation;

	private final NamedDefineContainerImpl<VariationMonitorFieldMapping> watches = new NamedDefineContainerImpl<VariationMonitorFieldMapping>();

	private final NamedDefineContainerImpl<VariationMonitorFieldMapping> unwatches = new NamedDefineContainerImpl<VariationMonitorFieldMapping>();

	public final NamedElementContainer<VariationMonitorFieldMapping> getWatches() {
		return this.watches;
	}

	public final NamedElementContainer<VariationMonitorFieldMapping> getUnwatches() {
		return this.unwatches;
	}

	private final void unwatch0(TableFieldDefine field) {
		if (field == null) {
			throw new NullPointerException();
		}
		if (!field.getOwner().getName().equals(this.target.getName())) {
			throw new IllegalArgumentException();
		}
		if (this.monitor.getWatches().find(field.getName()) == null) {
			throw new IllegalArgumentException("监视器未监视目标字段[" + field.getName() + "]。");
		}
		this.unwatches.add(new VariationMonitorFieldMapping(field));
	}

	/**
	 * 取消监视字段
	 * 
	 * @param field
	 * @return
	 */
	public final VariationMonitorModifyTask unwatch(TableFieldDefine field) {
		this.unwatch0(field);
		return this;
	}

	/**
	 * 取消监视字段
	 * 
	 * @param field
	 * @param others
	 * @return
	 */
	public final VariationMonitorModifyTask unwatch(TableFieldDefine field,
			TableFieldDefine... others) {
		this.unwatch0(field);
		for (TableFieldDefine other : others) {
			this.unwatch0(other);
		}
		return this;
	}

	/**
	 * 取消监视字段
	 * 
	 * @param fields
	 * @return
	 */
	public final VariationMonitorModifyTask unwatch(
			Iterable<TableFieldDefine> fields) {
		for (TableFieldDefine field : fields) {
			this.unwatch0(field);
		}
		return this;
	}

	private final void watch0(TableFieldDefine field) {
		if (!field.getOwner().getName().equals(this.target.getName())) {
			throw new IllegalArgumentException();
		}
		if (this.monitor.getWatches().find(field.getName()) != null) {
			throw new IllegalArgumentException("监视器已经监视了目标字段[" + field.getName() + "]。");
		}
		this.watches.add(new VariationMonitorFieldMapping(field));
	}

	/**
	 * 增加监视字段
	 * 
	 * @param field
	 * @return
	 */
	public final VariationMonitorModifyTask watch(TableFieldDefine field) {
		this.watch0(field);
		return this;
	}

	/**
	 * 增加监视字段
	 * 
	 * @param field
	 * @param others
	 * @return
	 */
	public final VariationMonitorModifyTask watch(TableFieldDefine field,
			TableFieldDefine... others) {
		this.watch0(field);
		for (TableFieldDefine other : others) {
			this.watch0(other);
		}
		return this;
	}

	/**
	 * 增加监视字段
	 * 
	 * @param fields
	 * @return
	 */
	public final VariationMonitorModifyTask watch(
			Iterable<TableFieldDefine> fields) {
		for (TableFieldDefine field : fields) {
			this.watch0(field);
		}
		return this;
	}
}