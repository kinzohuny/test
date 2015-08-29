package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerAssignBuffer extends SQLServerExprBuffer {

	final String var;

	public SQLServerAssignBuffer(SqlserverMetadata metadata, String var) {
		super(metadata, null);
		this.var = var;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("set ").append('@').append(this.var).append('=');
		super.writeTo(sql, args);
		sql.append(';');
	}
}