package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerTableSourceBuffer extends SQLServerSourceBuffer implements
		ISqlJoinedTableRefBuffer, ISqlJoinedWithRefBuffer {

	final String table;

	public SQLServerTableSourceBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String table, String alias) {
		super(metadata, command, alias);
		this.table = SQLServerExprBuffer.quote(table);
	}

	public SQLServerTableSourceBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String table, String alias,
			TableJoinType type) {
		super(metadata, command, alias, type);
		this.table = SQLServerExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.table);
	}
}
