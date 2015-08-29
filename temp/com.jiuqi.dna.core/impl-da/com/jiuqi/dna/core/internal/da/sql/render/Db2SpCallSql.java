package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.Db2SpExecutor;
import com.jiuqi.dna.core.internal.db.support.db2.Db2Metadata;

public final class Db2SpCallSql extends SpCallSql {

	public Db2SpCallSql(Db2Metadata dbMetadata,
			StoredProcedureDefineImpl procedure) {
		super(procedure);
		StringBuilder sql = new StringBuilder();
		sql.append("call ");
		dbMetadata.quoteId(sql, dbMetadata.user);
		sql.append('.');
		dbMetadata.quoteId(sql, procedure.name);
		final int c = procedure.getArguments().size();
		if (c > 0) {
			sql.append('(');
			ArrayList<StructFieldDefineImpl> fields = procedure.getArguments();
			for (int i = 0; i < c; i++) {
				if (i > 0) {
					sql.append(',').append(' ');
				}
				StructFieldDefineImpl f = fields.get(i);
				this.parameters.add(PlainSql.arg(f, f.getType()));
				sql.append('?');
			}
			sql.append(')');
		}
		this.build(sql);
	}

	@Override
	public final Db2SpExecutor newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new Db2SpExecutor(adapter, this, notify);
	}
}