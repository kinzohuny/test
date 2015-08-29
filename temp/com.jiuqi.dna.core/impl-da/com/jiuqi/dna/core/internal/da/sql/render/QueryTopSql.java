package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.internal.da.sql.execute.TopQuerier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class QueryTopSql extends SimpleSql<QueryTopSql, TopQuerier> {

	public final ParameterPlaceholder top = new ParameterPlaceholder();

	public QueryTopSql(DbMetadata dbMetadata, QueryStatementBase statement) {
		statement.validate();
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = dbMetadata.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.renderFullSelect(buffer.select(), usages);
		buffer.limit().loadParam(this.top);
		statement.renderOrderbys(buffer, usages);
		this.build(buffer);
	}

	@Override
	public final TopQuerier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new TopQuerier(adapter, this, notify);
	}
}