package com.jiuqi.dna.core.internal.db.support.kingbase.sync;

import com.jiuqi.dna.core.internal.db.sync.DbIndex;

final class KingbaseIndex extends
		DbIndex<KingbaseTable, KingbaseColumn, KingbaseDataType, KingbaseIndex> {

	KingbaseIndex(KingbaseTable table, String name, boolean unique) {
		super(table, name, unique);
	}

}