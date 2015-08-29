package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.OracleSpExecutor;
import com.jiuqi.dna.core.internal.db.support.oracle.OracleMetadata;

public final class OracleSpCallSql extends SpCallSql {

	public OracleSpCallSql(OracleMetadata dbMetadata,
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
	public final OracleSpExecutor newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new OracleSpExecutor(adapter, this, notify);
	}
}