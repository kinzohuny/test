package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

class OracleSelectColumnBuffer extends OracleExprBuffer {
	final String alias;

	public OracleSelectColumnBuffer(String alias) {
		this.alias = OracleExprBuffer.quote(alias);
	}
}
