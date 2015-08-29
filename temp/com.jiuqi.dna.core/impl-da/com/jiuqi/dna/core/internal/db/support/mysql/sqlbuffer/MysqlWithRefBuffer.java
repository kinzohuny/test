package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class MysqlWithRefBuffer extends MysqlRelationRefBuffer implements
		ISqlJoinedWithRefBuffer {

	final MysqlWithBuffer with;

	MysqlWithRefBuffer(MysqlCommandBuffer command, String target, String alias) {
		super(command, alias);
		if (!(command instanceof MysqlQueryBuffer)) {
			throw new UnsupportedOperationException();
		}
		MysqlQueryBuffer query = (MysqlQueryBuffer) command;
		this.with = query.getWith(target);
	}

	MysqlWithRefBuffer(MysqlCommandBuffer command, String target, String alias,
			TableJoinType type) {
		super(command, alias, type);
		if (!(command instanceof MysqlQueryBuffer)) {
			throw new UnsupportedOperationException();
		}
		MysqlQueryBuffer query = (MysqlQueryBuffer) command;
		this.with = query.getWith(target);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append('(');
		this.with.writeTo(sql, args);
		sql.append(')');
	}
}