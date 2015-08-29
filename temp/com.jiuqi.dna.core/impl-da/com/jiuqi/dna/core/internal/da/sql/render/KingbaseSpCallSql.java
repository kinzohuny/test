package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.KingbaseSpExecutor;
import com.jiuqi.dna.core.internal.db.support.kingbase.KingbaseMetadata;

public final class KingbaseSpCallSql extends SpCallSql {

	public KingbaseSpCallSql(KingbaseMetadata dbMetadata,
			StoredProcedureDefineImpl procedure) {
		super(procedure);
		StringBuilder sb = new StringBuilder();
		sb.append("{call ");
		dbMetadata.quoteId(sb, procedure.name);
		if (procedure.getArguments().size() > 0 || procedure.getResultSets() > 0) {
			sb.append('(');
			boolean after = false;
			for (StructFieldDefineImpl arg : procedure.getArguments()) {
				if (after) {
					sb.append(',').append(' ');
				}
				this.parameters.add(PlainSql.arg(arg, arg.getType()));
				sb.append('?');
				after = true;
			}
			for (int i = 0; i < procedure.getResultSets(); i++) {
				if (after) {
					sb.append(',').append(' ');
				}
				sb.append('?');
				after = true;
			}
			sb.append(')');
		}
		sb.append('}');
		this.build(sb);
	}

	@Override
	public final KingbaseSpExecutor newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new KingbaseSpExecutor(adapter, this, notify);
	}
}
