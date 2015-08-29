package com.jiuqi.dna.core.internal.db.support.kingbase.sync;

import com.jiuqi.dna.core.internal.db.sync.DbTable;

final class KingbaseTable extends
		DbTable<KingbaseTable, KingbaseColumn, KingbaseDataType, KingbaseIndex> {

	KingbaseTable(String name) {
		super(name);
	}

	@Override
	protected final KingbaseColumn newColumnOnly(String name) {
		return new KingbaseColumn(this, name);
	}

	@Override
	protected final KingbaseIndex newIndexOnly(String name, boolean unique) {
		return new KingbaseIndex(this, name, unique);
	}

}