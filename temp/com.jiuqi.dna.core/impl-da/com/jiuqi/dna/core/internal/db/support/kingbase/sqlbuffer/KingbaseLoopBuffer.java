package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class KingbaseLoopBuffer extends KingbaseSegmentBuffer implements
		ISqlLoopBuffer {
	KingbaseExprBuffer when;

	public KingbaseLoopBuffer(KingbaseSegmentBuffer scope) {
		super(scope);
	}

	public ISqlExprBuffer when() {
		if (this.when == null) {
			this.when = new KingbaseExprBuffer();
		}
		return this.when;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (this.vars != null) {
			writeDeclare(sql);
			sql.append("begin; ");
		}
		if (this.when != null) {
			sql.append("while ");
			this.when.writeTo(sql, args);
			sql.append(' ');
		}
		sql.append("loop ");
		writeStmts(sql, args);
		sql.append(" end loop;");
		if (this.vars != null) {
			sql.append("end;");
		}
	}
}
