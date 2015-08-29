package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.DMSpExecutor;
import com.jiuqi.dna.core.internal.db.support.dm.DmMetadata;

public final class DMSpCallSql extends SpCallSql {

	public DMSpCallSql(DmMetadata metaData, StoredProcedureDefineImpl procedure) {
		super(procedure);
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append("call ");
		metaData.quoteId(sb, procedure.name);
		if (procedure.getArguments().size() > 0
				|| procedure.getResultSets() > 0) {
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
	public final DMSpExecutor newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new DMSpExecutor(adapter, this, notify);
	}

}
