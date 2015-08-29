package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

public class SQLServerWithSourceBuffer extends SQLServerSourceBuffer implements
		ISqlJoinedWithRefBuffer {

	final SQLServerWithBuffer with;

	public SQLServerWithSourceBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String with, String alias) {
		super(metadata, command, alias);
		SQLServerQueryBuffer query = (SQLServerQueryBuffer) command;
		this.with = query.getWith(with);
	}

	public SQLServerWithSourceBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String with, String alias,
			TableJoinType type) {
		super(metadata, command, alias, type);
		SQLServerQueryBuffer query = (SQLServerQueryBuffer) command;
		this.with = query.getWith(with);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		if (this.metadata.beforeYukon()) {
			sql.append('(');
			this.with.writeSelect(sql, args);
			sql.append(')');
		} else {
			sql.append(this.with.alias);
		}
	}
}