package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.internal.da.sql.execute.LimitQuerier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class QueryLimitSql extends SimpleSql<QueryLimitSql, LimitQuerier> {

	public final ParameterPlaceholder limit = new ParameterPlaceholder();
	public final ParameterPlaceholder offset = new ParameterPlaceholder();
	public final QueryStatementBase query;

	public QueryLimitSql(DbMetadata dbMetadata, QueryStatementBase statement) {
		this.query = statement;
		statement.validate();
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = dbMetadata.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.renderFullSelect(buffer.select(), usages);
		buffer.limit().loadParam(this.limit);
		buffer.offset().loadParam(this.offset);
		statement.renderOrderbys(buffer, usages);
		this.build(buffer);
	}

	@Override
	public final LimitQuerier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new LimitQuerier(adapter, this, notify);
	}
}