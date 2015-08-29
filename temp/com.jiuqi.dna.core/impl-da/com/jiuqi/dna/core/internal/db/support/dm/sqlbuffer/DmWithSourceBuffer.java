package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class DmWithSourceBuffer extends DmSourceBuffer implements
		ISqlJoinedWithRefBuffer {

	final String with;

	DmWithSourceBuffer(String with, String alias) {
		super(alias);
		this.with = DmExprBuffer.quote(with);
	}

	DmWithSourceBuffer(String with, String alias, TableJoinType type) {
		super(alias, type);
		this.with = DmExprBuffer.quote(with);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.with);
	}
}