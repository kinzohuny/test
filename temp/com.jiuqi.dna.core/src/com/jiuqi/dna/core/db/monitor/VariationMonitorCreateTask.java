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
 * ����������
 * 
 * @author houchunlei
 * 
 */
public final class VariationMonitorCreateTask extends SimpleTask {

	/**
	 * @param target
	 *            ������Ŀ���
	 */
	public VariationMonitorCreateTask(TableDefine target) {
		this(VariationMonitorHelper.defaultMonitorName(target.getName()), target, VariationMonitorHelper.defaultVariationName(target.getName()), VariationMonitorHelper.defaultVariationName(target.getName()));
	}

	/**
	 * 
	 * @param name
	 *            ����������
	 * @param target
	 *            ������Ŀ���
	 */
	public VariationMonitorCreateTask(String name, TableDefine target) {
		this(name, target, VariationMonitorHelper.defaultVariationName(target.getName()), VariationMonitorHelper.defaultVariationName(target.getName()));
	}

	/**
	 * @param name
	 *            ����������
	 * @param target
	 *            ������Ŀ���
	 * @param variation
	 *            �仯��������ƣ�������30���ֽڳ��ȣ�
	 */
	public VariationMonitorCreateTask(String name, TableDefine target,
			String variation) {
		this(name, target, variation, variation);
	}

	/**
	 * 
	 * @param name
	 *            ����������
	 * @param target
	 *            ������Ŀ���
	 * @param variation
	 *            �仯��������ƣ�������30���ֽڳ��ȣ��ᱻ�Զ���д����
	 * @param trigger
	 *            ���������ƣ�������30���ֽڳ��ȣ��ᱻ�Զ���д����
	 */
	private VariationMonitorCreateTask(String name, TableDefine target,
			String variation, String trigger) {
		if (name == null || name.trim().length() == 0) {
			throw new NullPointerException("����������Ϊ�ա�");
		}
		this.name = name;
		if (target == null) {
			throw new NullArgumentException("��������Ŀ���Ϊ�ա�");
		}
		this.target = target;
		if (variation == null || variation.trim().length() == 0) {
			throw new NullArgumentException("�������ı仯��������");
		} else if (NameUtl.length(variation, CHARSET_GBK) > 30) {
			throw new IllegalArgumentException("�������ı仯�������Ƴ����������ơ�");
		}
		this.variation = variation.toUpperCase();
		if (trigger == null || trigger.trim().length() == 0) {
			throw new NullArgumentException("�������Ĵ���������");
		} else if (NameUtl.length(trigger, CHARSET_GBK) > 30) {
			throw new IllegalArgumentException("�������Ĵ��������Ƴ����������ơ�");
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
	 * ���Ӽ����ֶ�
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
	 * ���Ӽ����ֶ�
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
	 * ���Ӽ����ֶ�
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
	 * ������������
	 * 
	 * @return
	 */
	public final String getTrigger() {
		return this.trigger;
	}

	/**
	 * �����ļ���������������ú󷵻أ�
	 */
	public VariationMonitor monitor;
}