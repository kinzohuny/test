package com.jiuqi.dna.core.internal.da.sql.render;

import static com.jiuqi.dna.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.ORMAccessorImpl.RecidDeleter;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.QueryColumnImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class ObjByRecidDeleteSql extends
		SimpleSql<ObjByRecidDeleteSql, RecidDeleter> {

	public final ArgumentPlaceholder recid;

	public ObjByRecidDeleteSql(DbMetadata dbMetadata,
			MappingQueryStatementImpl statement) {
		statement.validate();
		statement.checkModifyRootOnly();
		final QuTableRef tableRef = statement.rootRelationRef().castAsTableRef();
		final TableDefineImpl table = tableRef.getTarget();
		final QueryColumnImpl recid = statement.findEqualColumn(tableRef, table.f_recid);
		if (recid == null) {
			throw Render.noRecidColumnForTable(statement, table);
		}
		this.recid = new ArgumentPlaceholder(recid.getMapingField(), recid.getMapingField().getType());
		final String alias = "T";
		if (table.dbTables.size() == 1) {
			ISqlDeleteBuffer delete = dbMetadata.sqlbuffers().delete(table.primary.namedb(), alias);
			delete.where().loadColumnRef(alias, FIELD_DBNAME_RECID).loadParam(this.recid).eq();
			this.build(delete);
		} else {
			ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
			for (DBTableDefineImpl dbTable : table.dbTables) {
				ISqlDeleteBuffer delete = buffer.delete(dbTable.namedb(), alias);
				delete.where().loadColumnRef(alias, FIELD_DBNAME_RECID).loadParam(this.recid).eq();
			}
			this.build(buffer);
		}
	}

	@Override
	public final RecidDeleter newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new RecidDeleter(adapter, this, notify);
	}
}