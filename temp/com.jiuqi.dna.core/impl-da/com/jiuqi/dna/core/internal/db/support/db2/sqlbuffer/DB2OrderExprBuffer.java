package com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class DB2OrderExprBuffer extends DB2ExprBuffer {

	final boolean desc;

	DB2OrderExprBuffer(boolean desc) {
		this.desc = desc;
	}

	DB2OrderExprBuffer(String column, boolean desc) {
		this.push(quote(column));
		this.desc = desc;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		super.writeTo(sql, args);
		if (this.desc) {
			sql.append(" desc");
		}
	}
}