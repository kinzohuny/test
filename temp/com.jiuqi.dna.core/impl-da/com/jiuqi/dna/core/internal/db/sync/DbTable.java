package com.jiuqi.dna.core.internal.db.sync;

import java.util.ArrayList;

import com.jiuqi.dna.core.def.table.IndexType;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.IndexItemImpl;
import com.jiuqi.dna.core.impl.LinkedStringKeyMap;

public abstract class DbTable<TTable extends DbTable<TTable, TColumn, TDataType, TIndex>, TColumn extends DbColumn<TTable, TColumn, TDataType, TIndex>, TDataType extends DbDataType<TTable, TColumn, TDataType, TIndex>, TIndex extends DbIndex<TTable, TColumn, TDataType, TIndex>> {

	@Override
	public final String toString() {
		return this.name;
	}

	public final String name;

	protected DbTable(String name) {
		this.name = name;
	}

	public final LinkedStringKeyMap<TColumn> columns = new LinkedStringKeyMap<TColumn>(true);

	public final TColumn findColumn(String name) {
		return this.columns.find(name);
	}

	public final TColumn getColumn(String name) {
		return this.columns.find(name);
	}

	protected abstract TColumn newColumnOnly(String name);

	public final TColumn addColumn(String name) {
		TColumn c = this.newColumnOnly(name);
		this.columns.put(name, c, true);
		return c;
	}

	final void removeColumnCascade(TColumn column) {
		this.columns.remove(column.name);
		for (int i = this.indexes.size() - 1; i >= 0; i--) {
			TIndex index = this.indexes.get(i);
			if (index.containsColumn(column)) {
				this.removeIndex(index);
			}
		}
	}

	public final void removeColumnsCascade(ArrayList<TColumn> columns) {
		for (int i = 0, c = columns.size(); i < c; i++) {
			this.removeColumnCascade(columns.get(i));
		}
	}

	public final LinkedStringKeyMap<TIndex> indexes = new LinkedStringKeyMap<TIndex>(true);

	public TIndex primaryKey;

	public final void clearIndexes() {
		this.indexes.clear();
		this.primaryKey = null;
	}

	public final TIndex findIndex(String name) {
		return this.indexes.find(name);
	}

	public final TIndex findStructEqualIndex(IndexDefineImpl index) {
		for (int i = 0, c = this.indexes.size(); i < c; i++) {
			TIndex compare = this.indexes.get(i);
			if (compare.structEquals(index)) {
				return compare;
			}
		}
		return null;
	}

	public final TIndex getIndex(String name) {
		return this.indexes.find(name);
	}

	public final void removeIndex(TIndex index) {
		this.indexes.remove(index.name, true);
		if (index == this.primaryKey) {
			this.primaryKey = null;
		}
	}

	public final void removeIndexes(ArrayList<TIndex> indexes) {
		for (int i = 0, c = indexes.size(); i < c; i++) {
			TIndex index = indexes.get(i);
			this.removeIndex(index);
		}
	}

	protected abstract TIndex newIndexOnly(String name, boolean unique);

	public final TIndex addIndex(String name, boolean unique) {
		TIndex index = this.newIndexOnly(name, unique);
		this.indexes.put(name, index, true);
		return index;
	}

	public final TIndex addIndexLike(IndexDefineImpl index) {
		TIndex oi = this.addIndex(index.namedb(), index.isUnique());
		if (index.getType() == IndexType.BITMAP) {
			oi.bitmap = true;
		}
		for (IndexItemImpl item : index.getItems()) {
			oi.add(this.getColumn(item.getField().getNameInDB()), item.isDesc());
		}
		return oi;
	}

	public final void fillIndexContainingColumn(ArrayList<TIndex> fill,
			ArrayList<TColumn> columns) {
		for (int i = 0, c = columns.size(); i < c; i++) {
			this.fillIndexContainColumn(fill, columns.get(i));
		}
	}

	public final void fillIndexContainColumn(ArrayList<TIndex> fill,
			TColumn column) {
		for (int i = 0, c = this.indexes.size(); i < c; i++) {
			final TIndex index = this.indexes.get(i);
			for (int j = 0, d = index.columns.size(); j < d; j++) {
				if (index.columns.get(j) == column && !fill.contains(index)) {
					fill.add(index);
				}
			}
		}
	}
}