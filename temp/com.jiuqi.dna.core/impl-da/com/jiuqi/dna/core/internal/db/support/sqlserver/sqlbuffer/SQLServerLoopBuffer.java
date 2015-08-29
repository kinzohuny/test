package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerLoopBuffer extends SQLServerSegmentBuffer implements
		ISqlLoopBuffer {

	SQLServerExprBuffer when;

	public SQLServerLoopBuffer(SqlserverMetadata metadata,
			SQLServerSegmentBuffer scope) {
		super(metadata, scope);
	}

	public ISqlExprBuffer when() {
		if (this.when == null) {
			this.when = new SQLServerExprBuffer(this.metadata, null);
		}
		return this.when;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (this.vars != null) {
			this.writeDeclare(sql);
		}
		if (this.when != null) {
			sql.append("while ");
			this.when.writeTo(sql, args);
			sql.append(' ');
		} else {
			sql.append("while 1=1 ");
		}
		if (this.stmts.size() > 1) {
			sql.append("begin ");
			this.writeStmts(sql, args);
			sql.append(" end ");
		} else {
			this.writeStmts(sql, args);
		}
	}
}