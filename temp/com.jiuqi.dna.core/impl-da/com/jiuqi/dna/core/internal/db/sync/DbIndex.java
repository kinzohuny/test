package com.jiuqi.dna.core.internal.db.sync;

import java.util.ArrayList;
import java.util.BitSet;

import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.IndexItemImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;

public abstract class DbIndex<TTable extends DbTable<TTable, TColumn, TDataType, TIndex>, TColumn extends DbColumn<TTable, TColumn, TDataType, TIndex>, TDataType extends DbDataType<TTable, TColumn, TDataType, TIndex>, TIndex extends DbIndex<TTable, TColumn, TDataType, TIndex>> {

	@Override
	public final String toString() {
		return this.name;
	}

	public final TTable table;
	public String name;
	public boolean unique;

	protected DbIndex(TTable table, String name, boolean unique) {
		this.table = table;
		this.name = name;
		this.unique = unique;
	}

	protected boolean isPrimaryKey() {
		return this == this.table.primaryKey;
	}

	final ArrayList<TColumn> columns = new ArrayList<TColumn>();
	final BitSet desc = new BitSet();

	public final void add(TColumn column, boolean desc) {
		this.columns.add(column);
		if (desc) {
			this.desc.set(this.columns.size() - 1);
		}
	}

	@Deprecated
	public final boolean containsOnlyRecid() {
		return this.columns.size() == 1 && this.unique && this.columns.get(0).name.equalsIgnoreCase(TableDefineImpl.FIELD_DBNAME_RECID);
	}

	public final boolean containsColumn(TColumn column) {
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			if (this.columns.get(i) == column) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 索引结构是否相同，不包括唯一性。
	 * 
	 * @param define
	 * @return
	 */
	public final boolean structEquals(IndexDefineImpl define) {
		if (define.getItems().size() != this.columns.size()) {
			return false;
		}
		for (int i = 0, c = define.getItems().size(); i < c; i++) {
			IndexItemImpl l = define.getItems().get(i);
			if (!l.getField().getNameInDB().equals(this.columns.get(i).name)) {
				return false;
			}
			if (l.isDesc() != this.desc.get(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean bitmap;
}