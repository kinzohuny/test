package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerWithBuffer extends SQLServerSelectBuffer {

	final String alias;

	public SQLServerWithBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String alias) {
		super(metadata, command);
		this.alias = SQLServerExprBuffer.quote(alias);
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append(this.alias);
		sql.append(" as (");
		super.writeTo(sql, args);
		sql.append(')');
	}
}