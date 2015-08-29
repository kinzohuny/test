package com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class DB2QueryRefBuffer extends DB2RelationRefBuffer implements
		ISqlJoinedQueryRefBuffer {

	final DB2SelectBuffer select = new DB2SelectBuffer();

	DB2QueryRefBuffer(String alias) {
		super(alias);
	}

	DB2QueryRefBuffer(String alias, TableJoinType joinType) {
		super(alias, joinType);
	}

	public final DB2SelectBuffer select() {
		return this.select;
	}

	@Override
	protected final void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append('(');
		this.select.writeTo(sql, args);
		sql.append(')');
	}

}
