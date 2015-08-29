package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class KingbaseSimpleBuffer extends SqlBuffer {

	static final KingbaseSimpleBuffer EXIT = new KingbaseSimpleBuffer("return");
	static final KingbaseSimpleBuffer BREAK = new KingbaseSimpleBuffer("break");

	final String keyword;

	public KingbaseSimpleBuffer(String keyword) {
		this.keyword = keyword;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append(this.keyword).append(';');
	}
}
