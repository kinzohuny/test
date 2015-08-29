package com.jiuqi.dna.core.internal.da.sqlbuffer;

import java.util.List;

public interface ISqlBuffer {

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args);
}
