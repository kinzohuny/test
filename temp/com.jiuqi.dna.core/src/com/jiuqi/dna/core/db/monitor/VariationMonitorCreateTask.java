package com.jiuqi.dna.core.db.monitor;

import java.nio.charset.Charset;

import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.NameUtl;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.internal.db.monitor.VariationMonitorFieldMapping;
import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * 创建监视器
 * 
 * @author houchunlei
 * 
 */
public final class VariationMonitorCreateTask extends SimpleTask {

	/**
	 * @param target
	 *            监视器目标表
	 */
	public VariationMonitorCreateTask(TableDefine target) {
		this(VariationMonitorHelper.defaultMonitorName(target.getName()), target, VariationMonitorHelper.defaultVariationName(target.getName()), VariationMonitorHelper.defaultVariationName(target.getName()));
	}

	/**
	 * 
	 * @param name
	 *            监视器名称
	 * @param target
	 *            监视器目标表
	 */
	public VariationMonitorCreateTask(String name, TableDefine target) {
		this(name, target, VariationMonitorHelper.defaultVariationName(target.getName()), VariationMonitorHelper.defaultVariationName(target.getName()));
	}

	/**
	 * @param name
	 *            监视器名称
	 * @param target
	 *            监视器目标表
	 * @param variation
	 *            变化量表的名称（不超过30个字节长度）
	 */
	public VariationMonitorCreateTask(String name, TableDefine target,
			String variation) {
		this(name, target, variation, variation);
	}

	/**
	 * 
	 * @param name
	 *            监视器名称
	 * @param target
	 *            监视器目标表
	 * @param variation
	 *            变化量表的名称（不超过30个字节长度，会被自动大写化）
	 * @param trigger
	 *            触发器名称（不超过30个字节长度，会被自动大写化）
	 */
	private VariationMonitorCreateTask(String name, TableDefine target,
			String variation, String trigger) {
		if (name == null || name.trim().length() == 0) {
			throw new NullPointerException("监视器名称为空。");
		}
		this.name = name;
		if (target == null) {
			throw new NullArgumentException("监视器的目标表为空。");
		}
		this.target = target;
		if (variation == null || variation.trim().length() == 0) {
			throw new NullArgumentException("监视器的变化量表名称");
		} else if (NameUtl.length(variation, CHARSET_GBK) > 30) {
			throw new IllegalArgumentException("监视器的变化量表名称超过长度限制。");
		}
		this.variation = variation.toUpperCase();
		if (trigger == null || trigger.trim().length() == 0) {
			throw new NullArgumentException("监视器的触发器名称");
		} else if (NameUtl.length(trigger, CHARSET_GBK) > 30) {
			throw new IllegalArgumentException("监视器的触发器名称超过长度限制。");
		}
		this.trigger = trigger.toUpperCase();
	}

	private static final Charset CHARSET_GBK = Charset.forName("GBK");

	public final String name;
	public final TableDefine target;
	public final String variation;
	public final String trigger;
	private NamedDefineContainerImpl<VariationMonitorFieldMapping> watches = new NamedDefineContainerImpl<VariationMonitorFieldMapping>();

	/**
	 * 增加监视字段
	 * 
	 * @param field
	 * @return
	 */
	public final VariationMonitorCreateTask watch(TableFieldDefine field) {
		this.add0(field);
		return this;
	}

	public final VariationMonitorCreateTask watch(String field) {
		this.add0(this.target.getFields().get(field));
		return this;
	}

	/**
	 * 增加监视字段
	 * 
	 * @param field
	 * @param others
	 * @return
	 */
	public final VariationMonitorCreateTask watch(TableFieldDefine field,
			TableFieldDefine... others) {
		this.add0(field);
		if (others != null) {
			for (TableFieldDefine f : others) {
				if (f != null) {
					this.add0(f);
				}
			}
		}
		return this;
	}

	/**
	 * 增加监视字段
	 * 
	 * @param fields
	 * @return
	 */
	public final VariationMonitorCreateTask watch(
			Iterable<TableFieldDefine> fields) {
		if (fields != null) {
			for (TableFieldDefine field : fields) {
				if (field != null) {
					this.add0(field);
				}
			}
		}
		return this;
	}

	private final void add0(TableFieldDefine field) {
		if (!field.getOwner().getName().equals(this.target.getName())) {
			throw new IllegalArgumentException();
		}
		for (VariationMonitorFieldMapping fm : this.watches) {
			if (fm.field == field) {
				return;
			}
		}
		this.watches.add(new VariationMonitorFieldMapping(this.target.getFields().get(field.getName())));
	}

	public final NamedElementContainer<VariationMonitorFieldMapping> getWatches() {
		return this.watches;
	}

	/**
	 * 触发器的名称
	 * 
	 * @return
	 */
	public final String getTrigger() {
		return this.trigger;
	}

	/**
	 * 创建的监视器（在任务调用后返回）
	 */
	public VariationMonitor monitor;
}