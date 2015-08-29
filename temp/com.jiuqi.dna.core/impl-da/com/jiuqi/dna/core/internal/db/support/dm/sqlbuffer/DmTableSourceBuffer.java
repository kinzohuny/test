package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class DmTableSourceBuffer extends DmSourceBuffer implements
		ISqlJoinedTableRefBuffer {

	final String table;

	DmTableSourceBuffer(String table, String alias) {
		super(alias);
		this.table = DmExprBuffer.quote(table);
	}

	DmTableSourceBuffer(String table, String alias, TableJoinType type) {
		super(alias, type);
		this.table = DmExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.table);
	}
}