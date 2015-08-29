package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.IndexItemDeclare;

/**
 * 索引项定义
 * 
 * @author gaojingxin
 * 
 */
public class IndexItemImpl extends DefineBaseImpl implements IndexItemDeclare {

	@Override
	public final String toString() {
		return this.field.name + ":" + Boolean.toString(this.desc);
	}

	public final void setDesc(boolean desc) {
		this.desc = desc;
	}

	public final TableFieldDefineImpl getField() {
		return this.field;
	}

	public final boolean isDesc() {
		return this.desc;
	}

	public final IndexDefineImpl index;

	final TableFieldDefineImpl field;

	boolean desc;

	IndexItemImpl(IndexDefineImpl index, TableFieldDefineImpl field,
			boolean desc) {
		if (index.owner != field.owner) {
			throw new IllegalArgumentException("不在相同逻辑表。");
		}
		if (index.dbTable != field.dbTable && !field.isRECID()) {
			throw new IllegalArgumentException("不在相同的物理表。");
		}
		this.index = index;
		this.field = field;
		this.desc = desc;
	}

	final IndexItemImpl clone(IndexDefineImpl index) {
		return new IndexItemImpl(index, this);
	}

	/**
	 * 克隆的构造方法
	 * 
	 * @param index
	 * @param sample
	 */
	private IndexItemImpl(IndexDefineImpl index, IndexItemImpl sample) {
		super(sample);
		this.index = index;
		this.field = index.owner.fields.get(sample.field.name);
		this.desc = sample.desc;
	}

	static final IndexItemImpl newForMerge(IndexDefineImpl index,
			TableFieldDefineImpl field) {
		return new IndexItemImpl(index, field);
	}

	IndexItemImpl(IndexDefineImpl index, TableFieldDefineImpl field) {
		this.index = index;
		this.field = field;
	}

	@Override
	public final String getXMLTagName() {
		return xml_tag;
	}

	static final String xml_tag = "index-item";

	@Override
	final void assignFrom(Object sample) {
		super.assignFrom(sample);
		final IndexItemImpl item = (IndexItemImpl) sample;
		this.desc = item.desc;
	}

}
