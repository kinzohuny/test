package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerQuerySourceBuffer extends SQLServerSourceBuffer implements
		ISqlJoinedQueryRefBuffer {

	final SQLServerSelectBuffer select;

	public SQLServerQuerySourceBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String alias) {
		super(metadata, command, alias);
		this.select = new SQLServerSelectBuffer(metadata, command);
	}

	public SQLServerQuerySourceBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String alias, TableJoinType type) {
		super(metadata, command, alias, type);
		this.select = new SQLServerSelectBuffer(metadata, command);
	}

	public ISqlSelectBuffer select() {
		return this.select;
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append('(');
		this.select.writeTo(sql, args);
		sql.append(')');
	}
}