package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlSelectIntoBuffer extends ISqlBuffer {

	public ISqlTableRefBuffer newTable(String table, String alias);

	public ISqlQueryRefBuffer newSubquery(String alias);

	public ISqlExprBuffer newColumn(String var);

	public ISqlExprBuffer where();
}
