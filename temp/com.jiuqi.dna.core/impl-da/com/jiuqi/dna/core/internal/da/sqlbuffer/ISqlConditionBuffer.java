package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlConditionBuffer extends ISqlBuffer {

	public ISqlExprBuffer newWhen();

	public ISqlSegmentBuffer newThen();

	public ISqlSegmentBuffer elseThen();
}
