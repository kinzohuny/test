package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaOrderExprBuffer extends HanaExprBuffer {

	final boolean desc;

	HanaOrderExprBuffer(HanaCommandBuffer command, boolean desc) {
		super(command);
		this.desc = desc;
	}

	HanaOrderExprBuffer(HanaCommandBuffer command, String column, boolean desc) {
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
