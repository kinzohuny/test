package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerReturnBuffer extends SQLServerExprBuffer {

	public SQLServerReturnBuffer(SqlserverMetadata metadata) {
		super(metadata, null);
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("return ");
		super.writeTo(sql, args);
		sql.append(';');
	}
}