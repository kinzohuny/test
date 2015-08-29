package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.internal.da.sql.execute.RowCountQuerier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class QueryRowCountSql extends
		SimpleSql<QueryRowCountSql, RowCountQuerier> {

	public QueryRowCountSql(DbMetadata dbMetadata, QueryStatementBase statement) {
		statement.validate();
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = dbMetadata.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		ISqlSelectBuffer nest = buffer.select().newQueryRef("N").select();
		if (statement.getDistinct() || statement.getSetOperates() != null) {
			statement.renderFullSelect(nest, usages);
		} else {
			statement.renderFrom(nest, usages);
			statement.renderWhere(nest, usages);
			statement.renderGroupby(nest, usages);
			statement.renderHaving(nest, usages);
			nest.newColumn("momo").load(1);
			statement.renderUnion(nest, usages);
		}
		buffer.select().newColumn("C").count(0, false);
		this.build(buffer);
	}

	@Override
	public final RowCountQuerier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new RowCountQuerier(adapter, this, notify);
	}
}