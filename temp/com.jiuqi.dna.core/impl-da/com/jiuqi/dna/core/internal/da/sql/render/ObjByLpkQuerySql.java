package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.ORMAccessorImpl.LpkQuerier;
import com.jiuqi.dna.core.impl.QuRootTableRef;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class ObjByLpkQuerySql extends
		SimpleSql<ObjByLpkQuerySql, LpkQuerier> {

	public ObjByLpkQuerySql(DbMetadata dbMetadata,
			MappingQueryStatementImpl statement) {
		statement.validate();
		statement.validateSingleRoot();
		final QuRootTableRef tableRef = (QuRootTableRef) statement.rootRelationRef();
		final TableDefineImpl table = tableRef.getTarget();
		table.checkLogicalKeyAvaiable();
		final int c = table.logicalKey.items.size();
		final TableFieldDefineImpl[] keys = new TableFieldDefineImpl[c];
		final ArgumentPlaceholder[] args = new ArgumentPlaceholder[c];
		statement.setRootKeys(keys, args);
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = dbMetadata.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.renderFrom(buffer.select(), usages);
		MappingQueryStatementImpl.fillLpkWhere(buffer.select().where(), statement.rootRelationRef().getName(), keys, args);
		statement.renderGroupby(buffer.select(), usages);
		statement.renderHaving(buffer.select(), usages);
		statement.renderSelect(buffer.select(), usages);
		this.build(buffer);
	}

	@Override
	public final LpkQuerier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new LpkQuerier(adapter, this, notify);
	}
}