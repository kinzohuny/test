package com.jiuqi.dna.core.internal.db.support.dm.sync;

import com.jiuqi.dna.core.internal.db.sync.DbIndex;

final class DmIndex extends DbIndex<DmTable, DmColumn, DmDataType, DmIndex> {

	protected DmIndex(DmTable table, String name, boolean unique) {
		super(table, name, unique);
	}
}