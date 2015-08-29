package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;

abstract class DmCommandBuffer extends SqlCommandBuffer {

	public DmCommandBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}
}