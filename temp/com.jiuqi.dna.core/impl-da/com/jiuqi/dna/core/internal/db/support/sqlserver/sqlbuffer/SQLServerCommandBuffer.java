package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;

abstract class SQLServerCommandBuffer extends SqlCommandBuffer {

	public SQLServerCommandBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}
}