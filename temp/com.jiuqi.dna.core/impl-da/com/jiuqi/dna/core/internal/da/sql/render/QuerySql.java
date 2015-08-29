package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.internal.da.sql.execute.Querier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class QuerySql extends SimpleSql<QuerySql, Querier> {

	public QuerySql(DbMetadata dbMetadata, QueryStatementBase statement) {
		statement.validate();
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = dbMetadata.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.renderFullSelect(buffer.select(), usages);
		statement.renderOrderbys(buffer, usages);
		this.build(buffer);
	}

	@Override
	public final Querier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new Querier(adapter, this, notify);
	}
}