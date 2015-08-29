package com.jiuqi.dna.core.internal.da.report;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlModifier;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;

public interface RptRecordSaveSql extends ModifySql {

	public SqlModifier newExecutor(DBAdapterImpl adapter, ActiveChangable notify);
}