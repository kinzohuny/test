package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;

public abstract class HanaCommandBuffer extends SqlCommandBuffer {

	public HanaCommandBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}
}