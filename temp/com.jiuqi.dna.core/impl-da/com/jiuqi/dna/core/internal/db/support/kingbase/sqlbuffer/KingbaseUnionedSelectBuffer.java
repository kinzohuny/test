package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class KingbaseUnionedSelectBuffer extends KingbaseSelectBuffer {
	final boolean unionAll;

	public KingbaseUnionedSelectBuffer(boolean unionAll) {
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
