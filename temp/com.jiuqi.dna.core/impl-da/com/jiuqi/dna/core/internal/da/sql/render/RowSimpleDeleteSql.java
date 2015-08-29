package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.QuRelationRef;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.QueryColumnImpl;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class RowSimpleDeleteSql extends SimpleModifySql {

	public RowSimpleDeleteSql(DbMetadata dbMetadata,
			QueryStatementBase statement) {
		statement.validate();
		final LinkedHashMap<DBTableDefineImpl, QueryColumnImpl> deletes = new LinkedHashMap<DBTableDefineImpl, QueryColumnImpl>();
		for (QuRelationRef relationRef : statement.rootRelationRef()) {
			if (!statement.supportModify(relationRef)) {
				continue;
			}
			final QuTableRef tableRef = (QuTableRef) relationRef;
			final TableDefineImpl table = tableRef.getTarget();
			final QueryColumnImpl recid = statement.findEqualColumn(tableRef, table.f_recid);
			if (recid == null) {
				throw Render.noRecidColumnForTable(statement, table);
			}
			for (DBTableDefineImpl dbTable : table.dbTables) {
				if (deletes.put(dbTable, recid) != null) {
					throw Render.duplicateModifyTable(statement, table);
				}
			}
		}
		if (deletes.size() == 0) {
			throw Render.modifyTableNotSupport(statement);
		} else if (deletes.size() == 1) {
			final Entry<DBTableDefineImpl, QueryColumnImpl> e = deletes.entrySet().iterator().next();
			final DBTableDefineImpl dbTable = e.getKey();
			ISqlDeleteBuffer delete = dbMetadata.sqlbuffers().delete(dbTable.namedb(), alias);
			delete(dbTable, e.getValue(), delete, null);
			this.build(delete);
		} else {
			ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
			for (Entry<DBTableDefineImpl, QueryColumnImpl> e : deletes.entrySet()) {
				DBTableDefineImpl dbTable = e.getKey();
				delete(dbTable, e.getValue(), null, buffer);
			}
			this.build(buffer);
		}
	}

	private static final String alias = "T";

	private static final void delete(DBTableDefineImpl dbTable,
			QueryColumnImpl recid, ISqlDeleteBuffer delete,
			ISqlSegmentBuffer segment) {
		if (delete == null) {
			delete = segment.delete(dbTable.namedb(), alias);
		}
		ISqlExprBuffer where = delete.where();
		where.loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID);
		where.loadParam(new ArgumentPlaceholder(recid.getMapingField(), dbTable.owner.f_recid.getType()));
		where.eq();
	}
}