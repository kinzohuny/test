package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.ORMAccessorImpl.RecverDeleter;
import com.jiuqi.dna.core.impl.QuRootTableRef;
import com.jiuqi.dna.core.impl.QueryColumnImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class ObjRecverDeleteSql extends
		SimpleSql<ObjRecverDeleteSql, RecverDeleter> {

	public final StructFieldDefineImpl arg_recid;

	public final ParameterPlaceholder recver = new ParameterPlaceholder();

	private static final String alias = "T";

	public ObjRecverDeleteSql(DbMetadata dbMetadata,
			MappingQueryStatementImpl statement) {
		statement.checkModifyRootOnly();
		final QuRootTableRef tableRef = (QuRootTableRef) statement.rootRelationRef();
		final TableDefineImpl table = tableRef.getTarget();
		final QueryColumnImpl recid = statement.findEqualColumn(tableRef, tableRef.getTarget().f_recid);
		if (recid == null) {
			throw Render.noRecidColumnForTable(statement, table);
		}
		this.arg_recid = recid.getMapingField();
		if (table.dbTables.size() == 1) {
			ISqlDeleteBuffer delete = dbMetadata.sqlbuffers().delete(table.primary.namedb(), alias);
			this.single(table.primary, delete, null);
			this.build(delete);
		} else {
			ISqlSegmentBuffer segment = dbMetadata.sqlbuffers().segment();
			ISqlDeleteBuffer delete = segment.delete(table.primary.namedb(), alias);
			this.single(table.primary, delete, null);
			ISqlConditionBuffer ifs = segment.ifThenElse();
			ISqlExprBuffer when = ifs.newWhen();
			when.rowcount().load(1).eq();
			ISqlSegmentBuffer then = ifs.newThen();
			for (int i = 1, c = table.dbTables.size(); i < c; i++) {
				DBTableDefineImpl dbTable = table.dbTables.get(i);
				this.single(dbTable, null, then);
			}
			this.build(segment);
		}
	}

	private final void single(DBTableDefineImpl dbTable,
			ISqlDeleteBuffer delete, ISqlSegmentBuffer segment) {
		if (delete == null) {
			delete = segment.delete(dbTable.namedb(), alias);
		}
		ISqlExprBuffer where = delete.where();
		where.loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID);
		where.loadParam(new ArgumentPlaceholder(this.arg_recid, this.arg_recid.getType()));
		where.eq();
		if (dbTable.isPrimary()) {
			where.loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECVER);
			where.loadParam(this.recver).eq();
			where.and(2);
		}
	}

	@Override
	public final RecverDeleter newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new RecverDeleter(adapter, this, notify);
	}
}