package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaUnionBuffer extends HanaSelectBuffer {

	final boolean unionAll;

	HanaUnionBuffer(HanaCommandBuffer command, boolean unionAll) {
		super(command);
		this.unionAll = unionAll;
	}

	@Override
	public final void writeTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		if (this.unionAll) {
			sql.append(" union ");
		} else {
			sql.append(" union all ");
		}
		if (this.unions != null) {
			sql.append('(');
			super.writeSelectTo(sql, args);
			sql.append(')');
		} else {
			super.writeSelectTo(sql, args);
		}
	}
}