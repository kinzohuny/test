package com.jiuqi.dna.core.internal.da.sql.execute;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.da.sql.render.SimpleSql;

public abstract class SimpleModifySql extends
		SimpleSql<SimpleModifySql, SimpleSqlModifier> implements ModifySql {

	@Override
	public final SimpleSqlModifier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new SimpleSqlModifier(adapter, this, notify);
	}
}