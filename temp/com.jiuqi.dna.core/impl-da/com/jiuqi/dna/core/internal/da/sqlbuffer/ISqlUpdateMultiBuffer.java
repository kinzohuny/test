package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlUpdateMultiBuffer extends ISqlCommandBuffer {

	public ISqlTableRefBuffer target();

	public ISqlExprBuffer newValue(String alias, String field);

	public ISqlExprBuffer where();

}
