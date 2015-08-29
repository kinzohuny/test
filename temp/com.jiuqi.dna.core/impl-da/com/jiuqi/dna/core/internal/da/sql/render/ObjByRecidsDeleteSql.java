package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.ContextVariableIntl;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.ORMAccessorImpl.RecidsDeleter;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlPredicate;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class ObjByRecidsDeleteSql extends
		SimpleSql<ObjByRecidsDeleteSql, RecidsDeleter> {

	static final ParameterPlaceholder dummy = new ParameterPlaceholder();

	public ObjByRecidsDeleteSql(DbMetadata dbMetadata,
			MappingQueryStatementImpl statement) {
		statement.validate();
		statement.checkModifyRootOnly();
		final QuTableRef tableRef = statement.rootRelationRef().castAsTableRef();
		final TableDefineImpl table = tableRef.getTarget();
		if (table.dbTables.size() == 1) {
			ISqlDeleteBuffer delete = dbMetadata.sqlbuffers().delete(table.primary.namedb(), ALIAS);
			where(delete, ALIAS, table.f_recid.namedb());
			this.build(delete);
		} else {
			ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
			for (DBTableDefineImpl dbTable : table.dbTables) {
				ISqlDeleteBuffer delete = buffer.delete(dbTable.namedb(), ALIAS);
				where(delete, ALIAS, table.primary.namedb());
			}
			this.build(buffer);
		}
	}

	private static final String ALIAS = "T";

	private static final void where(ISqlDeleteBuffer delete, String tableRef,
			String field) {
		final int c = ContextVariableIntl.ORM_PER_BYIDS_DELETE;
		ISqlExprBuffer where = delete.where();
		where.loadColumnRef(tableRef, field);
		for (int i = 0; i < c; i++) {
			where.loadParam(dummy);
		}
		where.predicate(SqlPredicate.IN, c + 1);
	}

	@Override
	public final RecidsDeleter newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new RecidsDeleter(adapter, this, notify);
	}
}