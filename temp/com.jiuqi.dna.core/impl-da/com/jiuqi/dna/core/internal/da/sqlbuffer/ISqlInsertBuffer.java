package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlInsertBuffer extends ISqlBuffer, ISqlCommandBuffer {

	public void newField(String name);

	public ISqlExprBuffer newValue();

	public ISqlSelectBuffer select();
}
