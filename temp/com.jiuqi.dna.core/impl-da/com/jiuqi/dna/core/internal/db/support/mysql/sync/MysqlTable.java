package com.jiuqi.dna.core.internal.db.support.mysql.sync;

import com.jiuqi.dna.core.internal.db.sync.DbTable;

final class MysqlTable extends
		DbTable<MysqlTable, MysqlColumn, MysqlDataType, MysqlIndex> {

	MysqlTable(String name) {
		super(name);
	}

	@Override
	protected final MysqlColumn newColumnOnly(String name) {
		return new MysqlColumn(this, name);
	}

	@Override
	protected final MysqlIndex newIndexOnly(String name, boolean unique) {
		return new MysqlIndex(this, name, unique);
	}
}