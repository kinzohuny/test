package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.IndexDeclare;
import com.jiuqi.dna.core.def.table.IndexType;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * 索引定义实现类
 * 
 * @author houchunlei
 * 
 */
public final class IndexDefineImpl extends NamedDefineImpl implements
		IndexDeclare {

	public final TableDefineImpl getOwner() {
		return this.owner;
	}

	public final MetaBaseContainerImpl<? extends IndexItemImpl> getItems() {
		return this.items;
	}

	public final boolean isUnique() {
		return this.unique;
	}

	public final IndexType getType() {
		return this.type;
	}

	public final void setUnique(boolean unique) {
		this.unique = unique;
	}

	public final IndexItemImpl addItem(TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("索引字段");
		}
		return this.addItem((TableFieldDefineImpl) field, false);
	}

	public final IndexItemImpl addItem(TableFieldDefine field, boolean desc) {
		if (field == null) {
			throw new NullArgumentException("索引字段");
		}
		return this.addItem((TableFieldDefineImpl) field, desc);
	}

	/**
	 * 所属逻辑表定义
	 */
	public final TableDefineImpl owner;

	/**
	 * 所属的物理表
	 */
	public final DBTableDefineImpl dbTable;

	/**
	 * 索引字段列表
	 */
	public final MetaBaseContainerImpl<IndexItemImpl> items = new MetaBaseContainerImpl<IndexItemImpl>();

	/**
	 * 是否唯一索引
	 */
	private boolean unique;

	/**
	 * 索引类型
	 */
	private final IndexType type;
	/**
	 * 数据库中名称
	 */
	private String namedb;

	/**
	 * 构造逻辑表使用的索引定义
	 * 
	 * @param owner
	 * @param dbTable
	 * @param name
	 * @param unique
	 */
	public IndexDefineImpl(TableDefineImpl owner, DBTableDefineImpl dbTable,
			String name, IndexType type) {
		super(name);
		if (owner == null) {
			throw new NullPointerException();
		}
		if (dbTable.owner != owner) {
			throw new IllegalArgumentException();
		}
		this.owner = owner;
		this.dbTable = dbTable;
		if (type == null) {
			throw new IllegalArgumentException();
		}
		this.type = type;
		this.namedb = name.toUpperCase();
	}

	public final String namedb() {
		return this.namedb;
	}

	public final void setNamedb(String namedb) {
		if (namedb == null || namedb.length() == 0) {
			throw new NullPointerException();
		}
		this.namedb = namedb.toUpperCase();
	}

	// public final void setType(IndexType type) {
	// if (type == null) {
	// throw new IllegalArgumentException();
	// }
	// this.type = type;
	// }

	final IndexItemImpl findItem(TableFieldDefineImpl field) {
		for (int i = 0, c = this.items.size(); i < c; i++) {
			IndexItemImpl item = this.items.get(i);
			if (item.getField() == field) {
				return item;
			}
		}
		return null;
	}

	final IndexItemImpl addItem(TableFieldDefineImpl field, boolean desc) {
		if (this.findItem(field) != null) {
			throw new IllegalArgumentException("字段[" + field.name + "]在索引[" + this.getName() + "]中已存在。");
		}
		IndexItemImpl item = new IndexItemImpl(this, field, desc);
		this.items.add(item);
		return item;
	}

	final boolean structEquals(IndexDefineImpl index) {
		if (this == index) {
			return true;
		}
		if (this.items.size() != index.items.size()) {
			return false;
		} else {
			for (int i = 0, c = this.items.size(); i < c; i++) {
				IndexItemImpl left = this.items.get(i);
				IndexItemImpl right = index.items.get(i);
				if (left.desc != right.desc || !left.field.namedb().equals(right.field.namedb())) {
					return false;
				}
			}
			return true;
		}
	}

	@Override
	public final String getXMLTagName() {
		return index_tag;
	}

	static final String index_tag = "index";
	static final String bitmap_tag = "bitmap";

	static final IndexDefineImpl newForMerge(TableDefineImpl table,
			DBTableDefineImpl dbTable, String name, IndexType type) {
		return new IndexDefineImpl(table, dbTable, name, type);
	}

	private IndexDefineImpl(TableDefineImpl owner, DBTableDefineImpl dbTable,
			String name) {
		super(name);
		this.owner = owner;
		this.dbTable = dbTable;
		this.namedb = name.toUpperCase();
		this.type = IndexType.B_TREE;
	}

	final IndexDefineImpl clone(TableDefineImpl owner) {
		return new IndexDefineImpl(owner, this);
	}

	private IndexDefineImpl(TableDefineImpl owner, IndexDefineImpl sample) {
		super(sample);
		this.owner = owner;
		this.namedb = sample.namedb;
		this.dbTable = owner.dbTables.get(sample.dbTable.name);
		this.unique = sample.unique;
		for (int i = 0, c = sample.items.size(); i < c; i++) {
			this.items.add(sample.items.get(i).clone(this));
		}
		this.type = sample.type;
	}

	@Override
	final void assignFrom(Object sample) {
		super.assignFrom(sample);
		IndexDefineImpl index = (IndexDefineImpl) sample;
		this.unique = index.unique;
		for (int i = 0, c = index.items.size(); i < c; i++) {
			IndexItemImpl from = index.items.get(i);
			TableFieldDefineImpl f = this.owner.fields.get(from.field.name);
			IndexItemImpl to = this.findItem(f);
			if (to == null) {
				to = from.clone(this);
				this.items.add(i, to);
			} else {
				this.items.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
		this.items.trunc(index.items.size());
	}

	static final ExistingDetector<NamedDefineContainerImpl<IndexDefineImpl>, IndexDefineImpl, String> detector = new ExistingDetector<NamedDefineContainerImpl<IndexDefineImpl>, IndexDefineImpl, String>() {

		public boolean exists(
				NamedDefineContainerImpl<IndexDefineImpl> container,
				String key, IndexDefineImpl ignore) {
			if (ignore != null && key.equals(ignore.namedb)) {
				return false;
			}
			for (int i = 0, c = container.size(); i < c; i++) {
				if (container.get(i).namedb.equals(key)) {
					return true;
				}
			}
			return false;
		}
	};

	final boolean containOnlyRecid() {
		return this.items.size() == 1 && this.items.get(0).field.namedb().equals(TableDefineImpl.FIELD_DBNAME_RECID);
	}

}
