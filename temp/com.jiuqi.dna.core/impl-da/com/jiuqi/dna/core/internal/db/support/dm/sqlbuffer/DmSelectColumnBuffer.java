package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

final class DmSelectColumnBuffer extends DmExprBuffer {

	final String alias;

	public DmSelectColumnBuffer(String alias) {
		this.alias = DmExprBuffer.quote(alias);
	}
}