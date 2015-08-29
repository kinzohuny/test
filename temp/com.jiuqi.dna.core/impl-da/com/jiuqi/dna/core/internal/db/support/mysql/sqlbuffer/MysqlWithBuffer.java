package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class MysqlWithBuffer extends MysqlSelectBuffer {

	final String name;

	MysqlWithBuffer(MysqlCommandBuffer command, String name) {
		super(command);
		this.name = name;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		super.writeSelectTo(sql, args);
	}
}
