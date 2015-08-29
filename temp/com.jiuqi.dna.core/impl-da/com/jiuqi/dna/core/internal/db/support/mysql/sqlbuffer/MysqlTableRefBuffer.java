package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class MysqlTableRefBuffer extends MysqlRelationRefBuffer implements
		ISqlJoinedTableRefBuffer {

	final String table;

	MysqlTableRefBuffer(MysqlCommandBuffer command, String table, String alias) {
		super(command, alias);
		this.table = MysqlExprBuffer.quote(table);
	}

	MysqlTableRefBuffer(MysqlCommandBuffer command, String table, String alias,
			TableJoinType type) {
		super(command, alias, type);
		this.table = MysqlExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.table);
	}

}
