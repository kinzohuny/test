package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class MysqlQueryRefBuffer extends MysqlRelationRefBuffer implements
		ISqlJoinedQueryRefBuffer {

	final MysqlSelectBuffer select;

	MysqlQueryRefBuffer(MysqlCommandBuffer command, String alias) {
		super(command, alias);
		this.select = new MysqlSelectBuffer(command);
	}

	MysqlQueryRefBuffer(MysqlCommandBuffer command, String alias,
			TableJoinType type) {
		super(command, alias, type);
		this.select = new MysqlSelectBuffer(command);
	}

	public final MysqlSelectBuffer select() {
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
