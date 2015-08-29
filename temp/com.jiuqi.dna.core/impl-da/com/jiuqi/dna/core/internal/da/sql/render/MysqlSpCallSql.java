package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.MysqlSpExecutor;
import com.jiuqi.dna.core.internal.da.sql.execute.SpExecutor;
import com.jiuqi.dna.core.internal.db.support.mysql.MysqlMetadata;

public final class MysqlSpCallSql extends SpCallSql {

	public MysqlSpCallSql(MysqlMetadata dbMetadata,
			StoredProcedureDefineImpl procedure) {
		super(procedure);
		StringBuilder sql = new StringBuilder();
		sql.append("{call ");
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
	public final SpExecutor newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new MysqlSpExecutor(adapter, this, notify);
	}
}