package com.jiuqi.dna.core.internal.db.support.sqlserver.sync;

import com.jiuqi.dna.core.internal.db.sync.DbTable;

final class SqlserverTable
		extends
		DbTable<SqlserverTable, SqlserverColumn, SqlserverDataType, SqlserverIndex> {

	SqlserverTable(String name) {
		super(name);
	}

	@Override
	protected final SqlserverColumn newColumnOnly(String name) {
		return new SqlserverColumn(this, name);
	}

	@Override
	protected final SqlserverIndex newIndexOnly(String name, boolean unique) {
		return new SqlserverIndex(this, name, unique);
	}

}