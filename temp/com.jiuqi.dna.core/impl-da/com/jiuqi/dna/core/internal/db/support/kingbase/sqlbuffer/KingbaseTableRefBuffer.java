package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class KingbaseTableRefBuffer extends KingbaseRelationRefBuffer implements
		ISqlJoinedTableRefBuffer, ISqlJoinedWithRefBuffer {
	final String name;

	public KingbaseTableRefBuffer(String table, String alias) {
		super(alias);
		this.name = KingbaseExprBuffer.quote(table);
	}

	public KingbaseTableRefBuffer(String table, String alias, TableJoinType type) {
		super(alias, type);
		this.name = KingbaseExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.name);
	}
}
