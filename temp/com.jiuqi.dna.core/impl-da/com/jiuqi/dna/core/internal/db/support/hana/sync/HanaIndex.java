package com.jiuqi.dna.core.internal.db.support.hana.sync;

import com.jiuqi.dna.core.internal.db.sync.DbIndex;

public class HanaIndex extends
		DbIndex<HanaTable, HanaColumn, HanaDataType, HanaIndex> {

	protected HanaIndex(HanaTable table, String name, boolean unique) {
		super(table, name, unique);
	}
}