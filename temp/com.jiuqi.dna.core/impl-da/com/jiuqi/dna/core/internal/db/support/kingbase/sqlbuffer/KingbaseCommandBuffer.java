package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;

abstract class KingbaseCommandBuffer extends SqlCommandBuffer {

	public KingbaseCommandBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}
}
