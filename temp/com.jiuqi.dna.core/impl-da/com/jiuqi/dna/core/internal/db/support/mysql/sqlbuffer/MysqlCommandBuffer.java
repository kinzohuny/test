package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;

abstract class MysqlCommandBuffer extends SqlCommandBuffer {

	public MysqlCommandBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}
}