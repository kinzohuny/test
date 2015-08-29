package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.DBTableDeclare;
import com.jiuqi.dna.core.def.table.TableType;
import com.jiuqi.dna.core.type.DataType;

/**
 * 物理表定义
 * 
 * @author houchunlei
 */
public final class DBTableDefineImpl extends NamedDefineImpl implements
		DBTableDeclare {

	public final TableDefineImpl getOwner() {
		return this.owner;
	}

	public final int getFieldCount() {
		return this.fields.size();
	}

	public final TableFieldDefineImpl newField(String name, DataType type) {
		return this.owner.newField(this, name, type, false);
	}

	public final String getNameInDB() {
		return this.namedb;
	}

	public final TableDefineImpl owner;

	public final StringKeyMap<TableFieldDefineImpl> fields = new StringKeyMap<TableFieldDefineImpl>(false);

	private String namedb;

	private String pkeyName;
	
	private TableType tableType = TableType.NORMAL;

	DBTableDefineImpl(TableDefineImpl owner, String name) {
		super(name.toUpperCase());
		this.owner = owner;
		this.namedb = name.toUpperCase();
		this.pkeyName = TableDefineImpl.DNA_PK_PREFIX.concat(this.namedb);
		this.tableType = TableType.NORMAL;//初始化为普通表
	}

	public final boolean isPrimary() {
		return this == this.owner.primary;
	}

	final void store(TableFieldDefineImpl field) {
		this.fields.put(field.namedb(), field, true);
	}

	final void unstore(TableFieldDefineImpl field) {
		this.fields.remove(field.namedb());
	}

	public final String namedb() {
		return this.namedb;
	}

	final void setNamedb(String namedb) {
		if (namedb == null || namedb.length() == 0) {
			throw new NullPointerException();
		} else if (namedb == this.namedb || namedb.equals(this.namedb)) {
			return;
		}
		this.namedb = namedb.toUpperCase();
	}

	public final String getPkeyName() {
		return this.pkeyName;
	}

	public final void setPkeyName(String name) {
		if (name != null && name.length() > 0 && name != this.pkeyName && !name.equals(this.pkeyName)) {
			this.pkeyName = name.toUpperCase();
		}
	}

	private int index;
	private int dbTablesModCount;

	public final int index() {
		final NamedDefineContainerImpl<DBTableDefineImpl> dbTables = this.owner.dbTables;
		final int dbtmc = dbTables.getModCount();
		if (this.dbTablesModCount != dbtmc) {
			this.dbTablesModCount = dbtmc;
			this.index = dbTables.indexOf(this);
		}
		return this.index;
	}

	public final void removeDuplicatedIndex() {
		for (int i = this.owner.indexes.size() - 1; i >= 0; i--) {
			IndexDefineImpl index = this.owner.indexes.get(i);
			if (index.containOnlyRecid()) {
				this.owner.indexes.remove(i);
			} else if (this.owner.logicalKey != null && index.structEquals(this.owner.logicalKey)) {
				this.owner.indexes.remove(i);
			} else {
				for (int j = 0; j < i; j++) {
					if (index.structEquals(this.owner.indexes.get(j))) {
						this.owner.indexes.remove(i);
						break;
					}
				}
			}
		}
	}

	/**
	 * 克隆到目标逻辑表中
	 * 
	 * @param table
	 * @return
	 */
	final DBTableDefineImpl clone(TableDefineImpl table) {
		return new DBTableDefineImpl(table, this);
	}

	/**
	 * 克隆的构造方法
	 * 
	 * @param owner
	 * @param sample
	 */
	private DBTableDefineImpl(TableDefineImpl owner, DBTableDefineImpl sample) {
		super(sample);
		this.owner = owner;
		this.namedb = sample.namedb;
		this.pkeyName = sample.pkeyName;
		this.tableType = sample.tableType;
	}

	@Override
	final void assignFrom(Object sample) {
		super.assignFrom(sample);
		DBTableDefineImpl from = (DBTableDefineImpl) sample;
		this.namedb = from.namedb;
		this.pkeyName = from.pkeyName;
	}

	@Override
	public final String getXMLTagName() {
		return dbtable_tag;
	}

	static final String dbtable_tag = "dbtable";

	public void setTableType(TableType type){
	    this.tableType = type;
    }

	public TableType getTableType(){
	    return this.tableType;
    }

}
