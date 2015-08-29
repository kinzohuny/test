package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlModifier;

public interface ModifySql extends ESql {

	public SqlModifier newExecutor(DBAdapterImpl adapter, ActiveChangable notify);
}