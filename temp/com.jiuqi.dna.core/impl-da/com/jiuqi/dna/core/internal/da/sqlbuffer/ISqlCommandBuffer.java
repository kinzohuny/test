package com.jiuqi.dna.core.internal.da.sqlbuffer;

import java.util.List;

public interface ISqlCommandBuffer {

	public String build(List<ParameterPlaceholder> reservers);
}
