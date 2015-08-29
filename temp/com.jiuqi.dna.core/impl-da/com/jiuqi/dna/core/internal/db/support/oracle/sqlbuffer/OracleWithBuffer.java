package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class OracleWithBuffer extends OracleSelectBuffer {

	final String alias;

	public OracleWithBuffer(String alias) {
		this.alias = OracleExprBuffer.quote(alias);
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append(this.alias).append(" as (");
		super.writeTo(sql, args);
		sql.append(')');
	}
}
