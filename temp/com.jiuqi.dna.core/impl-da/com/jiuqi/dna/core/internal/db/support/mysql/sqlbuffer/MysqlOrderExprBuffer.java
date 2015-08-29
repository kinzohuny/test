package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class MysqlOrderExprBuffer extends MysqlExprBuffer {

	final boolean desc;

	MysqlOrderExprBuffer(MysqlCommandBuffer command, boolean desc) {
		super(command);
		this.desc = desc;
	}

	MysqlOrderExprBuffer(MysqlCommandBuffer command, String column, boolean desc) {
		super(command);
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
