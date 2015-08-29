package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class OracleTableRefBuffer extends OracleRelationRefBuffer implements
		ISqlJoinedTableRefBuffer, ISqlJoinedWithRefBuffer {

	final String name;

	public OracleTableRefBuffer(String table, String alias) {
		super(alias);
		this.name = OracleExprBuffer.quote(table);
	}

	public OracleTableRefBuffer(String table, String alias, TableJoinType type) {
		super(alias, type);
		this.name = OracleExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.name);
	}
}
