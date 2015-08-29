package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class OraclePrintBuffer extends OracleExprBuffer {
	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("DBMS_OUTPUT.PUT_LINE(");
		super.writeTo(sql, args);
		sql.append(')').append(';');
	}
}
