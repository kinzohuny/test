package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlserverSpExecutor;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

public final class SqlserverSpCallSql extends SpCallSql {

	public SqlserverSpCallSql(SqlserverMetadata dbMetadata,
			StoredProcedureDefineImpl procedure) {
		super(procedure);
		StringBuilder sql = new StringBuilder();
		sql.append("{call ");
		dbMetadata.quoteId(sql, dbMetadata.beforeYukon() ? dbMetadata.user : dbMetadata.schema);
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
		sql.append('}');
		this.build(sql);
	}

	@Override
	public final SqlserverSpExecutor newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new SqlserverSpExecutor(adapter, this, notify);
	}
}