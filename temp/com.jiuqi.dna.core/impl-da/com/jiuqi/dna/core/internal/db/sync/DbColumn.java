package com.jiuqi.dna.core.internal.db.sync;

import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.type.DataType;

public abstract class DbColumn<TTable extends DbTable<TTable, TColumn, TDataType, TIndex>, TColumn extends DbColumn<TTable, TColumn, TDataType, TIndex>, TDataType extends DbDataType<TTable, TColumn, TDataType, TIndex>, TIndex extends DbIndex<TTable, TColumn, TDataType, TIndex>> {

	public final TTable table;
	public String name;

	@Override
	public final String toString() {
		return this.name;
	}

	protected DbColumn(TTable table, String name) {
		this.table = table;
		this.name = name;
	}

	public TDataType type;
	public int length;
	public int precision;
	public int scale;
	public boolean notNull;
	public String defaultDefinition;

	protected abstract TypeAlterability typeAlterable(DataType type);

	final boolean isRecid() {
		// TODO ¥Û–°–¥
		return this.name.equals(TableDefineImpl.FIELD_DBNAME_RECID);
	}

	final boolean isRecver() {
		return this.name.equals(TableDefineImpl.FIELD_DBNAME_RECVER);
	}

	@SuppressWarnings("unchecked")
	final String typeString() {
		if (this.type == null) {
			throw new IllegalStateException();
		}
		return this.type.toString((TColumn) this);
	}
}