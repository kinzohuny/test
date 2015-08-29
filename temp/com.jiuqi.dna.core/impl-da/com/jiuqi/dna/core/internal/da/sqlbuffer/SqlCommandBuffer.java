package com.jiuqi.dna.core.internal.da.sqlbuffer;

import java.util.List;

public abstract class SqlCommandBuffer extends SqlBuffer implements
		ISqlCommandBuffer {

	protected final ISqlSegmentBuffer scope;

	public SqlCommandBuffer(ISqlSegmentBuffer scope) {
		this.scope = scope;
	}

	public String build(List<ParameterPlaceholder> parameters) {
		SqlStringBuffer sql = new SqlStringBuffer();
		this.writeTo(sql, parameters);
		return sql.toString();
	}
}