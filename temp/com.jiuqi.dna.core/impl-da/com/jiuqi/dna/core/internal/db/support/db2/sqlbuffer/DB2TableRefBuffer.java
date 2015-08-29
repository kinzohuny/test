package com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class DB2TableRefBuffer extends DB2RelationRefBuffer implements
		ISqlJoinedTableRefBuffer {

	final String table;

	DB2TableRefBuffer(String table, String alias) {
		super(alias);
		this.table = DB2ExprBuffer.quote(table);
	}

	DB2TableRefBuffer(String table, String alias, TableJoinType type) {
		super(alias, type);
		this.table = DB2ExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.table);
	}

}
