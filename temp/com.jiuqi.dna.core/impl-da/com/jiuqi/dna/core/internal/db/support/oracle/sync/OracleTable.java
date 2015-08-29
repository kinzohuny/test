package com.jiuqi.dna.core.internal.db.support.oracle.sync;

import com.jiuqi.dna.core.internal.db.sync.DbTable;

final class OracleTable extends
		DbTable<OracleTable, OracleColumn, OracleDataType, OracleIndex> {

	OracleTable(String name) {
		super(name);
	}

	@Override
	protected final OracleColumn newColumnOnly(String name) {
		return new OracleColumn(this, name);
	}

	@Override
	protected final OracleIndex newIndexOnly(String name, boolean unique) {
		return new OracleIndex(this, name, unique);
	}

}