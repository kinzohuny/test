package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SpExecutor;

public abstract class SpCallSql extends SimpleSql<SpCallSql, SpExecutor> {

	public final StoredProcedureDefineImpl procedure;

	public SpCallSql(StoredProcedureDefineImpl procedure) {
		this.procedure = procedure;
	}

	@Override
	public abstract SpExecutor newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify);
}