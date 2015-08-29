package com.jiuqi.dna.core.internal.db.support.db2.sync;

import com.jiuqi.dna.core.internal.db.sync.DbIndex;

final class Db2Index extends
		DbIndex<Db2Table, Db2Column, Db2DataType, Db2Index> {

	Db2Index(Db2Table table, String name, boolean unique) {
		super(table, name, unique);
	}
}