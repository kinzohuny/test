package com.jiuqi.dna.core.internal.db.support.mysql.sync;

import com.jiuqi.dna.core.internal.db.sync.DbIndex;

final class MysqlIndex extends
		DbIndex<MysqlTable, MysqlColumn, MysqlDataType, MysqlIndex> {

	MysqlIndex(MysqlTable table, String name, boolean unique) {
		super(table, name, unique);
	}

	@Override
	protected final boolean isPrimaryKey() {
		return this.name.equalsIgnoreCase("PRIMARY");
	}
}