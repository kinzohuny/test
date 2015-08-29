package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlSelectBuffer extends ISqlBuffer {

	public ISqlTableRefBuffer newTableRef(String table, String alias);

	public ISqlQueryRefBuffer newQueryRef(String alias);

	public ISqlWithRefBuffer newWithRef(String target, String alias);

	public void fromDummy();

	public void distinct();

	public ISqlExprBuffer newColumn(String alias);

	public ISqlExprBuffer where();

	public ISqlExprBuffer groupby();

	public void rollup();

	public ISqlExprBuffer having();

	public ISqlSelectBuffer union(boolean all);
}