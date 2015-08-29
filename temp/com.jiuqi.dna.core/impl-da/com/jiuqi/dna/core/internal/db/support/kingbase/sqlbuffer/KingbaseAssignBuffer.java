package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class KingbaseAssignBuffer extends KingbaseExprBuffer {
	final String var;

	public KingbaseAssignBuffer(String var) {
		this.var = var;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append(this.var).append(':').append('=');
		super.writeTo(sql, args);
		sql.append(';');
	}
}