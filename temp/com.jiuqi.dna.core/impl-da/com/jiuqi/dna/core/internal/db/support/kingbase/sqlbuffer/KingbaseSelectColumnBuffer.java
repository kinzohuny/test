package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

class KingbaseSelectColumnBuffer extends KingbaseExprBuffer {
	final String alias;

	public KingbaseSelectColumnBuffer(String alias) {
		this.alias = KingbaseExprBuffer.quote(alias);
	}
}
