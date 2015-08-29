package com.jiuqi.dna.core.internal.da.sql.execute;

import com.jiuqi.dna.core.impl.DynObj;

public interface SqlModifier extends SqlExecutor {

	public int update(Object argValueObj);

	public boolean updateRow(Object argValueObj);

	@Deprecated
	public int update(DynObj argObj1, DynObj argObj2);
}