package com.jiuqi.dna.core.internal.db.support.oracle.sync;

import com.jiuqi.dna.core.internal.db.sync.DbIndex;

final class OracleIndex extends
		DbIndex<OracleTable, OracleColumn, OracleDataType, OracleIndex> {

	OracleIndex(OracleTable table, String name, boolean unique) {
		super(table, name, unique);
	}
}