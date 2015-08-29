package com.jiuqi.dna.core.internal.db.support.hana.sync;

import com.jiuqi.dna.core.internal.db.sync.DbTable;

public class HanaTable extends
		DbTable<HanaTable, HanaColumn, HanaDataType, HanaIndex> {

	protected HanaTable(String name) {
		super(name);
	}

	@Override
	protected HanaColumn newColumnOnly(String name) {
		return new HanaColumn(this, name);
	}

	@Override
	protected HanaIndex newIndexOnly(String name, boolean unique) {
		return new HanaIndex(this, name, unique);
	}
}