package com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class DB2CursorLoopBuffer extends DB2SegmentBuffer implements
		ISqlCursorLoopBuffer {

	final DB2QueryBuffer query;
	final String cursor;
	final boolean forUpdate;

	DB2CursorLoopBuffer(DB2SegmentBuffer scope, String cursor, boolean forUpdate) {
		super(scope);
		this.cursor = cursor;
		this.forUpdate = forUpdate;
		this.query = new DB2QueryBuffer();
	}

	public final DB2QueryBuffer query() {
		return this.query;
	}

	@Override
	public final void writeTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		throw Utils.notImplemented();
	}

}
