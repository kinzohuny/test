package com.jiuqi.dna.core.internal.da.sql.render;

import static com.jiuqi.dna.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.GUIDType;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.ORMAccessorImpl.LpkDeleter;
import com.jiuqi.dna.core.impl.QuRootTableRef;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectIntoBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlPredicate;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class ObjByLpkDeleteSql extends
		SimpleSql<ObjByLpkDeleteSql, LpkDeleter> {

	public final ArgumentPlaceholder[] args;

	public ObjByLpkDeleteSql(DbMetadata dbMetadata,
			MappingQueryStatementImpl statement) {
		statement.validate();
		statement.checkModifyRootOnly();
		final QuRootTableRef tableRef = (QuRootTableRef) statement.rootRelationRef();
		final TableDefineImpl table = tableRef.getTarget();
		table.checkLogicalKeyAvaiable();
		final int c = table.logicalKey.items.size();
		final TableFieldDefineImpl[] keys = new TableFieldDefineImpl[c];
		this.args = new ArgumentPlaceholder[c];
		statement.setRootKeys(keys, this.args);
		final String alias = "T";
		if (table.dbTables.size() == 1) {
			final DBTableDefineImpl primary = table.primary;
			ISqlDeleteBuffer delete = dbMetadata.sqlbuffers().delete(primary.namedb(), alias);
			ISqlExprBuffer where = delete.where();
			MappingQueryStatementImpl.fillLpkWhere(where, alias, keys, this.args);
			this.build(delete);
		} else {
			final String VREC = "VRECID";
			ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
			buffer.declare(VREC, GUIDType.TYPE);
			ISqlSelectIntoBuffer si = buffer.selectInto();
			si.newTable(table.primary.namedb(), alias);
			si.newColumn(VREC).loadColumnRef(alias, FIELD_DBNAME_RECID);
			ISqlExprBuffer where = si.where();
			MappingQueryStatementImpl.fillLpkWhere(where, alias, keys, this.args);
			ISqlConditionBuffer ifs = buffer.ifThenElse();
			ifs.newWhen().loadVar(VREC).predicate(SqlPredicate.IS_NOT_NULL, 1);
			ISqlSegmentBuffer then = ifs.newThen();
			for (DBTableDefineImpl dbTable : table.dbTables) {
				final String ma = Render.rowModifyAlias(dbTable);
				ISqlDeleteBuffer delete = then.delete(dbTable.namedb(), ma);
				delete.where().loadColumnRef(ma, FIELD_DBNAME_RECID).loadVar(VREC).eq();
			}
			this.build(buffer);
		}
		// HCL ¼¶´Î±íÉ¾³ý
	}

	@Override
	public final LpkDeleter newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new LpkDeleter(adapter, this, notify);
	}
}