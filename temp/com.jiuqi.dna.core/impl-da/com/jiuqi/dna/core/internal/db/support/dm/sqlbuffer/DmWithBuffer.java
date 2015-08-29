package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class DmWithBuffer extends DmSelectBuffer {

	final String alias;

	public DmWithBuffer(String alias) {
		this.alias = DmExprBuffer.quote(alias);
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append(this.alias).append(" as (");
		super.writeTo(sql, args);
		sql.append(')');
	}
}