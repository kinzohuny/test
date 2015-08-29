package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.table.TableFieldDeclare;
import com.jiuqi.dna.core.exception.NotDBTypeException;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;

/**
 * 逻辑表字段定义实现类
 * 
 * @author houchunlei
 * 
 */
public final class TableFieldDefineImpl extends NamedDefineImpl implements
		TableFieldDeclare, RelationColumn {

	@Override
	protected final boolean isNameCaseSensitive() {
		return false;
	}

	public final boolean isRECID() {
		return this.owner.f_recid == this;
	}

	public final boolean isRECVER() {
		return this == this.owner.f_recver;
	}

	public final void digestType(Digester digester) {
		this.digestAuthAndName(digester);
		this.type.digestType(digester);
	}

	public final TableDefineImpl getOwner() {
		return this.owner;
	}

	public final DBTableDefineImpl getDBTable() {
		return this.dbTable;
	}

	public final DataTypeInternal getType() {
		return this.type;
	}

	public final boolean adjustType(DataType type) {
		if (!type.isDBType()) {
			throw new NotDBTypeException(type.toString());
		}
		if (this.type == null) {
			this.type = (DataTypeInternal) type;
			return true;
		} else if (this.type == type) {
			return false;
		} else if (this.type.canDBTypeConvertTo(type)) {
			this.type = (DataTypeInternal) type;
			return true;
		}
		return false;
	}

	public final boolean isPrimaryKey() {
		IndexDefineImpl index = this.owner.logicalKey;
		if (this.isRECID()) {
			// too disgusting!!! but...must remain
			return index == null;
		} else {
			return index != null && index.findItem(this) != null;
		}
	}

	public final void setPrimaryKey(boolean logicalKey) {
		if (logicalKey) {
			this.owner.addKey(this);
		} else {
			this.owner.removeKey(this);
		}
	}

	public final boolean isKeepValid() {
		return this.notNull;
	}

	public final void setKeepValid(boolean value) {
		if (this.isRECID()) {
			throw new UnsupportedOperationException("不能修改RECID字段的非空属性.");
		} else if (this.isRECVER()) {
			throw new IllegalArgumentException("不能修改RECVER字段的非空属性.");
		}
		if (value) {
			this.notNull = true;
		} else if (!this.isPrimaryKey()) {
			this.notNull = false;
		}
	}

	public final ConstExpr getDefault() {
		return this.defaultValue;
	}

	public final void setDefault(ValueExpression expr) {
		if (expr == null || expr == NullExpr.NULL) {
			this.defaultValue = null;
		} else {
			try {
				this.defaultValue = (ConstExpr) expr;
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("字段的默认值必须为常量.", e);
			}
		}
	}

	public final void setDefault(Object value) {
		if (value == null || value == NullExpr.NULL) {
			this.defaultValue = null;
		} else {
			this.defaultValue = this.type.detect(ConstExpr.parser, value);
		}
	}

	public final boolean isReadonly() {
		return false;
	}

	public final void setReadonly(boolean value) {
		throw new UnsupportedOperationException();
	}

	public final String getNameInDB() {
		return this.namedb;
	}

	public final void setNameInDB(String namedb) {
	}

	public final boolean isTemplated() {
		return this.templated;
	}

	public final void setTemplated(boolean templated) {
		this.templated = templated;
	}

	public final TableDefineImpl owner;

	public final DBTableDefineImpl dbTable;

	public final boolean isStatic;

	private DataTypeInternal type;

	private ConstExpr defaultValue;

	private boolean notNull;

	private String namedb;

	boolean templated;

	TableFieldDefineImpl(TableDefineImpl owner, DBTableDefineImpl dbTable,
			String name, String namedb, DataTypeInternal type, boolean notNull,
			boolean isStatic) {
		super(name);
		this.owner = owner;
		this.dbTable = dbTable;
		this.namedb = namedb.toUpperCase();
		this.type = type;
		this.notNull = notNull;
		this.isStatic = isStatic;
	}

	public final String namedb() {
		return this.namedb;
	}

	public final void setNamedb(String namedb) {
		if (namedb == null || namedb.length() == 0) {
			throw new NullArgumentException("字段在数据库中名称");
		} else if (namedb == this.namedb || namedb.equals(this.namedb)) {
			return;
		}
		this.dbTable.unstore(this);
		this.namedb = namedb.toUpperCase();
		this.dbTable.store(this);
	}

	static final TableFieldDefineImpl newForMerge(TableDefineImpl owner,
			DBTableDefineImpl dbTable, String name) {
		return new TableFieldDefineImpl(owner, dbTable, name);
	}

	private TableFieldDefineImpl(TableDefineImpl owner,
			DBTableDefineImpl dbTable, String name) {
		super(name);
		this.owner = owner;
		this.dbTable = dbTable;
		this.namedb = name.toUpperCase();
		this.isStatic = false;
	}

	final TableFieldDefineImpl clone(TableDefineImpl table) {
		DBTableDefineImpl dbTable = table.dbTables.get(this.dbTable.name);
		TableFieldDefineImpl clone = new TableFieldDefineImpl(table, this);
		dbTable.store(clone);
		return clone;
	}

	private TableFieldDefineImpl(TableDefineImpl owner,
			TableFieldDefineImpl sample) {
		super(sample);
		this.owner = owner;
		this.dbTable = owner.dbTables.get(sample.dbTable.name);
		this.type = sample.type;
		this.namedb = sample.namedb;
		this.templated = sample.templated;
		this.notNull = sample.notNull;
		this.defaultValue = sample.defaultValue;
		this.isStatic = false;
	}

	@Override
	public final String getXMLTagName() {
		return field_tag;
	}

	static final String field_tag = "field";

	@Override
	final void assignFrom(Object sample) {
		super.assignFrom(sample);
		TableFieldDefineImpl f = (TableFieldDefineImpl) sample;
		if (!this.dbTable.name.equals(f.dbTable.name)) {
			throw new UnsupportedOperationException();
		}
		this.type = f.type;
		this.namedb = f.namedb;
		this.templated = f.templated;
		this.notNull = f.notNull;
		this.defaultValue = f.defaultValue;
	}

}
