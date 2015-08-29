package com.jiuqi.dna.core.internal.db.support.dm.sync;

import com.jiuqi.dna.core.internal.db.sync.DbTable;

final class DmTable extends DbTable<DmTable, DmColumn, DmDataType, DmIndex> {

	protected DmTable(String name) {
		super(name);
	}

	@Override
	protected DmColumn newColumnOnly(String name) {
		return new DmColumn(this, name);
	}

	@Override
	protected DmIndex newIndexOnly(String name, boolean unique) {
		return new DmIndex(this, name, unique);
	}
}