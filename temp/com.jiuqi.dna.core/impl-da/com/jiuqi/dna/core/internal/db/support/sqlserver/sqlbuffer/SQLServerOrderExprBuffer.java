package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerOrderExprBuffer extends SQLServerExprBuffer {

	final boolean desc;

	public SQLServerOrderExprBuffer(SqlserverMetadata metadata,
			SQLServerQueryBuffer query, boolean desc) {
		super(metadata, query);
		this.desc = desc;
	}

	public SQLServerOrderExprBuffer(SqlserverMetadata metadata,
			SQLServerQueryBuffer query, String column, boolean desc) {
		super(metadata, query);
		this.push(quote(column));
		this.desc = desc;
	}
}