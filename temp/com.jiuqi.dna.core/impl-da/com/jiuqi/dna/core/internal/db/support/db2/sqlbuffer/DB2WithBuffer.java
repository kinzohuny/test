package com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class DB2WithBuffer extends DB2SelectBuffer {

	final String name;

	DB2WithBuffer(String name) {
		this.name = DB2ExprBuffer.quote(name);
	}

	@Override
	public final void writeTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.name).append(" as (");
		super.writeFullSelectTo(sql, args);
		sql.append(')');
	}
}
