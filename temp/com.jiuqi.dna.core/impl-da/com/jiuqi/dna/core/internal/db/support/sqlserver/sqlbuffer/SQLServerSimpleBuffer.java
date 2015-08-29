package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class SQLServerSimpleBuffer extends SqlBuffer {
	static final SQLServerSimpleBuffer EXIT = new SQLServerSimpleBuffer("return");
	static final SQLServerSimpleBuffer BREAK = new SQLServerSimpleBuffer("break");

	final String keyword;

	public SQLServerSimpleBuffer(String keyword) {
		this.keyword = keyword;
	}

	public int getLength() {
		return this.keyword.length() + 1;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append(this.keyword).append(';');
	}
}
