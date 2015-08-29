package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlExecutor;

public abstract class SimpleSql<TSql extends SimpleSql<TSql, TExecutor>, TExecutor extends SqlExecutor>
		extends PlainSql implements ESql {

	public abstract TExecutor newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify);
}