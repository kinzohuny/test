package com.jiuqi.dna.core.internal.db.support.sqlserver.sync;

import com.jiuqi.dna.core.internal.db.sync.DbIndex;

final class SqlserverIndex
		extends
		DbIndex<SqlserverTable, SqlserverColumn, SqlserverDataType, SqlserverIndex> {

	SqlserverIndex(SqlserverTable table, String name, boolean unique) {
		super(table, name, unique);
	}

}