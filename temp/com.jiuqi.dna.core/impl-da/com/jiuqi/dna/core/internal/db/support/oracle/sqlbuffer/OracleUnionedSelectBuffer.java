package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class OracleUnionedSelectBuffer extends OracleSelectBuffer {

	final boolean unionAll;

	public OracleUnionedSelectBuffer(boolean unionAll) {
		this.unionAll = unionAll;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (this.unionAll) {
			sql.append(" union ");
		} else {
			sql.append(" union all ");
		}
		if (this.union != null) {
			sql.append('(');
			super.writeTo(sql, args);
			sql.append(')');
		} else {
			super.writeTo(sql, args);
		}
	}
}