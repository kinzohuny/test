package com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class DB2WithRefBuffer extends DB2RelationRefBuffer implements
		ISqlJoinedWithRefBuffer {

	final String with;

	DB2WithRefBuffer(String with, String alias) {
		super(alias);
		this.with = DB2ExprBuffer.quote(with);
	}

	DB2WithRefBuffer(String with, String alias, TableJoinType type) {
		super(alias, type);
		this.with = DB2ExprBuffer.quote(with);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.with);
	}
}
