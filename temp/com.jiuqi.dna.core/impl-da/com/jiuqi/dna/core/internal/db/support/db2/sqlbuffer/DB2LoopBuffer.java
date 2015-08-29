package com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class DB2LoopBuffer extends DB2SegmentBuffer implements ISqlLoopBuffer {

	DB2LoopBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}

	public final ISqlExprBuffer when() {
		return this.when;
	}

	final DB2ExprBuffer when = new DB2ExprBuffer();

	@Override
	public final void writeTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append("while ");
		this.when.writeTo(sql, args);
		sql.append(" do ");
		this.writeStatement(sql, args);
		sql.append("end while");
	}

}
