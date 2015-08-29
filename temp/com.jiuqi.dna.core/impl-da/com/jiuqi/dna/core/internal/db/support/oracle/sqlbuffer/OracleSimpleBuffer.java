package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class OracleSimpleBuffer extends SqlBuffer {
	static final OracleSimpleBuffer EXIT = new OracleSimpleBuffer("return");
	static final OracleSimpleBuffer BREAK = new OracleSimpleBuffer("break");

	final String keyword;

	public OracleSimpleBuffer(String keyword) {
		this.keyword = keyword;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append(this.keyword).append(';');
	}
}
