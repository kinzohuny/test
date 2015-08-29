package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerSelectColumnBuffer extends SQLServerExprBuffer {

	final String alias;

	public SQLServerSelectColumnBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String alias) {
		super(metadata, command);
		this.alias = SQLServerExprBuffer.quote(alias);
	}
}