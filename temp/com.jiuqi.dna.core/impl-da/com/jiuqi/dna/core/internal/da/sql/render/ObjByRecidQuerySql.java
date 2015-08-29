package com.jiuqi.dna.core.internal.da.sql.render;

import static com.jiuqi.dna.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.ORMAccessorImpl.RecidQuerier;
import com.jiuqi.dna.core.impl.QuRootTableRef;
import com.jiuqi.dna.core.impl.QueryColumnImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class ObjByRecidQuerySql extends
		SimpleSql<ObjByRecidQuerySql, RecidQuerier> {

	public ArgumentPlaceholder recid;

	public ObjByRecidQuerySql(DbMetadata dbMetadata,
			MappingQueryStatementImpl statement) {
		statement.validate();
		final QuRootTableRef tableRef = (QuRootTableRef) statement.rootRelationRef();
		final TableDefineImpl table = tableRef.getTarget();
		final QueryColumnImpl recid = statement.findColumn(tableRef, table.f_recid);
		if (recid == null) {
			throw Render.noRecidColumnForTable(statement, table);
		}
		this.recid = new ArgumentPlaceholder(recid.getMapingField(), table.f_recid.getType());
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = dbMetadata.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.renderFrom(buffer.select(), usages);
		ISqlExprBuffer where = buffer.select().where();
		where.loadColumnRef(statement.rootRelationRef().getName(), FIELD_DBNAME_RECID).loadParam(this.recid).eq();
		statement.renderGroupby(buffer.select(), usages);
		statement.renderHaving(buffer.select(), usages);
		statement.renderSelect(buffer.select(), usages);
		this.build(buffer);
	}

	@Override
	public final RecidQuerier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new RecidQuerier(adapter, this, notify);
	}
}